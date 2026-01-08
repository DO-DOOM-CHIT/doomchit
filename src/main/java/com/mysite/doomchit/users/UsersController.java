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
public class UsersController {
    private final UsersService UsersService;

    // 1. 회원 목록
    @GetMapping("/main")
    public String list(Model model) {
        List<Users> usersList = this.UsersService.getList();
        model.addAttribute("usersList", usersList);
        return "users_list";
    }

    // 3. 회원 등록 화면
    @GetMapping("/signup")
    public String usersCreate(UsersForm UsersForm) {
        return "users_form";
    }

    // 4. 회원 등록 처리
    @PostMapping("/create")
    public String UsersCreate(@Valid UsersForm usersForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "users_form";
        }
        this.UsersService.create(usersForm.getUserId(), usersForm.getUserPwd(), usersForm.getUsername());
        return "redirect:/users/list";
    }
}