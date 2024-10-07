package com.example.nagoyameshi.controller;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.CategoryShopRelation;
import com.example.nagoyameshi.entity.Shop;
import com.example.nagoyameshi.form.ShopEditForm;
import com.example.nagoyameshi.form.ShopRegisterForm;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.CategoryShopRelationRepository;
import com.example.nagoyameshi.repository.ShopRepository;
import com.example.nagoyameshi.service.ShopService;

@Controller
@RequestMapping("/admin/shops")
public class AdminShopController {
	private final ShopRepository shopRepository;
	private final CategoryRepository categoryRepository;
	private final ShopService shopService;
	private final CategoryShopRelationRepository categoryShopRelationRepository;

	public AdminShopController(ShopRepository shopRepository, ShopService shopService,
			CategoryShopRelationRepository categoryShopRelationRepository, CategoryRepository categoryRepository) {
		this.shopRepository = shopRepository;
		this.shopService = shopService;
		this.categoryShopRelationRepository = categoryShopRelationRepository;
		this.categoryRepository = categoryRepository;

	}

	@GetMapping
	public String index(Model model,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			@RequestParam(name = "keyword", required = false) String keyword) {
		Page<Shop> shopPage;

		if (keyword != null && !keyword.isEmpty()) {
			shopPage = shopRepository.findByNameLike("%" + keyword + "%", pageable);
		} else {
			shopPage = shopRepository.findAll(pageable);
		}

		model.addAttribute("shopPage", shopPage);
		model.addAttribute("keyword", keyword);

		return "admin/shops/index";
	}

	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, Model model) {
		Shop shop = shopRepository.getReferenceById(id);
		List<CategoryShopRelation> categoryShopRelation = categoryShopRelationRepository.findByShopOrderByIdAsc(shop);

		model.addAttribute("categoryShopRelation", categoryShopRelation);

		model.addAttribute("shop", shop);

		return "admin/shops/show";
	}

	@GetMapping("/register")
	public String register(Model model) {
		

		model.addAttribute("shopRegisterForm", new ShopRegisterForm());

		// 時間オプションを生成
		List<String> options = IntStream.rangeClosed(0, 47)
				.mapToObj(i -> LocalTime.of(0, 0).plusMinutes(30 * i).toString())
				.collect(Collectors.toList());

		model.addAttribute("timeOptions", options); // Modelに時間オプションを追加する

		List<Category> categories = categoryRepository.findAll();

		model.addAttribute("categories", categories);


		return "admin/shops/register";
	}

	@PostMapping("/create")
	public String create(@ModelAttribute @Validated ShopRegisterForm shopRegisterForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			Model model) {
		if (bindingResult.hasErrors()) {
			
			List<Category> categories = categoryRepository.findAll();

			model.addAttribute("categories", categories);

			// 時間オプションを再生成してモデルに追加
			List<String> options = IntStream.rangeClosed(0, 47)
					.mapToObj(i -> LocalTime.of(0, 0).plusMinutes(30 * i).toString())
					.collect(Collectors.toList());

			model.addAttribute("timeOptions", options); // Modelに時間オプションを追加する

			return "admin/shops/register";
		}
		// 開店時間と閉店時間のバリデーション
		LocalTime openingTime = shopRegisterForm.getOpeningTime();
		LocalTime closingTime = shopRegisterForm.getClosingTime();

		if (closingTime.isBefore(openingTime)) {
			bindingResult.rejectValue("closingTime", "error.closingTime", "閉店時間は開店時間より後の時間を設定してください。");

			// 時間オプションを再生成してモデルに追加
			List<String> options = IntStream.rangeClosed(0, 47)
					.mapToObj(i -> LocalTime.of(0, 0).plusMinutes(30 * i).toString())
					.collect(Collectors.toList());

			model.addAttribute("timeOptions", options); // Modelに時間オプションを追加する
			return "admin/shops/register";
		}

		shopService.create(shopRegisterForm);
		redirectAttributes.addFlashAttribute("successMessage", "店舗を登録しました。");

		return "redirect:/admin/shops";
	}

	@GetMapping("/{id}/edit")
	public String edit(@PathVariable(name = "id") Integer id, Model model) {
		Shop shop = shopRepository.getReferenceById(id);
		String imageName = shop.getImageName();
		List<Integer> categoryIds = categoryShopRelationRepository.findCategoryIdsByShopOrderByIdAsc(shop);
		ShopEditForm shopEditForm = new ShopEditForm(shop.getId(), shop.getName(), null, shop.getDescription(),
				shop.getOpeningTime(), shop.getClosingTime(), shop.getRegularOff(),
				shop.getPrice(), shop.getPostalCode(), shop.getAddress(), shop.getPhoneNumber(), categoryIds);
		
		 List<Category> categories = categoryRepository.findAll();

		model.addAttribute("imageName", imageName);
		model.addAttribute("shopEditForm", shopEditForm);
		model.addAttribute("categories", categories); 

		// 時間オプションを生成
		List<String> options = IntStream.rangeClosed(0, 47)
				.mapToObj(i -> LocalTime.of(0, 0).plusMinutes(30 * i).toString())
				.collect(Collectors.toList());

		model.addAttribute("timeOptions", options); // Modelに時間オプションを追加する

		return "admin/shops/edit";
	}

	@PostMapping("/{id}/update")
	public String update(@ModelAttribute @Validated ShopEditForm shopEditForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model) {
		if (bindingResult.hasErrors()) {
			List<Category> categories = categoryRepository.findAll();
			 model.addAttribute("categories", categories);
			// 時間オプションを再生成してモデルに追加
			List<String> options = IntStream.rangeClosed(0, 47)
					.mapToObj(i -> LocalTime.of(0, 0).plusMinutes(30 * i).toString())
					.collect(Collectors.toList());

			model.addAttribute("timeOptions", options); // Modelに時間オプションを追加する
			return "admin/shops/edit";
		}

		shopService.update(shopEditForm);
		redirectAttributes.addFlashAttribute("successMessage", "店舗情報を編集しました。");

		return "redirect:/admin/shops";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		shopRepository.deleteById(id);

		redirectAttributes.addFlashAttribute("successMessage", "店舗を削除しました。");

		return "redirect:/admin/shops";
	}
}