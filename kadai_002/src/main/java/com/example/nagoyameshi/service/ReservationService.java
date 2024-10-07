package com.example.nagoyameshi.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Shop;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationRegisterForm;
import com.example.nagoyameshi.repository.ReservationRepository;
import com.example.nagoyameshi.repository.ShopRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Service
public class ReservationService {
	private final ReservationRepository reservationRepository;
	private final ShopRepository shopRepository;  
    private final UserRepository userRepository;

	public ReservationService(ReservationRepository reservationRepository, ShopRepository shopRepository, UserRepository userRepository) {
		this.reservationRepository = reservationRepository;
		this.shopRepository = shopRepository;  
        this.userRepository = userRepository;  
	}

	@Transactional
	public void create(ReservationRegisterForm ReservationRegisterForm) {
		Reservation reservation = new Reservation();
		Shop shop = shopRepository.getReferenceById(ReservationRegisterForm.getShopId());
        User user = userRepository.getReferenceById(ReservationRegisterForm.getUserId());
        LocalDate reservationDate = LocalDate.parse(ReservationRegisterForm.getReservationDate());

        reservation.setShop(shop);
        reservation.setUser(user);
        reservation.setReservationDate(reservationDate);
		reservation.setReservationTime(ReservationRegisterForm.getReservationTime());
		reservation.setNumberOfPeople(ReservationRegisterForm.getNumberOfPeople());

		reservationRepository.save(reservation);

	}
}