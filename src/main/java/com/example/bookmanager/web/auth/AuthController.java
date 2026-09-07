package com.example.bookmanager.web.auth;

import com.example.bookmanager.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginForm(){
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm(@ModelAttribute("signupForm") SignupForm form){
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(
            @Validated @ModelAttribute("signupForm") SignupForm form,
            BindingResult bindingResult,
            Model model){

        if (bindingResult.hasErrors()){
            return "signup";
        }

        try {
            userService.registerUser(form.getEmail(), form.getPassword());
        } catch (IllegalArgumentException e){
            return "signup";
        }

        return "redirect:/login?signupSuccess";
    }
}
