package com.example.nagoyameshi.form;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationRegisterForm {
	private Integer shopId;
    
    private Integer userId;    
        
    private String reservationDate;   
    
    private LocalTime reservationTime; 
    
    private Integer numberOfPeople;
    
}