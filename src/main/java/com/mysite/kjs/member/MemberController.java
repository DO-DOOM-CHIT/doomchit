package com.mysite.kjs.member;

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
public class MemberController {
    private final MemberService memberService;

    // 1. 회원 목록
    @GetMapping("/list")
    public String list(Model model) {
        List<Member> memberList = this.memberService.getList();
        model.addAttribute("memberList", memberList);
        return "member_list";
    }

    // 2. 상세 조회 (이름 클릭 시 이동)
    @GetMapping(value = "/detail/{mno}")
    public String detail(Model model, @PathVariable("mno") Integer mno) {
        Member member = this.memberService.getMember(mno);
        model.addAttribute("member", member);
        return "member_detail";
    }

    // 3. 회원 등록 화면
    @GetMapping("/create")
    public String memberCreate(MemberForm memberForm) {
        return "member_form";
    }

    // 4. 회원 등록 처리
    @PostMapping("/create")
    public String memberCreate(@Valid MemberForm memberForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "member_form";
        }
        this.memberService.create(memberForm.getEmail(), memberForm.getPwd(), memberForm.getMname());
        return "redirect:/member/list";
    }
}