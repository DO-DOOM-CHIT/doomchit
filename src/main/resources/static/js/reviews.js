function toggleLyrics() {
  const content = document.getElementById("lyricsContent");
  const btn = document.getElementById("lyricsBtn");
  if (content.classList.contains("collapsed")) {
    content.classList.remove("collapsed");
    btn.innerHTML = '접기<i class="fa-solid fa-angle-up ps-1"></i>';
  } else {
    content.classList.add("collapsed");
    btn.innerHTML = '펼치기<i class="fa-solid fa-angle-down ps-1"></i>';
  }
}

const rating_value = document.querySelector("#rating_value");
const rating_input = document.querySelector("#rating_input");
if (rating_value && rating_input) {
  rating_value.textContent = rating_input.value;
  rating_input.addEventListener("input", (event) => {
    rating_value.textContent = event.target.value;
  });
}
