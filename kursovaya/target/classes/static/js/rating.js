document.addEventListener("DOMContentLoaded", () => {
    const stars = document.querySelectorAll(".star");
    const ratingInput = document.getElementById("ratingInput");
    stars.forEach((star) => {
        star.addEventListener("click", () => {
            const rating = star.getAttribute("data-value");
            ratingInput.value = rating;
            stars.forEach((s, index) => {
                const starValue = index + 1;
                if (starValue <= rating) {
                    s.querySelector("img").src = "/images/star-filled.png";
                } else {
                    s.querySelector("img").src = "/images/star-empty.png";
                }
            });
        });
    });
});