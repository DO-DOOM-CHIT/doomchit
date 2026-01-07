package com.mysite.doomchit.users;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "members")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mno")
    private Integer mno;

    @Column(length = 45, nullable = false, unique = true)
    private String email;

    @Column(length = 45, nullable = false)
    private String pwd;

    @Column(length = 45, nullable = false)
    private String mname;

    @Column(name = "cre_date")
    private LocalDateTime creDate = LocalDateTime.now();

    @Column(name = "mod_date")
    private LocalDateTime modDate = LocalDateTime.now();
}