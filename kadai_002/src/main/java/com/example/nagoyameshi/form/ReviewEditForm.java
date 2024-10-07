package com.example.nagoyameshi.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewEditForm {
  @NotNull
  private Integer id;
	
  @NotNull(message = "評価数を選択してください。")
  @Range(min=1, max=5, message = "評価は１～５で選択してください。")
  private Integer score;
  
  @NotBlank(message = "評価コメントを入力してください。")
  @Length(max = 300, message = "評価コメントは300文字以内で入力してください。")
  private String content;
  
}