package com.example.nagoyameshi.controller;

import java.time.LocalTime;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.CategoryShopRelation;
import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.Shop;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationInputForm;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.CategoryShopRelationRepository;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.repository.ShopRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.FavoriteService;
import com.example.nagoyameshi.service.ReviewService;

@Controller
@RequestMapping("/shops")
public class ShopController {
	private final ShopRepository shopRepository;
	private final CategoryRepository categoryRepository;
	private final CategoryShopRelationRepository categoryShopRelationRepository;
	private final ReviewRepository reviewRepository;
	private final FavoriteRepository favoriteRepository;
	private final ReviewService reviewService;
	private final FavoriteService favoriteService;

	public ShopController(ShopRepository shopRepository,
			CategoryRepository categoryRepository, CategoryShopRelationRepository categoryShopRelationRepository,
			ReviewRepository reviewRepository,
			ReviewService reviewService, FavoriteRepository favoriteRepository, FavoriteService favoriteService) {
		this.shopRepository = shopRepository;
		this.categoryRepository = categoryRepository;
		this.categoryShopRelationRepository = categoryShopRelationRepository;
		this.reviewRepository = reviewRepository;
		this.reviewService = reviewService;
		this.favoriteService = favoriteService;
		this.favoriteRepository = favoriteRepository;
	}

	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "area", required = false) String area,
			@RequestParam(name = "price", required = false) Integer price,
			@RequestParam(name = "categoryId", required = false) Integer categoryId,
			@RequestParam(name = "order", required = false) String order,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) {

		Page<Shop> shopPage;

		// キーワード検索
		if (keyword != null && !keyword.isEmpty() || area != null && !area.isEmpty() || categoryId != null) {
			shopPage = shopRepository.findByKeywordAndFilters(keyword != null ? keyword : "",
					area != null ? area : "",
					categoryId,
					pageable);
		} else if (area != null && !area.isEmpty()) {
			if (order != null && order.equals("priceAsc")) {
				shopPage = shopRepository.findByAddressLikeOrderByPriceAsc("%" + area + "%", pageable);
			} else {
				shopPage = shopRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
			}
		} else if (price != null) {
			if (order != null && order.equals("priceAsc")) {
				shopPage = shopRepository.findByPriceLessThanEqualOrderByPriceAsc(price, pageable);
			} else {
				shopPage = shopRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
			}
		} else if (categoryId != null) {
			shopPage = shopRepository.findByCategoryId(categoryId, pageable);
		} else {
			if (order != null && order.equals("priceAsc")) {
				shopPage = shopRepository.findAllByOrderByPriceAsc(pageable);
			} else {
				shopPage = shopRepository.findAllByOrderByCreatedAtDesc(pageable);
			}
		}

		List<Category> categories = categoryRepository.findAll();

		model.addAttribute("shopPage", shopPage);
		model.addAttribute("categories", categories);
		model.addAttribute("keyword", keyword);
		model.addAttribute("area", area);
		model.addAttribute("price", price);
		model.addAttribute("categoryId", categoryId);
		model.addAttribute("order", order);

		return "shops/index";
	}

	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, Model model,
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
				List<String> options = IntStream.rangeClosed(0, 47)
						.mapToObj(i -> LocalTime.of(0, 0).plusMinutes(30 * i).toString())
						.collect(Collectors.toList());

				model.addAttribute("timeOptions", options); // Modelに時間オプションを追加する 
				
		List<CategoryShopRelation> categoryShopRelation = categoryShopRelationRepository.findByShopOrderByIdAsc(shop);

		List<Review> reviewList = reviewRepository.findTop6ByShopOrderByCreatedAtDesc(shop);
		long reviewCount = reviewRepository.countByShop(shop);

		model.addAttribute("categoryShopRelation", categoryShopRelation);
		model.addAttribute("userPosted", userPosted);
		model.addAttribute("reviewList", reviewList);
		model.addAttribute("reviewCount", reviewCount);
		model.addAttribute("favorite", favorite);
		model.addAttribute("isFavorite", isFavorite);

		model.addAttribute("shop", shop);

		model.addAttribute("reservationInputForm", new ReservationInputForm());

		return "shops/show";
	}
}