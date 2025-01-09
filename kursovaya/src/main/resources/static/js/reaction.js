document.addEventListener("DOMContentLoaded", () => {
    const likeButtons = document.querySelectorAll(".like-button");
    const dislikeButtons = document.querySelectorAll(".dislike-button");

    fetch('/reactions')
        .then(response => response.json())
        .then(userReactions => {
            for (const reviewId in userReactions) {
                const reactionType = userReactions[reviewId];
                const reviewCard = document.querySelector(`.review-card[data-review-id="${reviewId}"]`);
                if (reviewCard) {
                    const likeImg = reviewCard.querySelector(".like-button img");
                    const dislikeImg = reviewCard.querySelector(".dislike-button img");

                    if (reactionType === "like") {
                        likeImg.src = "/images/like-active.png";
                        dislikeImg.src = "/images/dislike-icon.png";
                    } else if (reactionType === "dislike") {
                        dislikeImg.src = "/images/dislike-active.png";
                        likeImg.src = "/images/like-icon.png";
                    }
                }
            }
        })
        .catch(error => console.error("Error fetching reactions:", error));

    // Event listeners for buttons
    likeButtons.forEach(button => {
        button.addEventListener("click", () => handleReaction(button, "like"));
    });

    dislikeButtons.forEach(button => {
        button.addEventListener("click", () => handleReaction(button, "dislike"));
    });

    function handleReaction(button, reactionType) {
        const reviewCard = button.closest(".review-card");
        const reviewId = reviewCard.getAttribute("data-review-id");

        fetch('/reactions', {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ reviewId, reactionType })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("Something went wrong!");
                }
                const likeImg = reviewCard.querySelector(".like-button img");
                const dislikeImg = reviewCard.querySelector(".dislike-button img");

                if (reactionType === "like") {
                    if (likeImg.src.includes("/images/like-active.png")) {
                        likeImg.src = "/images/like-icon.png";
                        dislikeImg.src = "/images/dislike-icon.png";
                    } else {
                        likeImg.src = "/images/like-active.png";
                        dislikeImg.src = "/images/dislike-icon.png";
                    }
                } else if (reactionType === "dislike") {
                    if (dislikeImg.src.includes("/images/dislike-active.png")) {
                        dislikeImg.src = "/images/dislike-icon.png";
                        likeImg.src = "/images/like-icon.png";
                    } else {
                        dislikeImg.src = "/images/dislike-active.png";
                        likeImg.src = "/images/like-icon.png";
                    }
                }
            })
            .catch(error => {
                console.error("Error:", error);
            });
    }
});