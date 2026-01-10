package com.mysite.doomchit.users;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 =======================================================
    public User create(String userId, String userPwd, String username) {

        User user = new User();
        user.setUserId(userId);
        // 비밀번호 암호화
        user.setUserPwd(passwordEncoder.encode(userPwd));
        user.setUsername(username);
        userRepository.save(user);
        
        return user;
    }

    public void modify(User user, String username) {
        user.setUsername(username);
        userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }
}