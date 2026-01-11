/**
 * TODO (백엔드 연동 시)
 * - fetch('/api/songs')
 * - JPA Entity → DTO → JSON
 */

// 임시 데이터
const chartData = [
  {
    id: 1,
    title: 'Good Goodbye',
    artist: '화사 (HWASA)',
    album: 'Good Goodbye',
    comment: 76246,
    like: 98657,
    rating: 5
  },
  {
    id: 2,
    title: '안녕하시봉',
    artist: '나혜',
    album: '나혜의 첫앨범',
    comment: 65446,
    like: 12658,
    rating: 4
  }
  // 실제로는 API에서 가져옴
];

// DOM 요소
const chartBody = document.getElementById('chartBody');
const sortBtn = document.querySelector('.sort-btn');
const sortMenu = document.querySelector('.sort-menu');

// 차트 렌더링
function renderChart(list) {
  chartBody.innerHTML = '';

// 각 곡 행 생성
  list.forEach((song, index) => {
    const tr = document.createElement('tr');

  // 행 클릭 시 리뷰 페이지 이동
  tr.addEventListener('click', () => {
    window.location.href = `/doomchit/reviews.html?id=${musicId}`;
  });

  // 행 내용 채우기
    tr.innerHTML = `
      <td>${index + 1}</td>
      <td>
        <div class="song">
          <div class="thumb"></div>
          <div>
            <div class="song-title">${song.title}</div>
            <div class="song-artist">${song.artist}</div>
          </div>
        </div>
      </td>
      <td class="album">${song.album}</td>
      <td>
        <div class="icon-text">
          <i class="fa-regular fa-comment"></i>
          ${song.comment.toLocaleString()}
        </div>
      </td>
      <td>
        <div class="icon-text">
          <i class="fa-solid fa-heart"></i>
          ${song.like.toLocaleString()}
        </div>
      </td>
      <td>
        <div class="rating">
          ${createEmptyStars()}
        </div>
      </td>
    `;

    // ⭐ 행 전체 클릭 가능하게 (임시 # 처리)
    tr.style.cursor = 'pointer';
    tr.addEventListener('click', () => {
    // TODO: 나중에 URL 확정되면 여기만 수정
    window.location.href = '#';
});

// 행을 테이블 본문에 추가
    chartBody.appendChild(tr);
  });
}

// 정렬 메뉴 토글 및 정렬 기능
sortBtn.addEventListener('click', () => {
  sortMenu.style.display =
    sortMenu.style.display === 'block' ? 'none' : 'block';
});

// 정렬 옵션 클릭 시 차트 재렌더링
sortMenu.addEventListener('click', (e) => {
  const type = e.target.dataset.sort;
  if (!type) return;

  // 정렬
  sortMenu.addEventListener('click', (e) => {
  e.stopPropagation(); // ⭐ 핵심

  const li = e.target.closest('[data-sort]');
  if (!li) return;

  const type = li.dataset.sort;
  location.href = `/doomchit/main?sort=${type}`;
});

  // 메뉴 닫기
  sortMenu.style.display = 'none';
});

// 초기 차트 렌더링 (서버 사이드 렌더링을 위해 JS 렌더링 중단)
// renderChart(chartData);

// 별점 아이콘 생성 함수
function createEmptyStars() {
  return `
    <i class="fa-regular fa-star"></i>
    <i class="fa-regular fa-star"></i>
    <i class="fa-regular fa-star"></i>
    <i class="fa-regular fa-star"></i>
    <i class="fa-regular fa-star"></i>
  `;
}
