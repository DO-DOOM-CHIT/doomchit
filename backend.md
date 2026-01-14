# 📂 파일 & 코드 단위 백엔드 구조 흐름

네, 맞습니다! 아래 나열된 순서는 **실제 코드가 실행되는 시간 순서(Time Flow)**입니다.
사용자 요청이 들어오면 **1번** 파일이 **2번** 파일을 부르고, 그 결과로 **3번**이 실행되는 식입니다.

---

## 1️⃣ 메인 페이지 (차트 보기)
**URL:** `/doomchit/main`

### 🚀 코드 실행 경로 (Flow)
1.  **📄 [MainController.java]** `main(Model model)`
    *   👋 "사용자가 메인 페이지에 들어왔습니다." -> 서비스를 호출합니다.
    *   ⬇️ **(호출)**
2.  **📄 [MusicService.java]** `getMelonChartBasic()`
    *   🍈 "멜론 서버야, 차트 데이터 좀 줘." (`WebClient` 통신)
    *   📝 "알겠어, 자바가 알아듣게 번역할게." (`parseMelonJson`)
    *   ⬇️ **(데이터 리턴)**
3.  **📄 [MainController.java]** `populateMusicInfo(List<Music> chart)`
    *   📊 "멜론 데이터는 받았는데, 좋아요/댓글 수는 우리 DB에서 찾아야 해."
    *   (반복문 실행)
        *   ⬇️ **📄 [MusicService.java]** `findMusicByMusicId` (DB 조회)
        *   ⬇️ **📄 [Likes/ReviewRepository]** `countByMusic` (좋아요/리뷰 수 카운트)
    *   ⬇️ **(모든 데이터 완성)**
4.  **📄 [main.html]**
    *   🖼️ "완성된 데이터를 화면에 그려줍니다."

---

## 2️⃣ 음악 검색 (자동완성)
**URL:** `/doomchit/search?keyword=...`

### 🚀 코드 실행 경로 (Flow)
1.  **📄 [MainController.java]** `search(String keyword)`
    *   📡 "검색 요청이 왔네? 멜론한테 물어보자."
    *   ⬇️ **(호출)**
2.  **📄 [MusicService.java]** `searchMelon(String keyword)`
    *   🕵️ "PC 크롬 브라우저인 척 위장하고 멜론 검색창을 두드립니다." (Header 조작)
    *   ✂️ "결과가 왔는데 괄호가 쳐져있네? 잘라내고 순수 데이터만 챙기자." (JSONP 파싱)
    *   ⬇️ **(JSON 데이터 리턴)**
3.  **🌐 [Response Body]**
    *   📦 "새로고침 없이 검색 결과(JSON)를 즉시 프론트엔드로 보냅니다."

---

## 3️⃣ 상세 페이지 진입 (크롤링 & 저장)
**URL:** `/music/detail/{musicId}` (멜론 ID로 접근)

### 🚀 코드 실행 경로 (Flow)
1.  **📄 [ReviewController.java]** `musicDetailBridge(Long musicId)`
    *   🌉 "외부 ID(멜론ID)네요? 우리 DB 번호로 바꿔드릴게요."
    *   ⬇️ **(호출)**
2.  **📄 [MusicService.java]** `getOrCreateMusicByMusicId(...)`
    *   🧐 "DB에 있나?" -> (없음) -> "그럼 멜론 상세페이지 털어오자!"
    *   🕷️ **크롤링 실행 (`fillSongAndAlbumDetail`)**:
        *   가사, 장르, 발매일 싹 긁어옴 (`Jsoup`)
    *   💾 **DB 저장**: "이제 우리 DB에 영구 저장!" (`Repository.save`)
    *   ⬇️ **(내부 mno 리턴)**
3.  **📄 [ReviewController.java]**
    *   ↪️ **(리다이렉트)**: "자, 이제 진짜 리뷰 페이지(`/doomchit/reviews/{mno}`)로 가세요."

---

## 4️⃣ 리뷰 작성
**URL:** `/doomchit/reviews/{mno}` (POST 요청)

### 🚀 코드 실행 경로 (Flow)
1.  **📄 [ReviewController.java]** `createReview(...)`
    *   👮 "잠깐! 로그인 했나요?" (`@PreAuthorize`)
    *   📝 "내용은 꽉 채웠나요?" (`BindingResult`)
    *   ⬇️ **(호출)**
2.  **📄 [UserService.java]** `getCurrentUser()`
    *   👤 "지금 글 쓰는 사람 누구야?" (로그인 유저 정보 조회)
    *   ⬇️ **(호출)**
3.  **📄 [ReviewService.java]** `create(...)`
    *   🏭 "리뷰 객체 생성! 도장 쾅!" (`new Review`)
    *   💾 "창고(DB)에 넣습니다." (`reviewRepository.save`)
    *   ⬇️ **(완료)**
4.  **📄 [ReviewController.java]**
    *   🔄 "등록됐습니다. 다시 목록 페이지를 보여줍니다 (새로고침)."

---

## 5️⃣ 좋아요 토글
**URL:** `/doomchit/like/toggle/{mno}`

### 🚀 코드 실행 경로 (Flow)
1.  **📄 [LikesController.java]** `toggleLike(Long mno)`
    *   💓 "좋아요 버튼 눌렀네요."
    *   ⬇️ **(호출)**
2.  **📄 [LikesService.java]** `toggleLike(...)`
    *   🔍 "이 사람, 이 노래에 이미 좋아요 눌렀었나?" (`Repository` 조회)
    *   🔄 **(토글 로직)**
        *   (있으면) 🗑️ **삭제** (좋아요 취소)
        *   (없으면) 💾 **저장** (좋아요 등록)
    *   ⬇️ **(완료)**
3.  **📄 [ReviewController.java]**
    *   🔄 "처리 끝. 원래 보던 페이지로 돌아갑니다."

---

## 6️⃣ 회원가입 로직
**URL:** `/doomchit/signup` (POST 요청)

### 🚀 코드 실행 경로 (Flow)
1.  **📄 [UserController.java]** `signup(UserForm form)`
    *   🛡️ "아이디/닉네임 중복 아닌가요? 비밀번호는 똑같이 쳤나요?"
    *   ⬇️ **(통과 시 호출)**
2.  **📄 [UserService.java]** `create(...)`
    *   🔐 "비밀번호는 못 알아보게 암호화합니다." (`BCrypt`)
    *   💾 "회원 명부(DB)에 등록합니다." (`Repository.save`)
3.  **📄 [UserController.java]**
    *   ↪️ "가입 축하합니다. 로그인 페이지로 이동하세요."

---

## 7️⃣ 로그인 / 로그아웃 로직 (Spring Security)
**로그인은 컨트롤러가 아닌 스프링 시큐리티가 "가로채서" 처리합니다.**

### 🔑 로그인 (Flow)
**URL:** `/doomchit/login` (POST 요청)

1.  **🛡️ [Spring Security]** `SecurityConfig (FilterChain)`
    *   🛑 "잠깐! 로그인 요청이 들어왔네? 내가 처리할게." (컨트롤러로 안 보냄)
    *   ⬇️ **(인증 시도)**
2.  **📄 [UserSecurityService.java]** `loadUserByUsername(String userId)`
    *   🕵️ "DB에 이 아이디(`userId`)를 가진 사람이 있나?" (`userRepository.findByUserId`)
    *   **IF (없음):** ❌ "그런 사람 없는데요?" -> 로그인 실패 예외 발생
    *   **IF (있음):** 
        *   ✅ "찾았습니다! 비밀번호 맞는지 확인해볼게요."
        *   🔐 암호화된 비밀번호 비교 (`BCryptPasswordEncoder`)
    *   ⬇️ **(인증 성공)**
3.  **🛡️ [Spring Security]**
    *   🎫 "신분 확인 완료! **세션(Session)** 티켓 발급."
    *   🏁 "원래 가려던 페이지나 메인 페이지로 보내주자." (`SuccessHandler`)

---

### 🚪 로그아웃 (Flow)
**URL:** `/doomchit/user/logout`

1.  **🛡️ [Spring Security]** `SecurityConfig`
    *   👋 "로그아웃 요청이네? 세션 티켓 찢어버리자." (`invalidateHttpSession`)
    *   🧹 "로그인 쿠키도 삭제!" (`deleteCookies`)
    *   ⬇️ **(완료)**
2.  **🔄 [Redirect]**
    *   ↪️ "안녕히 가세요. 메인 페이지(`/`)로 이동합니다."
