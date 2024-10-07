package com.example.nagoyameshi.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.nagoyameshi.entity.Shop;
import com.example.nagoyameshi.repository.ShopRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;

@Controller
public class HomeController {
     private final ShopRepository shopRepository;        
     
     public HomeController(ShopRepository shopRepository) {
         this.shopRepository = shopRepository;            
     }    
    
    @GetMapping("/")   
     public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {   
         if (userDetailsImpl != null && "ROLE_ADMIN".equals(userDetailsImpl.getUser().getRole().getName())) {
             return "redirect:/admin";
         }
         List<Shop> newShops = shopRepository.findTop10ByOrderByCreatedAtDesc();
         
         model.addAttribute("newShops", newShops);        
        
        return "index";
    }   
}
