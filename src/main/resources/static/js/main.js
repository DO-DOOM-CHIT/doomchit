/**
 * TODO (백엔드 연동 시)
 * - fetch('/api/songs')
 * - JPA Entity → DTO → JSON
 */

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

const chartBody = document.getElementById('chartBody');
const sortBtn = document.querySelector('.sort-btn');
const sortMenu = document.querySelector('.sort-menu');

/* ===== Render ===== */
function renderChart(list) {
  chartBody.innerHTML = '';

  list.forEach((song, index) => {
    const tr = document.createElement('tr');

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
      <td class="rating">
        ${'★'.repeat(song.rating)}
      </td>
    `;

    chartBody.appendChild(tr);
  });
}

/* ===== Sort ===== */
sortBtn.addEventListener('click', () => {
  sortMenu.style.display =
    sortMenu.style.display === 'block' ? 'none' : 'block';
});

sortMenu.addEventListener('click', (e) => {
  const type = e.target.dataset.sort;

  if (!type) return;

  const sorted = [...chartData].sort((a, b) => b[type] - a[type]);
  renderChart(sorted);

  sortMenu.style.display = 'none';
});

/* ===== Init ===== */
renderChart(chartData);
