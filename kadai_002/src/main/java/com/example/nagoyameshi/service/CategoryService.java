package com.example.nagoyameshi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.form.CategoryEditForm;
import com.example.nagoyameshi.form.CategoryRegisterForm;
import com.example.nagoyameshi.repository.CategoryRepository;

@Service
public class CategoryService {
	private final CategoryRepository categoryRepository;    
    
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;        
    }    
    
    @Transactional
    public void create(CategoryRegisterForm categoryRegisterForm) {
    	
    	if (categoryRepository.existsByName(categoryRegisterForm.getName())) {
            throw new IllegalArgumentException("このカテゴリは既に登録してあります");
        }
        Category category = new Category();        
        
        category.setName(categoryRegisterForm.getName());                
                    
        categoryRepository.save(category);
    }  
    
    @Transactional
    public void updateCategory(Integer id, String newName) {
        if (categoryRepository.existsByName(newName) && !categoryRepository.getReferenceById(id).getName().equals(newName)) {
            throw new IllegalArgumentException("このカテゴリは既に登録してあります");
        }
        Category category = categoryRepository.getReferenceById(id);
        category.setName(newName);        
        categoryRepository.save(category);
    }
    
    @Transactional
    public void update(CategoryEditForm categoryEditForm) {
        Category category = categoryRepository.getReferenceById(categoryEditForm.getId());
        
        category.setName(categoryEditForm.getName());                
                    
        categoryRepository.save(category);
    }    
}

