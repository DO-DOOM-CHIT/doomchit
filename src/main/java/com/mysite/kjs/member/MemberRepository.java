package com.mysite.kjs.member;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    List<Member> findByMnameContaining(String keyword);
}