package com.mysite.doomchit.users;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Integer> {
	Optional<Users> findByUserId(String userId);
}