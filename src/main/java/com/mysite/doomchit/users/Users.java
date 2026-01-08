package com.mysite.doomchit.users;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uno")
    private Integer uno;

    @Column(length = 50, nullable = false, unique = true)
    private String userId;

    @Column(length = 100, nullable = false)
    private String userPwd;

    @Column(length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "cre_Date")
    private LocalDateTime creDate = LocalDateTime.now();

}