// レビューの削除用フォーム
const deleteReviewForm = document.forms.deleteReviewForm;

// レビューの更新用フォーム
const updateReviewForm = document.forms.updateReviewForm;

// レビューの削除用モーダルを開くときの処理
document.getElementById('deleteReviewModal').addEventListener('show.bs.modal', (event) => {
	let deleteButton = event.relatedTarget;
	let shopId = deleteButton.dataset.shopId;
	let reviewId = deleteButton.dataset.reviewId;

	deleteReviewForm.action = `/shops/${shopId}/reviews/${reviewId}/delete`;
});

// レビューの更新用モーダルを開くときの処理
document.getElementById('updateReviewModal').addEventListener('show.bs.modal', (event) => {
    let updateButton = event.relatedTarget;
    let reviewId = updateButton.dataset.reviewId;

    updateReviewForm.action = `/admin/reviews/${reviewId}/update`;
});
