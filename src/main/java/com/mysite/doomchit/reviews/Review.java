package com.mysite.doomchit.reviews;

import java.math.BigDecimal;
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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
// 한 유저가 하나의 음악에 여러 개의 리뷰를 달지 못하게 하는 제약
@Table(name = "reviews")
public class Review {

  // 리뷰 고유 번호
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer rno;

  // 리뷰 내용
  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;
  
  // 평점 (1.0 ~ 5.0, 0.5 단위)
  @DecimalMin(value = "1.0", inclusive = true)        // 최저점 1.0
  @DecimalMax(value = "5.0", inclusive = true)        // 최고점 5.0
  @Column(precision = 2, scale = 1, nullable = false) // 0.5단위
  private BigDecimal rating;

  // 리뷰 작성일
  @CreationTimestamp
  @Column(name = "cre_date", nullable = false, updatable = false)
  private LocalDateTime creDate;

  // 리뷰 수정일
  @UpdateTimestamp
  @Column(name = "mod_date", nullable = false)
  private LocalDateTime modDate;

  @ManyToOne
  @JoinColumn(name = "uno", nullable = false, foreignKey = @ForeignKey(name = "fk_reviews_uno"))
  private Users user; // 작성자 (users 테이블과 조인)

  @ManyToOne
  @JoinColumn(name = "mno", nullable = false, foreignKey = @ForeignKey(name = "fk_reviews_mno"))
  private Music music; // 리뷰 대상 음악 (musics 테이블과 조인)
}
