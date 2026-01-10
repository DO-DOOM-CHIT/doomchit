package com.mysite.doomchit.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserForm {
    @NotBlank(message = "아이디를 입력해 주세요.")
    private String user_id;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String user_pwd1;
    
    @NotBlank(message = "비밀번호 확인을 진행해 주세요.")
    private String user_pwd2;

    @Size(max = 50)
    @NotBlank(message = "닉네임을 입력해 주세요.")
    private String username;
}