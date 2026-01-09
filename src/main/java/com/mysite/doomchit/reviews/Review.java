package com.mysite.doomchit.reviews;

import java.time.LocalDateTime;

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

@Getter
@Setter
@Entity
@Table(name = "reviews")
public class Review {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer rno; // 리뷰 고유 번호

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content; // 리뷰 내용

  private LocalDateTime creDate; // 리뷰 작성일

  private LocalDateTime modDate; // 리뷰 수정일

  @ManyToOne
  @JoinColumn(name = "uno")
  private Users users; // 작성자 (Users 테이블과 조인)

  @ManyToOne
  @JoinColumn(name = "mno") // Music 테이블의 id 컬럼과 매핑될 이름 (Music.java의 PK는 id임)
  private Music music; // 리뷰 대상 음악 (Music 테이블과 조인)
}
