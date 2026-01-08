# doomchit

src/
└─ main/
   └─ resources/
      ├─ static/
      │  ├─ css/
      │  │  └─ main.css          # 공통 스타일 (메인/좋아요)
      │  │
      │  ├─ doomchit/
      │  │  ├─ header.html       # 공통 헤더 (JS로 include)
      │  │  ├─ main.html         # 메인(차트) 페이지
      │  │  └─ likes.html        # 좋아요 페이지
      │  │
      │  └─ js/
      │     ├─ common.js         # 헤더 로드 및 공통 JS
      │     ├─ main.js           # 메인 페이지 전용 JS
      │     └─ likes.js          # 좋아요 페이지 전용 JS
      │
      └─ templates/              # (현재 미사용, 서버 렌더링 대비)
b