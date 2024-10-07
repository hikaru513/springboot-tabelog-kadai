let maxDate = new Date();
maxDate.setMonth(maxDate.getMonth() + 3);

flatpickr('#reservationDate', {
    locale: 'ja',
    minDate: 'today',
    maxDate: maxDate
});