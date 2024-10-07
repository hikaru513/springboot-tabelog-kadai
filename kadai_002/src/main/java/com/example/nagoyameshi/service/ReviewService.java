package com.example.nagoyameshi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.Shop;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReviewEditForm;
import com.example.nagoyameshi.form.ReviewForm;
import com.example.nagoyameshi.repository.ReviewRepository;

@Service
public class ReviewService {
  private final ReviewRepository reviewRepository;
  
  public ReviewService(ReviewRepository reviewRepository) {
	  this.reviewRepository = reviewRepository;
  }
  
//  登録用
  @Transactional
  public void create(Shop shop, User user, ReviewForm reviewForm) {
	  Review review = new Review();
	  
	  review.setShop(shop);
	  review.setUser(user);
	  review.setScore(reviewForm.getScore());
	  review.setContent(reviewForm.getContent());
	  
	  reviewRepository.save(review);
	  
  }
  
//  更新用
  @Transactional
  public void update(ReviewEditForm reviewEditForm) {
	  Review review = reviewRepository.getReferenceById(reviewEditForm.getId());
	  
	  review.setScore(reviewEditForm.getScore());
	  review.setContent(reviewEditForm.getContent());
	  
	  reviewRepository.save(review);
  }
  
  
  public boolean reviewJudge(Shop shop, User user) {
	  Review review = reviewRepository.findByUserAndShop(user, shop);
	  return review != null;
  }
}