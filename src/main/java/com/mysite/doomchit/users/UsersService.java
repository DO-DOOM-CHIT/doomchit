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

    public Users getMember(Integer mno) {
        Optional<Users> member = this.memberRepository.findById(mno);
        return member.orElse(null);
    }

    public void create(String email, String pwd, String mname) {
        Users m = new Users();
        m.setEmail(email);
        m.setPwd(pwd);
        m.setMname(mname);
        m.setCreDate(LocalDateTime.now());
        m.setModDate(LocalDateTime.now());
        this.memberRepository.save(m);
    }

    public void modify(Users member, String mname) {
        member.setMname(mname);
        member.setModDate(LocalDateTime.now());
        this.memberRepository.save(member);
    }

    public void delete(Users member) {
        this.memberRepository.delete(member);
    }
}