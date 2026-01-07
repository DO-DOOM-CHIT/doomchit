package com.mysite.kjs;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mysite.kjs.member.Member;
import com.mysite.kjs.member.MemberRepository;

@SpringBootTest(classes = SbbApplication.class)
class ApplicationTests {

    @Autowired
    private MemberRepository memberRepository;

    //@Test
    void test1_saveSamples() {
        // 1. 테이블에 샘플데이터 5개 저장
        for (int i = 1; i <= 5; i++) {
            Member m = new Member();
            m.setEmail("test" + i + "@hkit.com");
            m.setPwd("1234");
            m.setMname("유저" + i);
            this.memberRepository.save(m);
        }
        System.out.println("샘플 데이터 5개 저장 완료");
    }

    //@Test
    void test2_findAll() {
        // 2. 테이블에 저장한 정보를 전체출력
        List<Member> all = this.memberRepository.findAll();
        System.out.println("전체 회원 목록 출력");
        all.forEach(m -> System.out.println("ID: " + m.getMno() + ", Name: " + m.getMname()));
    }

    //@Test
    void test3_findById() {
        // 3. 테이블의 특정 PK(mno=1)로 조회한 결과를 출력
        Optional<Member> om = this.memberRepository.findById(1);
        om.ifPresent(m -> System.out.println("조회 결과: " + m.getEmail()));
    }

    //@Test
    void test4_modify() {
        // 4. 테이블의 원하는 자료를 수정 (mno=1 유저 이름 변경)
        Optional<Member> om = this.memberRepository.findById(1);
        om.ifPresent(m -> {
            m.setMname("이름수정완료");
            this.memberRepository.save(m);
            System.out.println("수정 확인: " + m.getMname());
        });
    }

    //@Test
    void test5_delete() {
        // 5. 테이블의 원하는 자료를 삭제 (mno=2 유저 삭제)
        Optional<Member> om = this.memberRepository.findById(2);
        om.ifPresent(m -> {
            this.memberRepository.delete(m);
            System.out.println("삭제 완료: mno=2");
        });
    }

    //@Test
    void test6_findByMname() {
        // 6. 이름에 특정문자('유저')가 포함된 이름을 모두 출력
        List<Member> searchList = this.memberRepository.findByMnameContaining("%유저%");
        System.out.println("'유저' 포함 검색 결과 출력");
        searchList.forEach(m -> System.out.println(m.getMname()));
    }
}