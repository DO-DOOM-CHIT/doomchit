package com.mysite.doomchit.users;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UsersService {
    private final UsersRepository UsersRepository;

    public List<Users> getList() {
        return this.UsersRepository.findAll();
    }

    public Users getMember(Integer uno) {
        Optional<Users> users = this.UsersRepository.findById(uno);
        return users.orElse(null);
    }

    public void create(String userId, String userPwd, String username) {
        Users u = new Users();
        u.setUserId(userId);
        u.setUserPwd(userPwd);
        u.setUsername(username);
        u.setCreDate(LocalDateTime.now());
        this.UsersRepository.save(u);
    }

    public void modify(Users users, String username) {
        users.setUsername(username);
        this.UsersRepository.save(users);
    }

    public void delete(Users users) {
        this.UsersRepository.delete(users);
    }
}