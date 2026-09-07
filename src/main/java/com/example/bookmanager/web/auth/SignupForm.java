package com.example.bookmanager.web.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupForm {

    @NotBlank(message = "メールアドレスを入力してください")
    @Email(message = "有効なメールアドレスの形式で入力してください")
    private String email;

    @NotBlank(message = "パスワードを入力してください")
    @Size(min = 6, message = "パスワードは６文字以上で入力してください")
    private String password;
}
