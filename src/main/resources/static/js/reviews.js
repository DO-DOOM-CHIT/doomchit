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

const rating_value1 = document.querySelector("#rating_value1");
const rating_input1 = document.querySelector("#rating_input1");
if (rating_value1 && rating_input1) {
  rating_value1.textContent = rating_input1.value;
  rating_input1.addEventListener("input", (event) => {
    rating_value1.textContent = event.target.value;
  });
}

const rating_value2 = document.querySelector("#rating_value2");
const rating_input2 = document.querySelector("#rating_input2");
if (rating_value2 && rating_input2) {
  rating_value2.textContent = rating_input2.value;
  rating_input2.addEventListener("input", (event) => {
    rating_value2.textContent = event.target.value;
  });
}
