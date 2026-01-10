package com.mysite.doomchit.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserForm {
    @NotEmpty(message = "이메일은 필수항목입니다.")
    @Email
    private String userId;

    @NotEmpty(message = "비밀번호는 필수항목입니다.")
    private String userPwd;

    @Size(max = 45)
    @NotEmpty(message = "닉네임은 필수항목입니다.")
    private String username;
}