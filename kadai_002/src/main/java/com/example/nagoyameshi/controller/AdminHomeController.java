package com.example.nagoyameshi.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.nagoyameshi.repository.ReservationRepository;
import com.example.nagoyameshi.repository.ShopRepository;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.service.StripeService;


@Controller
@RequestMapping("/admin")
public class AdminHomeController {
	private final UserRepository userRepository;
	private final ShopRepository shopRepository;
	private final ReservationRepository reservationRepository;
	private final StripeService stripeService;

	public AdminHomeController(UserRepository userRepository, ShopRepository shopRepository,
			ReservationRepository reservationRepository, StripeService stripeService) {
		this.userRepository = userRepository;
		this.shopRepository = shopRepository;
		this.reservationRepository = reservationRepository;
		this.stripeService = stripeService; 
	}

	@GetMapping
	public String index(Model model) {
		long totalMembers = userRepository.countByRoleNameInRoleFreeMemberOrRolePaidMember();
		long totalFreeMembers = userRepository.countByRole_Name("ROLE_FREE_MEMBER");
		long totalPaidMembers = userRepository.countByRole_Name("ROLE_PAID_MEMBER");
		long totalShops = shopRepository.count();
		long totalReservations = reservationRepository.count();

		LocalDate now = LocalDate.now();
		LocalDate startOfMonth = now.withDayOfMonth(1);
		long salesForThisMonth;

		try {
			salesForThisMonth = stripeService.getMonthlyTotalRevenue(startOfMonth);
		} catch (Exception e) {
			salesForThisMonth = 0;
		}

		model.addAttribute("totalMembers", totalMembers);
		model.addAttribute("totalFreeMembers", totalFreeMembers);
		model.addAttribute("totalPaidMembers", totalPaidMembers);
		model.addAttribute("totalShops", totalShops);
		model.addAttribute("totalReservations", totalReservations);
		model.addAttribute("salesForThisMonth", salesForThisMonth);

		return "admin/index";
	}
}
