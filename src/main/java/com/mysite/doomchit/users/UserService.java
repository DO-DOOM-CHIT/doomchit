package com.mysite.doomchit.users;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {
	
	// ----------------------------------------------------
	// Service : 비즈니스 로직, 중복체크, 쿼리실행, 트랜잭션 관리
	// ----------------------------------------------------
	
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

    // 아이디 중복 체크
    @Transactional(readOnly = true)
    public boolean existsUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }

    // 닉네임 중복 체크
    @Transactional(readOnly = true)
    public boolean existsUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Transactional(readOnly = true)
    public Users getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String userId = auth.getName();

        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }


}