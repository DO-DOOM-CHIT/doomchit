package com.mysite.doomchit.musics;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MusicService {

    // 멜론 모바일 차트 API 정보
    private static final String APP_VERSION = "6.5.8.1";
    private static final String CP_ID = "AS40";
    private static final String USER_AGENT = CP_ID + "; Android 13; " + APP_VERSION + "; sdk_gphone64_arm64";
    private static final String CHART_API_URL = "https://m2.melon.com/m6/chart/ent/songChartList.json?cpId=" + CP_ID
            + "&cpKey=14LNC3&appVer=" + APP_VERSION;

    // 멜론 웹 상세 페이지 URL
    private static final String SONG_DETAIL_URL = "https://www.melon.com/song/detail.htm?songId=";
    private static final String ALBUM_DETAIL_URL = "https://www.melon.com/album/detail.htm?albumId=";

    @Autowired
    private MusicRepository musicRepository;

    private final WebClient webClient = WebClient.builder().build();

    // DB에서 음악 찾기, 없으면 저장
    public Music getOrCreateMusic(String title, String artist, String album, String image) {
        return musicRepository.findByTitleAndArtist(title, artist)
                .orElseGet(() -> {
                    Music m = new Music();
                    m.setTitle(title);
                    m.setArtist(artist);
                    m.setAlbumTitle(album);
                    m.setImage(image);
                    return musicRepository.save(m);
                });
    }

    public Music getMusic(Long mno) {
        return musicRepository.findById(mno)
                .orElseThrow(() -> new IllegalArgumentException("음악을 찾을 수 없습니다."));
    }

    public Music getOrCreateMusicByMusicId(Long musicId, String title, String artist, String image) {
        // 1. DB 확인
        return musicRepository.findByMusicId(musicId)
                .map(m -> {
                    boolean needUpdate = false;
                    // 기존 데이터 화질 개선 (Regex 이용)
                    if (m.getImage() != null && m.getImage().contains("/resize/")) {
                        String newImage = m.getImage().replaceAll("/resize/\\d+", "/resize/500");
                        if (!newImage.equals(m.getImage())) {
                            m.setImage(newImage);
                            needUpdate = true;
                        }
                    }

                    if (m.getDuration() == null || m.getDuration() == 0 || m.getLyrics() == null
                            || m.getLyrics().isEmpty() || m.getLyrics().split("\n").length < 5) {
                        fillSongAndAlbumDetail(m);
                        needUpdate = true;
                    }
                    if (needUpdate) {
                        return musicRepository.save(m);
                    }

                    return m;
                })
                .orElseGet(() -> {
                    // 2. 파라미터가 있으면 바로 생성 (검색 결과 클릭 시)
                    if (title != null && !title.isEmpty()) {
                        Music m = new Music();
                        m.setMusicId(musicId);
                        m.setTitle(title);
                        m.setArtist(artist);
                        // 고화질 이미지로 변환 (Regex)
                        m.setImage(image.replaceAll("/resize/\\d+", "/resize/500"));
                        m.setDuration(0); // DB Not Null 제약조건 대응
                        // m.setCreDate(java.time.LocalDateTime.now()); // Entity에 필드 없음

                        fillSongAndAlbumDetail(m); // 상세 정보 크롤링
                        return musicRepository.save(m);
                    }

                    // 3. 파라미터 없으면 차트 API에서 탐색 (기존 로직)
                    List<Music> chart = getMelonChartBasic();
                    for (Music m : chart) {
                        if (m.getMusicId().equals(musicId)) {
                            fillSongAndAlbumDetail(m);
                            return musicRepository.save(m);
                        }
                    }
                    throw new IllegalArgumentException("차트에서 데이터를 찾을 수 없습니다: " + musicId);
                });
    }

    // DB에서 단순히 찾기 (없으면 null)
    public Music findMusicByMusicId(Long musicId) {
        return musicRepository.findByMusicId(musicId).orElse(null);
    }

    // 1. 메인 페이지용: 가볍고 빠른 기본 차트 (제목, 가수, 앨범 등 목록 API 정보만)
    public List<Music> getMelonChartBasic() {
        try {
            System.out.println("DEBUG: Fetching Melon Chart Basic...");

            String response = webClient.get()
                    .uri(CHART_API_URL)
                    .header("User-Agent", USER_AGENT)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseMelonJson(response);

        } catch (Exception e) {
            System.err.println("Melon API Error: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    // 2. 리뷰 페이지용: 모든 상세 정보가 포함된 차트 (크롤링 포함, 느리지만 정보 완벽)
    public List<Music> getMelonChartDetailed() {
        List<Music> musicList = getMelonChartBasic();

        System.out.println("DEBUG: Fetching Song & Album Details in Parallel...");
        musicList.parallelStream().forEach(this::fillSongAndAlbumDetail);

        return musicList;
    }

    // 3. 검색어 자동완성 (Melon API 프록시)
    public List<java.util.Map<String, Object>> searchMelon(String keyword) {
        List<java.util.Map<String, Object>> results = new ArrayList<>();
        try {
            // jscallback 파라미터 제외하고 요청
            String searchUrl = "https://www.melon.com/search/keyword/index.json?query="
                    + java.net.URLEncoder.encode(keyword, "UTF-8");

            String response = webClient.get()
                    .uri(searchUrl)
                    .header("User-Agent", USER_AGENT)
                    .header("Referer", "https://www.melon.com/")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // JSONP Wrapper 제거 (괄호 제거)
            if (response != null) {
                int start = response.indexOf("{");
                int end = response.lastIndexOf("}");
                if (start != -1 && end != -1) {
                    response = response.substring(start, end + 1);
                }
            }

            // JSON parsing
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            /*
             * 아티스트 정보 제외 (곡 정보만 표시)
             * // 1) 아티스트 (ARTISTCONTENTS)
             * if (root.has("ARTISTCONTENTS")) {
             * for (JsonNode node : root.get("ARTISTCONTENTS")) {
             * java.util.Map<String, Object> item = new java.util.HashMap<>();
             * item.put("type", "artist");
             * item.put("name", node.path("ARTISTNAME").asText());
             * item.put("detail", node.path("NATIONALITYNAME").asText() + "/" +
             * node.path("SEXNAME").asText() + "/"
             * + node.path("ACTTYPENAME").asText());
             * item.put("image", node.path("ARTISTIMG").asText().replace("/120", "/64")); //
             * 작은 이미지
             * results.add(item);
             * }
             * }
             */

            // 2) 곡 (SONGCONTENTS)
            if (root.has("SONGCONTENTS")) {
                for (JsonNode node : root.get("SONGCONTENTS")) {
                    java.util.Map<String, Object> item = new java.util.HashMap<>();
                    item.put("type", "song");
                    item.put("id", node.path("SONGID").asText());
                    item.put("name", node.path("SONGNAME").asText());
                    item.put("detail", node.path("ARTISTNAME").asText());
                    item.put("image", node.path("ALBUMIMG").asText().replaceAll("/resize/\\d+", "/resize/500"));
                    results.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    // 상세 정보 크롤링 (곡 상세 + 앨범 상세)
    private void fillSongAndAlbumDetail(Music music) {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36";

        // 1. 곡 상세 페이지 크롤링
        if (music.getMusicId() != null) {
            try {
                String songUrl = SONG_DETAIL_URL + music.getMusicId();
                Document songDoc = Jsoup.connect(songUrl)
                        .userAgent(userAgent)
                        .header("Accept",
                                "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                        .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                        .header("Referer", "https://www.melon.com/chart/index.htm")
                        .timeout(10000)
                        .ignoreHttpErrors(true)
                        .get();

                // 장르 추출
                Element genreKey = songDoc.selectFirst("dl.list dt:contains(장르)");
                if (genreKey != null) {
                    music.setGenre(genreKey.nextElementSibling().text());
                }

                // 발매일 추출
                Element dateKey = songDoc.selectFirst("dl.list dt:contains(발매일)");
                if (dateKey != null) {
                    try {
                        music.setRelDate(LocalDate.parse(dateKey.nextElementSibling().text(),
                                DateTimeFormatter.ofPattern("yyyy.MM.dd")));
                    } catch (Exception e) {
                    }
                }

                // 앨범 ID 추출 (재생시간 확보용 - 곡 상세에는 재생시간이 없음)
                Element albumLink = songDoc.selectFirst("a[href*='goAlbumDetail']");
                if (albumLink != null) {
                    String href = albumLink.attr("href"); // javascript:melon.link.goAlbumDetail('10554246');
                    String albumIdStr = href.replaceAll("[^0-9]", "");
                    if (!albumIdStr.isEmpty()) {
                        music.setAlbumId(Long.parseLong(albumIdStr));
                    }
                }

                // 가사 추출
                Element lyricDiv = songDoc.selectFirst("div.lyric");
                if (lyricDiv != null) {
                    String html = lyricDiv.html();
                    // 1. BR 태그를 줄바꿈으로 변환
                    html = html.replaceAll("(?i)<br\\s*/?>", "\n");
                    // 2. 나머지 태그 제거
                    html = html.replaceAll("<[^>]+>", "");
                    // 3. HTML 엔티티 디코딩
                    html = org.jsoup.parser.Parser.unescapeEntities(html, true);

                    // 4. 과도한 줄바꿈 제거 (연속된 줄바꿈을 하나로)
                    String text = html.replaceAll("(\\r\\n|\\r|\\n)+", "\n").trim();

                    if (text.contains("[가사 준비중]")) {
                        text = "가사 준비중입니다.";
                    }
                    music.setLyrics(text);
                }

                // 작사/작곡 추출
                Elements personList = songDoc.select("ul.list_person li");
                for (Element li : personList) {
                    String updateText = li.text();
                    if (updateText.contains("작사")) {
                        Elements names = li.select(".artist_name");
                        if (!names.isEmpty())
                            music.setLyricist(String.join(", ", names.eachText()));
                    }
                    if (updateText.contains("작곡")) {
                        Elements names = li.select(".artist_name");
                        if (!names.isEmpty())
                            music.setComposer(String.join(", ", names.eachText()));
                    }
                }
            } catch (Exception e) {
                // Ignore
            }
        }

        // 2. 앨범 상세 페이지 크롤링 (앨범 정보 + 재생시간 확보)
        if (music.getAlbumId() != null) {
            try {
                String albumUrl = ALBUM_DETAIL_URL + music.getAlbumId();
                Document albumDoc = Jsoup.connect(albumUrl)
                        .userAgent(userAgent)
                        .get();

                // 발매사
                Element pubKey = albumDoc.selectFirst("dl.list dt:contains(발매사)");
                if (pubKey != null)
                    music.setPublisher(pubKey.nextElementSibling().text());

                // 기획사
                Element agencyKey = albumDoc.selectFirst("dl.list dt:contains(기획사)");
                if (agencyKey != null)
                    music.setAgency(agencyKey.nextElementSibling().text());

                // 재생시간 추출 (트랙 리스트에서 검색)
                // form#frm table tbody tr
                Elements rows = albumDoc.select("form#frm table tbody tr");
                for (Element row : rows) {
                    // 이 행이 현재 musicId인지 확인
                    Element input = row.selectFirst("input[name='input_check']");
                    if (input != null && input.val().equals(String.valueOf(music.getMusicId()))) {
                        // 찾았다. 시간 추출. 보통 마지막 쪽에 위치.
                        Elements cols = row.select("td");
                        for (Element col : cols) {
                            String txt = col.text().trim();
                            if (txt.contains(":") && txt.length() < 10) {
                                String[] parts = txt.split(":");
                                if (parts.length == 2) {
                                    try {
                                        int min = Integer.parseInt(parts[0]);
                                        int sec = Integer.parseInt(parts[1]);
                                        music.setDuration(min * 60 + sec);
                                    } catch (Exception ex) {
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    // 앨범 수록곡 가져오기
    public List<Music> getAlbumTracklist(Long albumId) {
        List<Music> tracks = new ArrayList<>();
        try {
            String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36";
            String albumUrl = ALBUM_DETAIL_URL + albumId;

            Document doc = Jsoup.connect(albumUrl)
                    .userAgent(userAgent)
                    .header("Accept",
                            "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                    .timeout(10000)
                    .get();

            // 수록곡 테이블 파싱
            Elements rows = doc.select("form#frm table tbody tr");
            for (Element row : rows) {
                try {
                    // 1. 곡 제목
                    Element titleElem = row.selectFirst(".wrap_song_info .ellipsis.rank01 a");
                    if (titleElem == null)
                        continue;
                    String title = titleElem.text();

                    // 2. 가수
                    Element artistElem = row.selectFirst(".wrap_song_info .ellipsis.rank02 a");
                    String artist = (artistElem != null) ? artistElem.text() : "Unknown";

                    // 3. Music ID (href="javascript:melon.play.playSong('100000', '36599950');")
                    // 보통 두 번째 파라미터가 songId
                    String href = titleElem.attr("href");
                    Long musicId = 0L;
                    if (href.contains("playSong")) {
                        String[] parts = href.split("'");
                        if (parts.length >= 4) {
                            musicId = Long.parseLong(parts[3]);
                        }
                    }

                    Music track = new Music();
                    track.setTitle(title);
                    track.setArtist(artist);
                    track.setMusicId(musicId);
                    track.setAlbumId(albumId); // 같은 앨범

                    tracks.add(track);
                } catch (Exception e) {
                    // 개별 곡 파싱 실패 시 패스
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tracks;
    }

    // JSON 파싱 로직 (목록 API)
    private List<Music> parseMelonJson(String json) {
        List<Music> musicList = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode songList = root.path("response").path("SONGLIST");

            for (int i = 0; i < songList.size(); i++) {
                JsonNode song = songList.get(i);
                Music music = new Music();

                music.setTitle(song.path("SONGNAME").asText());

                JsonNode artistList = song.path("ARTISTLIST");
                if (artistList.size() > 0) {
                    music.setArtist(artistList.get(0).path("ARTISTNAME").asText());
                } else {
                    music.setArtist("Unknown");
                }

                music.setAlbumTitle(song.path("ALBUMNAME").asText());
                String imgUrl = song.path("ALBUMIMG").asText();
                if (imgUrl != null && !imgUrl.isEmpty()) {
                    music.setImage(imgUrl);
                } else {
                    music.setImage("");
                }

                music.setMusicId(song.path("SONGID").asLong());
                music.setAlbumId(song.path("ALBUMID").asLong());

                // 기본 정보
                music.setDuration(song.path("PLAYTIME").asInt(0));

                JsonNode genreList = song.path("GENRELIST");
                if (genreList.size() > 0) {
                    music.setGenre(genreList.get(0).asText());
                }

                try {
                    String dateStr = song.path("ISSUEDATE").asText();
                    if (dateStr != null && dateStr.length() == 8) {
                        int year = Integer.parseInt(dateStr.substring(0, 4));
                        int month = Integer.parseInt(dateStr.substring(4, 6));
                        int day = Integer.parseInt(dateStr.substring(6, 8));
                        music.setRelDate(LocalDate.of(year, month, day));
                    }
                } catch (Exception e) {
                }

                music.setRank(song.path("CURRANK").asInt());

                musicList.add(music);
            }
        } catch (Exception e) {
            System.err.println("Parsing Error: " + e.getMessage());
        }
        return musicList;
    }

    public String getRawMelonChartJson() {
        try {
            return webClient.get()
                    .uri(CHART_API_URL)
                    .header("User-Agent", USER_AGENT)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}