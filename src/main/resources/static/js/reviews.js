const rating_value = document.querySelector("#rating_value");
const rating_input = document.querySelector("#rating_input");
if (rating_value && rating_input) {
  rating_value.textContent = rating_input.value;
  rating_input.addEventListener("input", (event) => {
    rating_value.textContent = event.target.value;
  });
}
