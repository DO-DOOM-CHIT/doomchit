package com.mysite.kjs.member;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public List<Member> getList() {
        return this.memberRepository.findAll();
    }

    public Member getMember(Integer mno) {
        Optional<Member> member = this.memberRepository.findById(mno);
        return member.orElse(null);
    }

    public void create(String email, String pwd, String mname) {
        Member m = new Member();
        m.setEmail(email);
        m.setPwd(pwd);
        m.setMname(mname);
        m.setCreDate(LocalDateTime.now());
        m.setModDate(LocalDateTime.now());
        this.memberRepository.save(m);
    }

    public void modify(Member member, String mname) {
        member.setMname(mname);
        member.setModDate(LocalDateTime.now());
        this.memberRepository.save(member);
    }

    public void delete(Member member) {
        this.memberRepository.delete(member);
    }
}