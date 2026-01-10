package com.mysite.doomchit.users;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/doomchit/*")
@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;

    // 회원가입 ===========================================
    @GetMapping("/signup")
    public String signup(UserForm userForm) {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("userForm") UserForm userForm, BindingResult bindingResult) {
    	
        if (bindingResult.hasErrors()) {
            return "signup";
        }
        
        if(!userForm.getUser_pwd1().equals(userForm.getUser_pwd2())) {
        	bindingResult.rejectValue("user_pwd2", "passwordIncorrect", "비밀번호가 일치하지 않습니다.");
        	return "signup";
        }
        
        userService.create(userForm.getUser_id(), userForm.getUser_pwd1(), userForm.getUsername());
        return "redirect:/";
    }
}