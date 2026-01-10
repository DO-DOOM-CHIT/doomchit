package com.mysite.doomchit.users;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    // 유저 고유 번호
    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AI
    private Integer uno;

    // 로그인 아이디
    @Column(length = 50, nullable = false, unique = true)
    private String user_id;

    // 로그인 비밀번호
    @Column(length = 100, nullable = false)
    private String user_pwd;

    // 닉네임
    @Column(length = 50, nullable = false, unique = true)
    private String username;

    // 가입일
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime cre_date;

}