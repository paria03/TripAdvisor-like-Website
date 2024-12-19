function fetchReviews(action) {

    let hotelId = document.getElementById("hotelId").innerText.replace("Id: ", "").trim();

    fetch(`/hotelReviews?hotelId=` + hotelId + '&action=' + action)
        .then(response => response.json())
        .then(reviews => {
            const reviewsContainer = document.getElementById("reviews-container");
            reviewsContainer.innerHTML = "";
            if (reviews.noMoreReviews) {
                const noMoreReviewsMessage = document.createElement("div");
                noMoreReviewsMessage.classList.add("no-more-reviews");
                noMoreReviewsMessage.innerText = "No more reviews available.";
                reviewsContainer.appendChild(noMoreReviewsMessage);
            } else {
                reviews.forEach(review => {
                    const reviewDiv = document.createElement("div");
                    reviewDiv.classList.add("review-container");

                    reviewDiv.innerHTML = `
                    <div class="review-title">Title: ${review.title}</div>
                    <div class="review-content">
                        <p>Date: ${review.formattedDate}</p>
                        <p>Rating: ${review.rating}</p>
                        <br>
                        <p>${review.text}</p>
                        <br>
                        <p>Review By: ${review.nickName}</p>
                     
                        <button class="btn btn-outline-primary btn-sm" onclick="likeReview(${review.reviewId})"><i class="bi bi-hand-thumbs-up"></i> Like</button>
                        <p id="like-count-${review.reviewId}">${review.likeCount}</p>users found this review helpful
                    </div>
                    
                `;
                    let currentUsername = document.getElementById("username").value;

                    if (currentUsername === review.nickName) {
                        const editDeleteForm = `
                        <div>
                            <form action="/editReview" method="get">
                                <input type="hidden" name="reviewId" value="${review.reviewId}">
                                <input type="hidden" name="hotelId" value="${hotelId}">
                                <button type="submit"  class="btn btn-secondary">Edit</button>
                            </form>
                            <form action="/deleteReview" method="get">
                                <input type="hidden" name="reviewId" value="${review.reviewId}">
                                <input type="hidden" name="hotelId" value="${hotelId}">
                                <button type="submit"  class="btn btn-danger">Delete</button>
                            </form>
                            
                        </div>
                    `;
                        reviewDiv.innerHTML += editDeleteForm;
                    }
                    reviewsContainer.appendChild(reviewDiv);
                });
            }
        });
}

function likeReview(reviewId) {
    fetch("/likeReview?reviewId=" + reviewId, {method: "POST"})
        .then(response => response.json())
        .then(data => {
            if (data.likeCount !== undefined) {
                document.getElementById(`like-count-${reviewId}`).innerText = data.likeCount;
            } else {
                alert(data.message);
            }
        });
}

window.onload = function () {
    fetchReviews();
};
