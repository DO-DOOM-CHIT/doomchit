package com.mysite.doomchit.reviews;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mysite.doomchit.DataNotFoundException;
import com.mysite.doomchit.musics.Music;
import com.mysite.doomchit.users.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReviewService {

  private final ReviewRepository reviewRepository;

  // 리뷰 생성
  public Review create(Music music, User user, String content) {
    Review review = new Review();
    review.setMusic(music);
    review.setUser(user);
    review.setContent(content);
    review.setCre_date(LocalDateTime.now());
    reviewRepository.save(review);
    return review;
  }

  // 특정 음악의 리뷰 목록 조회
  public List<Review> getList(Music music) {
    return reviewRepository.findByMusic(music);
  }

  // 리뷰 단건 조회
  public Review getReview(Integer rno) {
    Optional<Review> review = reviewRepository.findById(rno);
    if (review.isPresent()) {
      return review.get();
    } else {
      throw new DataNotFoundException("review not found");
    }
  }

  // 리뷰 수정
  public void modify(Review review, String content) {
    review.setContent(content);
    review.setMod_date(LocalDateTime.now());
    reviewRepository.save(review);
  }

  // 리뷰 삭제
  public void delete(Review review) {
    reviewRepository.delete(review);
  }
}
