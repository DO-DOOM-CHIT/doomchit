package com.mysite.doomchit.users;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/doomchit")
@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;

    // 회원가입 ===========================================
    @GetMapping("/signup")
    public String userCreate(UserForm userForm) {
        return "signup";
    }

    @PostMapping("/signup")
    public String UserCreate(@Valid UserForm userForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup";
        }
        userService.create(userForm.getUserId(), userForm.getUserPwd(), userForm.getUsername());
        return "redirect:/user/list";
    }
}