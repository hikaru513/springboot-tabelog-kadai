package com.example.nagoyameshi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.CategoryShopRelation;
import com.example.nagoyameshi.entity.Shop;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.CategoryShopRelationRepository;

@Service
public class CategoryShopRelationService {
    private final CategoryShopRelationRepository categoryShopRelationRepository;
    private final CategoryRepository categoryRepository; 

    public CategoryShopRelationService(CategoryShopRelationRepository categoryShopRelationRepository, CategoryRepository categoryRepository) {
        this.categoryShopRelationRepository = categoryShopRelationRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public void create(List<Integer> categoryIds, Shop shop) {
        for (Integer categoryId : categoryIds) {
            if (categoryId == null) {
                continue;
            }
            
            CategoryShopRelation categoryShopRelation = new CategoryShopRelation();
            Category category = categoryRepository.getReferenceById(categoryId);
            
            categoryShopRelation.setShop(shop);
            categoryShopRelation.setCategory(category);
            
            categoryShopRelationRepository.save(categoryShopRelation);            
        }
    }
    @Transactional
    public void update(List<Integer> newCategoryIds, Shop shop) { 
        List<CategoryShopRelation> existingCategoryShopRelations = categoryShopRelationRepository.findByShopOrderByIdAsc(shop);
        List<Integer> existingCategoryIds = categoryShopRelationRepository.findCategoryIdsByShopOrderByIdAsc(shop);

        if (newCategoryIds == null) {
            // newCategoryIdsがnullの場合は全てのエンティティを削除する
            for (CategoryShopRelation existingCategoryShopRelation : existingCategoryShopRelations) {
            	categoryShopRelationRepository.delete(existingCategoryShopRelation);
            }
        } else {
            // 既存のエンティティが新しいリストに存在しない場合は削除する
            for (CategoryShopRelation existingCategoryShopRelation : existingCategoryShopRelations) {
                if (!newCategoryIds.contains(existingCategoryShopRelation.getCategory().getId())) {
                	categoryShopRelationRepository.delete(existingCategoryShopRelation);
                }
            }

            // 新しいIDが既存のエンティティに存在しない場合は新たにエンティティを作成する
            for (Integer newCategoryId : newCategoryIds) {
                if (newCategoryId != null && !existingCategoryIds.contains(newCategoryId)) {
                	CategoryShopRelation categoryShopRelation = new CategoryShopRelation();
                    Category category = categoryRepository.getReferenceById(newCategoryId);

                    categoryShopRelation.setShop(shop);
                    categoryShopRelation.setCategory(category);

                    categoryShopRelationRepository.save(categoryShopRelation);
                }
            }
        }
    }   
    
    @Transactional
    public void deleteByShop(Shop shop) {
    	categoryShopRelationRepository.deleteByShop(shop);
    }   
    
    @Transactional
    public void deleteByCategory(Category category) {
    	categoryShopRelationRepository.deleteByCategory(category);
    }
}