package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.CategoryShopRelation;
import com.example.nagoyameshi.entity.Shop;

@Repository
public interface CategoryShopRelationRepository extends JpaRepository<CategoryShopRelation, Integer> {
    List<CategoryShopRelation> findByShopOrderByIdAsc(Shop shop);
    
    @Query("SELECT cr.category.id FROM CategoryShopRelation cr WHERE cr.shop = :shop ORDER BY cr.id ASC")
    public List<Integer> findCategoryIdsByShopOrderByIdAsc(@Param("shop") Shop shop);
    
    public void deleteByShop(Shop shop);
    public void deleteByCategory(Category category);
}