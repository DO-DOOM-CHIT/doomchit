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
                    m.setAlbum_title(album);
                    m.setImage(image);
                    return musicRepository.save(m);
                });
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

    // 상세 정보 크롤링 (곡 상세 + 앨범 상세)
    private void fillSongAndAlbumDetail(Music music) {
        try {
            // 헤더 설정 (차단 우회용 - 리얼 브라우저처럼 보이기)
            String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36";

            // 1. 곡 상세 페이지 크롤링
            if (music.getMusicId() != null) {
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
                    String dateStr = dateKey.nextElementSibling().text();
                    try {
                        music.setRelDate(LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy.MM.dd")));
                    } catch (Exception e) {
                    }
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
            }

            // 2. 앨범 상세 페이지 크롤링
            if (music.getAlbumId() != null) {
                String albumUrl = ALBUM_DETAIL_URL + music.getAlbumId();
                Document albumDoc = Jsoup.connect(albumUrl)
                        .userAgent(userAgent)
                        .header("Accept",
                                "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                        .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                        .header("Referer", "https://www.melon.com/chart/index.htm")
                        .timeout(10000)
                        .ignoreHttpErrors(true)
                        .get();

                // 발매사
                Element pubKey = albumDoc.selectFirst("dl.list dt:contains(발매사)");
                if (pubKey != null)
                    music.setPublisher(pubKey.nextElementSibling().text());

                // 기획사
                Element agencyKey = albumDoc.selectFirst("dl.list dt:contains(기획사)");
                if (agencyKey != null)
                    music.setAgency(agencyKey.nextElementSibling().text());
            }

        } catch (Exception e) {
            // 크롤링 실패해도 무시
        }
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

                music.setAlbum_title(song.path("ALBUMNAME").asText());
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