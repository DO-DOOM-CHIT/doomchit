package com.mysite.doomchit.users;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    List<Users> findByMnameContaining(String keyword);
}