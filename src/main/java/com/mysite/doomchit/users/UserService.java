package com.mysite.doomchit.users;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 =======================================================
    public User create(String user_id, String user_pwd, String username) {

        User user = new User();
        user.setUser_id(user_id);
        // 비밀번호 암호화
        user.setUser_pwd(passwordEncoder.encode(user_pwd));
        user.setUsername(username);
        userRepository.save(user);
        
        return user;
    }

}