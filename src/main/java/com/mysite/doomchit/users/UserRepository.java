package com.mysite.doomchit.users;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Integer> {
	
	// 로그인 / 현재 사용자 조회
	Optional<Users> findByUserId(String userId);
	
    // 아이디 중복 체크
    boolean existsByUserId(String userId);
    
    // 닉네임 중복 체크
    boolean existsByUsername(String username);
}