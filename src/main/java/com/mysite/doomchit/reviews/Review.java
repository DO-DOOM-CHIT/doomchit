package com.mysite.doomchit.reviews;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.mysite.doomchit.musics.Music;
import com.mysite.doomchit.users.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
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

  // 리뷰 고유 번호
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer rno;

  // 리뷰 내용
  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  // 리뷰 작성일
  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime cre_date;

  // 리뷰 수정일
  @UpdateTimestamp
  @Column(nullable = false)
  private LocalDateTime mod_date;

  @ManyToOne
  @JoinColumn(name = "uno", nullable = false, foreignKey = @ForeignKey(name = "fk_reviews_uno"))
  private Users user; // 작성자 (users 테이블과 조인)

  @ManyToOne
  @JoinColumn(name = "mno", nullable = false, foreignKey = @ForeignKey(name = "fk_reviews_mno"))
  private Music music; // 리뷰 대상 음악 (musics 테이블과 조인)
}
