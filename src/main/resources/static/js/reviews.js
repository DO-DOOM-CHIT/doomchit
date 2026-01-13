//⭐을 눌러서 평점 주기
document.querySelectorAll(".stars").forEach((ratingStars) => {
  const starsFill = ratingStars.querySelector(".stars-fill");
  const ratingWrap = ratingStars.closest(".rating-wrap");
  const ratingInput = ratingWrap.querySelector("input[type='hidden']");
  const ratingValueText = ratingWrap.querySelector(".rating-value");

  if (!starsFill || !ratingInput) return;

  // ⭐ 확정된 값
  let confirmedRating = parseFloat(ratingInput.value) || 5;

  const renderUI = (rating) => {
    starsFill.style.width = (rating / 5) * 100 + "%";
    if (ratingValueText) {
      ratingValueText.textContent = rating.toFixed(1);
    }
  };

  // 초기 렌더
  renderUI(confirmedRating);

  const calculateRating = (clientX) => {
    const rect = ratingStars.getBoundingClientRect();
    let offsetX = clientX - rect.left;
    offsetX = Math.max(0, Math.min(offsetX, rect.width));

    const step = Math.floor((offsetX / rect.width) * 10);
    return Math.max(1, Math.min(5, (step + 1) / 2));
  };

  // 🟡 hover = 미리보기
  ratingStars.addEventListener("mousemove", (e) => {
    const previewRating = calculateRating(e.clientX);
    // ⭐ 별만 미리보기
    starsFill.style.width = (previewRating / 5) * 100 + "%";
  });

  // 🔵 hover 종료 → 확정값으로 복귀
  ratingStars.addEventListener("mouseleave", () => {
    renderUI(confirmedRating);
  });

  // ✅ 클릭 = 확정
  ratingStars.addEventListener("click", (e) => {
    confirmedRating = calculateRating(e.clientX);
    ratingInput.value = confirmedRating;
    renderUI(confirmedRating);
  });

  const form = ratingStars.closest("form");

  if (form) {
    form.addEventListener("submit", () => {
      // ⭐ 별점 한 번도 안 건드린 경우 대비
      if (!ratingInput.value) {
        ratingInput.value = confirmedRating;
      }
    });
  }
});

// 가사 펼치기 및 접기
function toggleLyrics() {
  const content = document.getElementById("lyricsContent");
  const btn = document.getElementById("lyricsBtn");

  if (!content || !btn) return;

  content.classList.toggle("collapsed");

  btn.innerHTML = content.classList.contains("collapsed")
    ? '펼치기 <i class="fa-solid fa-angle-down ps-1"></i>'
    : '접기 <i class="fa-solid fa-angle-up ps-1"></i>';
}
