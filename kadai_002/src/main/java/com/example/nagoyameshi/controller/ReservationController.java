package com.example.nagoyameshi.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.CategoryShopRelation;
import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.Shop;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationInputForm;
import com.example.nagoyameshi.form.ReservationRegisterForm;
import com.example.nagoyameshi.repository.CategoryShopRelationRepository;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.ReservationRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.repository.ShopRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.FavoriteService;
import com.example.nagoyameshi.service.ReservationService;
import com.example.nagoyameshi.service.ReviewService;


@Controller
public class ReservationController {
	private final ReservationRepository reservationRepository;
	private final ShopRepository shopRepository;
	private final ReservationService reservationService; 
	private final CategoryShopRelationRepository categoryShopRelationRepository;
	private final ReviewRepository reviewRepository;
	private final FavoriteRepository favoriteRepository;
	private final ReviewService reviewService;
	private final FavoriteService favoriteService;

	public ReservationController(ReservationRepository reservationRepository, ShopRepository shopRepository, ReservationService reservationService,
			CategoryShopRelationRepository categoryShopRelationRepository,
			ReviewRepository reviewRepository,
			ReviewService reviewService, FavoriteRepository favoriteRepository, FavoriteService favoriteService) {
		this.reservationRepository = reservationRepository;
		this.shopRepository = shopRepository;
		this.reservationService = reservationService;
		this.categoryShopRelationRepository = categoryShopRelationRepository;
		this.reviewRepository = reviewRepository;
		this.reviewService = reviewService;
		this.favoriteService = favoriteService;
		this.favoriteRepository = favoriteRepository;
	}

	@GetMapping("/reservations")
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) {
		User user = userDetailsImpl.getUser();
		Page<Reservation> reservationPage = reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);

		LocalDate currentDate = LocalDate.now();
	    model.addAttribute("currentDate", currentDate);
	    model.addAttribute("reservationPage", reservationPage);

		return "reservations/index";
	}

	@GetMapping("/shops/{id}/reservations/input")
	public String input(@PathVariable(name = "id") Integer id,
			@ModelAttribute @Validated ReservationInputForm reservationInputForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			Model model,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		Favorite favorite = null;
		boolean isFavorite = false;

		Shop shop = shopRepository.getReferenceById(id);
		boolean userPosted = false;

		if (userDetailsImpl != null) {
			User user = userDetailsImpl.getUser();
			userPosted = reviewService.reviewJudge(shop, user);
			isFavorite = favoriteService.favoriteJudge(shop, user);
			if (isFavorite) {
				favorite = favoriteRepository.findByShopAndUser(shop, user);
			}
		}

		// 時間オプションを再生成してモデルに追加
		List<String> options = new ArrayList<>();
		options = IntStream.rangeClosed(0, 47)
 				.mapToObj(i -> LocalTime.of(0, 0).plusMinutes(30 * i).toString())
 				.collect(Collectors.toList());

		model.addAttribute("timeOptions", options); // Modelに時間オプションを追加する 
		Integer numberOfPeople = reservationInputForm.getNumberOfPeople();
		
		  
		List<CategoryShopRelation> categoryShopRelation = categoryShopRelationRepository.findByShopOrderByIdAsc(shop);

		List<Review> reviewList = reviewRepository.findTop6ByShopOrderByCreatedAtDesc(shop);
		long reviewCount = reviewRepository.countByShop(shop);
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("shop", shop);
			model.addAttribute("categoryShopRelation", categoryShopRelation);
			model.addAttribute("userPosted", userPosted);
			model.addAttribute("reviewList", reviewList);
			model.addAttribute("reviewCount", reviewCount);
			model.addAttribute("favorite", favorite);
			model.addAttribute("isFavorite", isFavorite);
			model.addAttribute("errorMessage", "予約内容に不備があります。");
			return "shops/show";
		}

		redirectAttributes.addFlashAttribute("reservationInputForm", reservationInputForm);

		return "redirect:/shops/{id}/reservations/confirm";
	}

	@GetMapping("/shops/{id}/reservations/confirm")
	public String confirm(
			@PathVariable(name = "id") Integer id,
			@ModelAttribute ReservationInputForm reservationInputForm,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			Model model) {
		Shop shop = shopRepository.getReferenceById(id);
		User user = userDetailsImpl.getUser();

		// 予約日を取得する
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate reservationDate = LocalDate.parse(reservationInputForm.getReservationDate(), formatter);

		// 予約時間を取得する 
		LocalTime reservationTime = reservationInputForm.getReservationTime();

		// 予約人数を計算する
		Integer numberOfPeople = reservationInputForm.getNumberOfPeople();

		ReservationRegisterForm reservationRegisterForm = new ReservationRegisterForm(
				shop.getId(), user.getId(), reservationDate.toString(), reservationInputForm.getReservationTime(),
				reservationInputForm.getNumberOfPeople());

		model.addAttribute("shop", shop);
		model.addAttribute("reservationRegisterForm", reservationRegisterForm);

		return "reservations/confirm";
	}

	@PostMapping("/shops/{id}/reservations/create")
	public String create(@ModelAttribute ReservationRegisterForm reservationRegisterForm) {
		reservationService.create(reservationRegisterForm);
		return "redirect:/reservations?reserved";
	}

	@PostMapping("/reservations/{reservationId}/delete")
	public String delete(@PathVariable(name = "reservationId") Integer reservationId, RedirectAttributes redirectAttributes) {
	    reservationRepository.deleteById(reservationId);
	    redirectAttributes.addFlashAttribute("successMessage", "予約をキャンセルしました。");
	    return "redirect:/reservations";
	}
}