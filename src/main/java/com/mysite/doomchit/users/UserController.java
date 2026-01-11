package com.mysite.doomchit.users;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/doomchit")
@RequiredArgsConstructor
@Controller
public class UserController {

	// ----------------------------------------------------
	// Controller : 요청 / 응답, Validation 결과 처리, 에러 종류 결정
	// ----------------------------------------------------
	
    private final UserService userService;

    // 회원가입 ===========================================
    @GetMapping("/signup")
    public String signup(UserForm userForm) {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserForm userForm, BindingResult bindingResult) {
    	
    	// 기본 Validation
        if (bindingResult.hasErrors()) {
            return "signup";
        }
        
        // 비밀번호 확인 체크
        if(!userForm.getUserPwd1().equals(userForm.getUserPwd2())) {
        	bindingResult.rejectValue("userPwd2", "passwordIncorrect", "비밀번호가 일치하지 않습니다.");
        	return "signup";
        }
        
        // UX용 아이디 중복 체크
        if (userService.existsUserId(userForm.getUserId())) {
            bindingResult.rejectValue(
                "userId",
                "duplicate",
                "이미 사용 중인 아이디입니다."
            );
            return "signup";
        }
        
        // UX용 닉네임 중복 체크
        if (userService.existsUsername(userForm.getUsername())) {
            bindingResult.rejectValue(
                "username",
                "duplicate",
                "이미 사용 중인 닉네임입니다."
            );
            return "signup";
        }
        
        // 회원가입 시도
        try {
        	userService.create(
        			userForm.getUserId(), userForm.getUserPwd1(), userForm.getUsername()
        			);
        } catch(DataIntegrityViolationException e) {
        	bindingResult.rejectValue("userId", "duplicate", "중복된 값이 존재합니다.");
        	
        	return "signup";
        	
            }
        
        return "redirect:/";
    }
    
    // 로그인 ===========================================
    // Post는 Spring Security가 가로채서 처리함
    @GetMapping("/login")
    public String login() {
    	return "login";
    }
    
}