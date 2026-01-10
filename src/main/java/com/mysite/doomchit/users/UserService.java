package com.mysite.doomchit.users;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 =======================================================
    public Users create(String userId, String userPwd, String username) {

        Users user = new Users();
        user.setUserId(userId);
        // 비밀번호 암호화
        user.setUserPwd(passwordEncoder.encode(userPwd));
        user.setUsername(username);
        userRepository.save(user);
        
        return user;
    }
    
    public Users getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String userId = auth.getName();

        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }


}