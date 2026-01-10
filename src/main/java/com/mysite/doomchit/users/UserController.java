package com.mysite.doomchit.users;

import org.springframework.stereotype.Controller;
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
    public String signup(@Valid UserForm userForm, BindingResult bindingResult) {
    	
        if (bindingResult.hasErrors()) {
            return "signup";
        }
        
        if(!userForm.getUserPwd1().equals(userForm.getUserPwd2())) {
        	bindingResult.rejectValue("userPwd2", "passwordIncorrect", "비밀번호가 일치하지 않습니다.");
        	return "signup";
        }
        
        userService.create(userForm.getUserId(), userForm.getUserPwd1(), userForm.getUsername());
        return "redirect:/";
    }
    
    // 로그인 ===========================================
    // Post는 Spring Security가 가로채서 처리함
    @GetMapping("/login")
    public String login() {
    	return "login";
    }
    
}