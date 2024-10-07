package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.nagoyameshi.entity.Shop;

public interface ShopRepository extends JpaRepository<Shop, Integer> {
	public Page<Shop> findByNameLike(String keyword, Pageable pageable);
	
    public Page<Shop> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword, Pageable pageable);  
    public Page<Shop> findByNameLikeOrAddressLikeOrderByPriceAsc(String nameKeyword, String addressKeyword, Pageable pageable);  
    public Page<Shop> findByAddressLikeOrderByCreatedAtDesc(String area, Pageable pageable);
    public Page<Shop> findByAddressLikeOrderByPriceAsc(String area, Pageable pageable);
    public Page<Shop> findByPriceLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable);
    public Page<Shop> findByPriceLessThanEqualOrderByPriceAsc(Integer price, Pageable pageable);
    public Page<Shop> findAllByOrderByCreatedAtDesc(Pageable pageable);
    public Page<Shop> findAllByOrderByPriceAsc(Pageable pageable);
    
    public List<Shop> findTop10ByOrderByCreatedAtDesc();
    
    @Query("SELECT r FROM Shop r LEFT JOIN r.categoryShopRelation cr LEFT JOIN cr.category c WHERE (r.name LIKE %:keyword% OR r.address LIKE %:keyword% OR c.name LIKE %:keyword%) AND (:area IS NULL OR r.address LIKE %:area%) AND (:categoryId IS NULL OR c.id = :categoryId)")
    Page<Shop> findByKeywordAndFilters(@Param("keyword") String keyword, @Param("area") String area, @Param("categoryId") Integer categoryId, Pageable pageable);

    
    @Query("SELECT r FROM Shop r JOIN r.categoryShopRelation cr WHERE cr.category.id = :categoryId")
    Page<Shop> findByCategoryId(@Param("categoryId") Integer categoryId,Pageable pageable);
}
