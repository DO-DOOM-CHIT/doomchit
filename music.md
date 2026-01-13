# MusicService.java 완전 정복 (Line 1 ~ 474 전체 해설)

이 문서는 `MusicService.java` 파일의 모든 라인을 빠짐없이 분석합니다. 왼쪽의 라인 번호를 보면서 코드를 따라가세요.

---

### [Line 1 ~ 18] 패키지 선언 및 라이브러리 임포트
어떤 도구들을 가져와서 쓸 준비를 하는지 보여줍니다.

*   **Line 1:** `package com.mysite.doomchit.musics;`
    *   이 파일이 `musics`라는 폴더(패키지) 안에 소속되어 있음을 선언합니다.
*   **Line 3-6:** 자바 기본 도구들
    *   `LocalDate`, `DateTimeFormatter`: 날짜(발매일)를 다루기 위해 가져옴.
    *   `ArrayList`, `List`: 목록(차트 100개)을 담을 그릇을 가져옴.
*   **Line 8-11:** **[핵심] Jsoup 관련**
    *   `Jsoup`, `Document`, `Element`, `Elements`: 웹사이트 HTML을 긁어와서 태그별로 자르고 뜯어보는 크롤링 도구들입니다.
*   **Line 12-14:** 스프링(Spring) 관련
    *   `Autowired`: 의존성 주입(DI). "DB 저장소 좀 갖다 줘"라고 부탁하는 데 씁니다.
    *   `Service`: "이 클래스는 비즈니스 로직(일하는 놈)이야"라고 스프링에게 알려주는 명찰.
    *   `WebClient`: **[핵심]** 멜론 서버에 API 요청을 보낼 때 쓰는 최신 통신 도구입니다.
*   **Line 16-17:** Jackson 라이브러리
    *   `JsonNode`, `ObjectMapper`: 멜론이 보내준 JSON 데이터(글자)를 자바 객체로 변환할 때 씁니다.

---

### [Line 19 ~ 37] 클래스 설정 및 전역 변수 (가장 중요한 설정값)
서버를 속이기 위한 정보와 연결 도구들을 준비하는 곳입니다.

*   **Line 19:** `@Service`
    *   스프링 부트가 시작될 때 이 클래스를 메모리에 띄워서 일꾼으로 등록합니다.
*   **Line 20:** `public class MusicService {`
    *   클래스 시작.
*   **Line 23:** `private static final String APP_VERSION = "6.5.8.1";`
    *   우리가 흉내 낼 멜론 앱의 버전입니다. 너무 옛날 버전을 쓰면 서버가 업데이트하라고 에러를 뱉을 수 있어서 최신 버전으로 맞춰줬습니다.
*   **Line 24:** `private static final String CP_ID = "AS40";`
    *   통신사/제휴사 코드입니다. "SKT 안드로이드 폰" 같은 식별자 역할을 합니다.
*   **Line 25:** `USER_AGENT` **[매우 중요]**
    *   `CP_ID + "; Android 13; " + APP_VERSION + "; sdk_gphone64_arm64"`
    *   **역할:** 가짜 신분증. 이 문자열을 헤더에 실어 보내면, 멜론 서버는 "아, 이거 안드로이드 13 폰이구나"라고 속습니다.
*   **Line 26-27:** `CHART_API_URL` **[매우 중요]**
    *   `https://m2.melon.com/m6/chart/ent/songChartList.json...`
    *   **역할:** 일반 웹 브라우저용 주소가 아니라, **모바일 앱이 데이터를 받아가는 비밀 뒷문(API)** 주소입니다.
    *   `cpKey=14LNC3`: 이게 없으면 "인증되지 않은 접근"이라며 튕겨냅니다. 앱 내부를 뜯어봐야 알 수 있는 키값입니다.
*   **Line 30-31:** 크롤링용 주소
    *   `SONG_DETAIL_URL`, `ALBUM_DETAIL_URL`: 나중에 ID만 붙여서 실제 웹페이지에 접속하기 위한 기본 주소(Prefix)입니다.
*   **Line 33-34:** `musicRepository` 연결
    *   DB에 데이터를 넣고 빼기 위해 `MusicRepository`를 주입받습니다.
*   **Line 36:** `WebClient webClient`
    *   통신 도구를 미리 조립해서 만들어둡니다. 매번 새로 만들면 비효율적이니까요.

---

### [Line 38 ~ 49] getOrCreateMusic (기본 데이터 저장)
단순 정보를 DB에 저장하거나 가져오는 메소드입니다.

*   **Line 39:** 파라미터로 제목(title), 가수(artist), 앨범(album), 이미지(image)를 받습니다.
*   **Line 40:** `musicRepository.findByTitleAndArtist(...)`
    *   DB에 "이 제목에 이 가수 노래 이미 있어?" 하고 물어봅니다.
*   **Line 41:** `.orElseGet(() -> { ... })`
    *   **만약 없으면(else),** 중괄호 안의 내용을 실행합니다.
*   **Line 42-47:** 새 `Music` 객체를 만들어서 파라미터로 받은 값들을 채워 넣고 `save`(저장) 한 뒤 그 결과를 리턴합니다.
*   **요약:** "있으면 그거 쓰고, 없으면 새로 만들어서 써."

---

### [Line 51 ~ 54] getMusic (단순 조회)
*   **Line 51:** 음악 번호(mno, 우리 DB의 PK)로 조회를 시도합니다.
*   **Line 53:** 없으면 "음악을 찾을 수 없습니다"라고 에러(테러)를 냅니다.

---

### [Line 56 ~ 107] getOrCreateMusicByMusicId **(핵심 로직 1)**
이 서비스의 심장입니다. **"무조건 데이터를 되살려내거나 만들어내는 좀비 같은 함수"**입니다.

*   **Line 56:** 파라미터로 `musicId`(멜론 고유번호)와 기본 정보를 받습니다.
*   **Line 58:** `musicRepository.findByMusicId(musicId)`
    *   멜론 ID로 DB를 먼저 뒤집니다.
    
    **(상황 1: DB에 있을 때 - Line 59~80)**
    *   **Line 60:** `boolean needUpdate = false;` (수정할 게 있는지 체크하는 스위치)
    *   **Line 62-68 (이미지 화질 업글):**
        *   이미지 주소에 `/resize/`가 있으면 숫자를 500으로 바꿔서 고화질로 만듭니다.
        *   만약 주소가 바뀌었다면 `needUpdate = true`로 스위치를 켭니다.
    *   **Line 70-74 (데이터 누실 복구):**
        *   재생시간이 0이거나, 가사가 없거나, 가사가 5줄 미만(너무 짧다 = 뭔가 잘못됨)이면?
        *   `fillSongAndAlbumDetail(m)`을 호출해 **웹 크롤링을 다시 시도**합니다.
    *   **Line 75-77:** 업데이트된 게 있으면 `save`로 DB에 반영합니다.

    **(상황 2: DB에 없을 때 - Line 81~106)**
    *   **Line 83:** 만약 파라미터로 제목(`title`)이 같이 들어왔다면? (검색 결과 클릭 시)
        *   **Line 84-90:** 새 놈 만들고 기본 정보(제목, 가수) 채우고 이미지 화질 개선합니다.
        *   **Line 93:** `fillSongAndAlbumDetail(m)` -> **크롤링하러 출동!** (가사 가져와!)
        *   **Line 94:** 저장하고 끝.
    *   **Line 97:** 제목도 없이 ID만 딸랑 왔다? (보통 차트 새로고침 할 때)
        *   **Line 98:** `getMelonChartBasic()` -> **멜론 차트 100위 목록을 API로 싹 긁어옵니다.**
        *   **Line 99-104:** 100개 중에서 지금 찾으려는 `musicId`랑 똑같은 놈을 루프 돌며 찾습니다.
        *   찾으면 걔 데리고 크롤링(`fillSongAndAlbumDetail`)해서 저장합니다.

---

### [Line 115 ~ 133] getMelonChartBasic (API 통신)
멜론 서버에 "차트 내놔"라고 말하는 부분입니다.

*   **Line 119:** `webClient.get()` -> HTTP GET 요청 준비.
*   **Line 120:** `.uri(CHART_API_URL)` -> 아까 준비한 비밀 주소로.
*   **Line 121:** `.header("User-Agent", USER_AGENT)` -> **[중요]** "저 안드로이드 폰입니다"라며 위조 신분증 제시.
*   **Line 123:** `.bodyToMono(String.class)` -> 응답 내용을 문자열(String) 한 덩어리로 받겠다.
*   **Line 124:** `.block()` -> 받을 때까지 멈춰! (데이터 다 받을 때까지 기다림)
*   **Line 126:** `parseMelonJson(response)` -> 받아온 외계어(JSON)를 해석하러 보냄.

---

### [Line 136 ~ 143] getMelonChartDetailed
*   **Line 137:** 일단 기본 차트(100개)를 받아옵니다.
*   **Line 140:** `musicList.parallelStream().forEach(...)`
    *   **병렬 처리(Parallel):** 100개를 하나씩 순서대로 하면 너무 느리니까, 컴퓨터의 여러 코어를 동시에 써서 다구리(?)로 크롤링(`fillSongAndAlbumDetail`)을 돌립니다. 속도가 훨씬 빠릅니다.

---

### [Line 146 ~ 208] searchMelon (검색 기능)
*   **Line 150-151:** 검색어를 URL에 넣을 수 있게 인코딩(한글 -> `%EB%...`)해서 검색 주소를 만듭니다.
*   **Line 153-159:** 이번엔 `searchUrl`로 요청을 보냅니다. 여기서도 `.header("Referer", ...)`를 넣어 "나 멜론 홈페이지에서 검색하는 거야"라고 속입니다.
*   **Line 161-168 (JSONP 처리):**
    *   멜론 검색 API는 특이하게 괄호 `(...)`로 감싸진 데이터를 줍니다.
    *   `indexOf("{")`, `lastIndexOf("}")`: 제일 앞에 있는 `{`와 제일 뒤에 있는 `}`를 찾아서 그 사이의 글자만 잘라냅니다(Substring). 그래야 순수 JSON이 되니까요.
*   **Line 171-172:** Jackson `ObjectMapper`로 JSON 트리를 만듭니다.
*   **Line 193-202:** `SONGCONTENTS`라는 항목(노래 검색 결과)이 있으면 루프를 돌면서 제목, 가수, 이미지를 뽑아서 `results` 리스트에 담습니다.

---

### [Line 211 ~ 341] fillSongAndAlbumDetail **(핵심 로직 2 - 크롤링)**
웹페이지에 직접 들어가서 글자를 긁어오는 노가다꾼입니다.

*   **Line 212:** `userAgent` 변수를 이번엔 **"Chrome 브라우저"** 정보로 바꿉니다. 모바일 앱인 척하다가 여기서는 **PC 사용자인 척 연기**합니다. (안 그러면 웹페이지가 안 열림)

**[Part 1: 노래 상세 페이지 (Line 215 ~ 292)]**
*   **Line 218:** `Jsoup.connect(songUrl)...get()` -> 해당 노래의 웹페이지 소스코드(HTML)를 통째로 가져옵니다.
*   **Line 229-232:** `dl.list dt:contains(장르)` -> "장르"라는 글자가 있는 칸을 찾아서 그 옆 칸(nextElementSibling) 내용을 가져옵니다.
*   **Line 235-242:** "발매일"을 찾아서 `LocalDate` 형식으로 변환합니다. 에러 나면 그냥 무시(catch)합니다.
*   **Line 255 (가사 추출):** `div.lyric` -> 가사가 들어있는 `div` 박스를 찾습니다.
    *   **Line 259:** `<br>`(줄바꿈 태그)를 `\n`(엔터 문자)으로 바꿉니다.
    *   **Line 261:** 나머지 HTML 태그를 모두 삭제합니다.
    *   **Line 263:** `&gt;` 같은 특수문자를 다시 원래 문자로 되돌립니다.
    *   **Line 266:** `trim()`으로 앞뒤 공백을 자릅니다.
*   **Line 275-288:** 작사가/작곡가 정보를 리스트(`ul.list_person`)에서 하나씩 꺼내서 찾습니다.

**[Part 2: 앨범 상세 페이지 (Line 295 ~ 340)]**
*   **Line 298:** 노래 상세 페이지에는 "재생 시간"이 없어서 앨범 페이지로 또 이동합니다.
*   **Line 315:** 앨범에 수록된 곡 리스트(`table` 행)를 하나씩 검사합니다.
*   **Line 317-318:** "지금 내가 찾고 있는 노래(`musicId`)가 이 줄에 있나?" 확인합니다.
*   **Line 323-332:** 맞다면 그 줄에서 `분:초` 형식의 텍스트(예: "3:40")를 찾아서 초 단위(`3*60 + 40`)로 계산해 넣습니다.

---

### [Line 344 ~ 399] getAlbumTracklist (앨범 수록곡 가져오기)
*   **Line 348:** 앨범 상세 페이지 URL로 접속합니다.
*   **Line 359:** 테이블 행(`tr`)을 돕니다.
*   **Line 363-370:** 제목(`rank01`)과 가수(`rank02`)를 CSS 선택자로 긁어옵니다.
*   **Line 372-381:** "듣기" 버튼에 걸린 자바스크립트 링크(`javascript:playSong('ids')`)를 분석해서 노래의 고유 ID를 빼옵니다.
*   **Line 389:** 리스트에 추가합니다.

---

### [Line 402 ~ 460] parseMelonJson (JSON 해석기)
API(Step 1)에서 받은 JSON 글자를 자바 객체로 옮겨 적는 단순 반복 작업입니다.

*   **Line 406:** JSON 글자를 트리(`Tree`) 구조로 읽습니다.
*   **Line 407:** 응답 중 `SONGLIST` 부분만 딱 집어냅니다.
*   **Line 409:** `for`문으로 노래 개수만큼 돕니다.
*   **Line 413:** `SONGNAME` -> `title`
*   **Line 417:** `ARTISTLIST` 배열의 첫 번째 항목 -> `artist`
*   **Line 422:** `ALBUMNAME` -> `albumTitle`
*   **Line 424:** `ALBUMIMG` -> `image`
*   **Line 431:** `SONGID` -> `musicId`
*   **Line 434:** `PLAYTIME` -> `duration` (초 단위)
*   **Line 441-450:** `ISSUEDATE`(예: "20230101")를 잘라서 연/월/일로 변환합니다. `substring(0,4)`는 연도, `substring(4,6)`은 월... 이런 식입니다.
*   **Line 452:** `CURRANK` -> `rank` (현재 순위)

---

### [Line 462 ~ 473] getRawMelonChartJson
*   개발 테스트용 함수입니다. 파싱 안 하고 그냥 JSON 원본 글자를 그대로 리턴합니다. "제대로 받아오고 있나?" 눈으로 확인할 때 씁니다.

---

**[요약]**
이 하나의 파일 안에는 **"모바일 앱인 척하는 연기자(API)"**와 **"PC 브라우저인 척하는 연기자(크롤링)"**, 그리고 **"외계어 통역사(JSON 파서)"**가 모두 들어있습니다. 이들이 협동해서 데이터를 훔쳐(?)옵니다.
