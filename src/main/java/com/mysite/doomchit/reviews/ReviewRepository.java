package com.mysite.doomchit.reviews;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.doomchit.musics.Music;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
  // 특정 음악에 달린 리뷰 목록 조회
  List<Review> findByMusic(Music music);
}
