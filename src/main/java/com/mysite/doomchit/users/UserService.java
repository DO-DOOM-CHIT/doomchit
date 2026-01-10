package com.mysite.doomchit.users;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public List<User> getList() {
        return userRepository.findAll();
    }

    public User getMember(Integer uno) {
        Optional<User> user = userRepository.findById(uno);
        return user.orElse(null);
    }

    public void create(String userId, String userPwd, String username) {
        User u = new User();
        u.setUserId(userId);
        u.setUserPwd(userPwd);
        u.setUsername(username);
        u.setCreDate(LocalDateTime.now());
        userRepository.save(u);
    }

    public void modify(User user, String username) {
        user.setUsername(username);
        userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }
}