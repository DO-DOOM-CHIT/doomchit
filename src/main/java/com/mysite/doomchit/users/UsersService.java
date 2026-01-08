package com.mysite.doomchit.users;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UsersService {
    private final UsersRepository memberRepository;

    public List<Users> getList() {
        return this.memberRepository.findAll();
    }

    public Users getMember(Integer uno) {
        Optional<Users> users = this.memberRepository.findById(uno);
        return users.orElse(null);
    }

    public void create(String userId, String userPwd, String username) {
        Users u = new Users();
        u.setUserId(userId);
        u.setUserPwd(userPwd);
        u.setUsername(username);
        u.setCreDate(LocalDateTime.now());
        this.memberRepository.save(u);
    }

    public void modify(Users users, String username) {
        users.setUsername(username);
        this.memberRepository.save(users);
    }

    public void delete(Users users) {
        this.memberRepository.delete(users);
    }
}