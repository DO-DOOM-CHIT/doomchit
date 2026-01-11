package com.mysite.doomchit.likes;

import com.mysite.doomchit.musics.Music;
import com.mysite.doomchit.users.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "likes")
public class Likes {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long lno;

  @Column(name = "cre_date")
  private LocalDateTime creDate;

  @ManyToOne
  @JoinColumn(name = "uno")
  private Users user;

  @ManyToOne
  @JoinColumn(name = "mno")
  private Music music;
}
