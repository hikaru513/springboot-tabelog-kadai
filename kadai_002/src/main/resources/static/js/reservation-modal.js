// 予約の削除用フォーム
const deleteReservationForm = document.forms.deleteReservationForm;

// 予約の削除用モーダルを開くときの処理
document.getElementById('deleteReservationModal').addEventListener('show.bs.modal', (event) => {
	let deleteButton = event.relatedTarget;
	let shopId = deleteButton.dataset.shopId;
	let reservationId = deleteButton.dataset.reservationId;

	deleteReservationForm.action = `/shops/${shopId}/reservations/${reservationId}/delete`;
});
