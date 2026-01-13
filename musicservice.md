# Doomchit 음악 차트 로직 실행 경로 추적 (File-to-File)

이 문서는 사용자가 웹 브라우저에서 **"메인 화면(차트)"**에 접속했을 때, 코드가 **어떤 파일의 어떤 줄(Line)로 이동하며 실행되는지** 완벽하게 추적합니다.

---

## 🚦 시작: 사용자가 `/doomchit/main` 페이지 접속

### [1단계] `MainController.java` (접속 요청 받음)
사용자의 요청이 제일 먼저 도착하는 곳입니다.

*   **위치:** `src/main/java/com/mysite/doomchit/MainController.java`
*   **[Line 29]** `@GetMapping("/doomchit/main")`
    *   사용자가 접속했다는 신호가 여기서 잡힙니다.
*   **[Line 30]** `public String main(Model model)` 함수 시작.
*   **[Line 31]** `List<Music> chart = musicService.getMelonChartBasic();`
    *   👉 **[이동]** 여기서 데이터를 가져오기 위해 **`MusicService.java` 파일로 점프**합니다!

---

### [2단계] `MusicService.java` (차트 데이터 수집)
컨트롤러의 명령을 받고 멜론 서버에 데이터를 가지러 가는 곳입니다.

*   **위치:** `src/main/java/com/mysite/doomchit/musics/MusicService.java`
*   **[Line 115]** `public List<Music> getMelonChartBasic()` 함수 진입.
*   **[Line 119]** `webClient.get().uri(CHART_API_URL)...`
    *   � **[외부 통신]** 여기서 내 컴퓨터를 떠나 **`m2.melon.com` (멜론 서버)**로 요청이 날아갑니다.
    *   ✋ **[대기]** 멜론이 JSON 데이터를 줄 때까지 여기서 잠시 멈춥니다.
*   **[Line 126]** `return parseMelonJson(response);`
    *   JSON을 받으면 해석하기 위해 **같은 파일 아래쪽 [Line 402]** `parseMelonJson` 함수로 이동합니다.

---

### [3단계] `MusicService.java` (데이터 해석 및 객체 생성)
받아온 JSON 글자 덩어리를 자바 객체로 변환합니다.

*   **[Line 402]** `private List<Music> parseMelonJson(...)` 함수 진입.
*   **[Line 411]** `Music music = new Music();`
    *   👉 **[참조]** 이 객체를 만들기 위해 **`Music.java` 파일(엔티티) 설계도**를 참고합니다.
*   **[Line 459]** `return musicList;`
    *   해석이 끝난 100곡 리스트를 가지고 **[Line 126]**으로 복귀 -> 다시 **[Line 31]** (MainController.java)로 복귀합니다!

---

### [4단계] `MainController.java` (부가 정보 채우기)
차트 목록은 가져왔는데, 좋아요 수나 댓글 수는 우리 DB에 있으니까 그걸 합쳐야 합니다.

*   **복귀 위치:** `MainController.java`의 **[Line 31]** 끝남.
*   **[Line 32]** `populateMusicInfo(chart);` 실행 (바로 아래 **[Line 57]** 함수로 이동)
*   **[Line 60]** `musicService.findMusicByMusicId(m.getMusicId())`
    *   "이 노래 ID가 우리 DB 조회용 번호(PK)로 몇 번이냐?" 확인하기 위해 다시 **`MusicService.java`**로 이동.

---

### [5단계] `MusicService.java` -> `MusicRepository.java` (DB 조회)
*   **[Line 110]** `public Music findMusicByMusicId(...)` 진입.
*   **[Line 111]** `return musicRepository.findByMusicId(musicId);`
    *   � **[이동]** DB 담당자인 **`MusicRepository.java` 파일**을 호출합니다.

---

### [6단계] `MusicRepository.java` (창고 조회)
*   **위치:** `src/main/java/com/mysite/doomchit/musics/MusicRepository.java`
*   **역할:** 자바 코드 없이 인터페이스만 정의되어 있지만, **Spring Data JPA**가 자동으로 `SELECT * FROM music WHERE music_id = ?` 쿼리를 만들어 **MySQL DB**에 날립니다.
*   👉 **[복귀]** DB에서 찾은 데이터를 들고 **MusicService** -> **MainController**로 되돌아갑니다.

---

### [7단계] `MainController.java` (화면으로 보내기)
모든 데이터 준비를 마쳤습니다.

*   **복귀 위치:** `MainController.java`의 **[Line 60]** 이후.
*   **[Line 63~66]** DB에서 가져온 정보로 좋아요/댓글 수 등을 채워 넣습니다.
*   **[Line 36]** `return "main";`
    *   👉 **[최종 이동]** `main.html` (화면 파일)로 데이터(`chart`)를 넘기면서 사용자의 화면에 출력하라고 지시합니다.

---

## [요약: 파일 이동 경로]

1. `MainController.java` (메인 접속)
   ↓ (Line 31)
2. `MusicService.java` (멜론 차트 가져와)
   ↓ (외부 서버 통신)
3. `Music.java` (객체 생성)
   ↓ (복귀)
4. `MainController.java` (데이터 받았음)
   ↓ (Line 60)
5. `MusicRepository.java` (DB에 이 노래 있어?)
   ↓ (MySQL 조회)
6. `MainController.java` (준비 완료)
   ↓ (Line 36)
7. `main.html` (화면 출력)
