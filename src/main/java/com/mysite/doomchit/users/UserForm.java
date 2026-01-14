package com.mysite.doomchit.users;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserForm {
    @NotBlank(message = "아이디를 입력해 주세요.")
    private String userId;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String userPwd1;
    
    @NotBlank(message = "비밀번호 확인을 진행해 주세요.")
    private String userPwd2;

    @NotBlank(message = "닉네임을 입력해 주세요.")
    private String username;
}