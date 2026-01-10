package com.mysite.doomchit.users;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/member")
@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;

    // 1. 회원 목록
    @GetMapping("/main")
    public String list(Model model) {
        List<User> userList = userService.getList();
        model.addAttribute("userList", userList);
        return "user_list";
    }

    // 3. 회원 등록 화면
    @GetMapping("/signup")
    public String userCreate(UserForm userForm) {
        return "user_form";
    }

    // 4. 회원 등록 처리
    @PostMapping("/create")
    public String UserCreate(@Valid UserForm userForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user_form";
        }
        userService.create(userForm.getUserId(), userForm.getUserPwd(), userForm.getUsername());
        return "redirect:/user/list";
    }
}