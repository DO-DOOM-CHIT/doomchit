package com.mysite.doomchit.reviews;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.doomchit.musics.Music;
import com.mysite.doomchit.users.Users;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
  // 특정 음악의 리뷰 목록
  List<Review> findByMusic(Music music);

  // 특정 음악에 대해 특정 유저가 이미 리뷰를 작성했는지 체크하는 용도
  boolean existsByMusicAndUser(Music music, Users user);
}
