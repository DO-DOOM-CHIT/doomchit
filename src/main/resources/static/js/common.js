/**
 * common.js
 *
 * ✔ 공통 JS
 * ✔ header.html 로드
 * ✔ 메뉴 active 처리
 * ✔ 로그인 상태 분기 (추후 API 연동)
 */

/* ==========================
   1. header.html 불러오기
========================== */
document.addEventListener('DOMContentLoaded', () => {
  loadHeader();
});

/**
 * header.html을 현재 페이지에 삽입
 */
function loadHeader() {
  const headerContainer = document.getElementById('header');

  if (!headerContainer) return;

  fetch('/doomchit/header.html')
    .then(res => res.text())
    .then(html => {
      headerContainer.innerHTML = html;

      // 헤더 로드 후 로그인 상태 처리
      setActiveMenu();
      checkLoginStatus();
    })
    .catch(err => {
      console.error('header.html 로드 실패', err);
    });
}

/* ==========================
   2. 메뉴 active 처리
========================== */
function setActiveMenu() {
  const path = window.location.pathname;

  const menuLinks = document.querySelectorAll('.header-nav a');
  menuLinks.forEach(link => link.classList.remove('active'));

  if (path.includes('likes')) {
    document
      .querySelector('.header-nav a[data-menu="likes"]')
      ?.classList.add('active');
  } else {
    document
      .querySelector('.header-nav a[data-menu="main"]')
      ?.classList.add('active');
  }
}

/* ==========================
   3. 로그인 상태 체크
========================== */
function checkLoginStatus() {
  /**
   * TODO (백엔드 연동 시)
   *
   * fetch('/api/auth/me')
   *  → 로그인 유저 정보 반환
   */

  // 지금은 임시로 false
  const isLogin = false;

  const authArea = document.querySelector('.auth');

  if (!authArea) return;

  if (isLogin) {
    // 로그인 상태
    authArea.innerHTML = `
      <span class="user-name">나혜</span>
      <button class="logout-btn">로그아웃</button>
    `;

    document
      .querySelector('.logout-btn')
      .addEventListener('click', logout);

  } else {
    // 비로그인 상태
    authArea.innerHTML = `
      <a href="/doomchit/login" class="login-link">로그인</a>
    `;
  }
}

/* ==========================
   4. 로그아웃
========================== */
function logout() {
  /**
   * TODO (백엔드 연동 시)
   * POST /api/auth/logout
   */
  alert('로그아웃 (추후 API 연동)');
}

/* ==========================
   검색 기능
========================== */
document.addEventListener('click', (e) => {
  if (e.target.closest('#searchBtn')) {
    handleSearch();
  }
});

document.addEventListener('keydown', (e) => {
  if (e.key === 'Enter' && e.target.id === 'searchInput') {
    handleSearch();
  }
});

function handleSearch() {
  const keyword = document.getElementById('searchInput').value.trim();

  if (!keyword) return;

  /**
   * TODO (백엔드 연동)
   * GET /api/search?keyword=
   * or
   * /doomchit/search?keyword=
   */
  console.log('검색어:', keyword);

  // 임시 동작
  alert(`"${keyword}" 검색 (API 연동 예정)`);
}