package com.example.nagoyameshi.controller;

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

import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.form.ReviewEditForm;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.repository.ShopRepository;
import com.example.nagoyameshi.service.ReviewService;
 
 @Controller
 @RequestMapping("/admin/reviews")
public class AdminReviewController {
     private final ReviewRepository reviewRepository;
     private final ReviewService reviewService;
     private final ShopRepository shopRepository;
     
     public AdminReviewController(ReviewRepository reviewRepository, ReviewService reviewService, ShopRepository shopRepository) {
         this.reviewRepository = reviewRepository;    
         this.reviewService = reviewService;
         this.shopRepository = shopRepository;
     }	
     
     @GetMapping
     public String index(@RequestParam(value = "keyword", required = false) String keyword, Model model, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable) {
         Page<Review> reviewPage;
         
         if (keyword != null && !keyword.isEmpty()) {
             reviewPage = reviewRepository.findByShopNameOrUserNameOrUserEmailContaining(keyword, pageable);
         } else {
             reviewPage = reviewRepository.findAll(pageable);
         }

         model.addAttribute("reviewPage", reviewPage);
         model.addAttribute("keyword", keyword); // ビューに検索キーワードを渡す            
      
         return "admin/reviews/index";
     }
     
     @GetMapping("/{id}")
     public String show(@PathVariable(name = "id") Integer id, Model model) {
         Review review = reviewRepository.getReferenceById(id);
         
         model.addAttribute("review", review);
         
         return "admin/reviews/show";
     }  
     
     @GetMapping("/{id}/edit")
     public String edit(@PathVariable(name = "id") Integer id, Model model) {
         Review review = reviewRepository.getReferenceById(id);
         ReviewEditForm reviewEditForm = new ReviewEditForm(review.getId(), review.getScore(), review.getContent());
         
         model.addAttribute("reviewEditForm", reviewEditForm);
         model.addAttribute("review", review); 

         return "admin/reviews/edit";
     }  
     
     @PostMapping("/{id}/update")
     public String update(@PathVariable(name = "id") Integer id,
                          @ModelAttribute @Validated ReviewEditForm reviewEditForm,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          Model model) {
    	 
         Review review = reviewRepository.getReferenceById(id);

         if (bindingResult.hasErrors()) {
             model.addAttribute("review", review);
             return "admin/reviews/edit";
         }

         reviewService.update(reviewEditForm);
         redirectAttributes.addFlashAttribute("successMessage", "レビューを編集しました。");

         return "redirect:/admin/reviews";
     }
      
     
     @PostMapping("/{id}/delete")
     public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
         reviewRepository.deleteById(id);

         redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");

         return "redirect:/admin/reviews";
     }
 }

