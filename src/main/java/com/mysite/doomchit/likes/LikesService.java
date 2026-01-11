package com.mysite.doomchit.likes;

import org.springframework.stereotype.Service;

import com.mysite.doomchit.musics.Music;
import com.mysite.doomchit.users.Users;

import java.time.LocalDateTime;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LikesService {

  private final LikesRepository likesRepository;

  @Transactional
  public boolean toggleLike(Users user, Music music) {
    if (likesRepository.existsByUserAndMusic(user, music)) {
      Likes likes = likesRepository.findByUserAndMusic(user, music).get();
      likesRepository.delete(likes);
      return false; // 좋아요 취소됨
    } else {
      Likes likes = new Likes();
      likes.setUser(user);
      likes.setMusic(music);
      likes.setCreDate(LocalDateTime.now()); // Added line to set creDate
      likesRepository.save(likes);
      return true; // 좋아요 추가됨
    }
  }

  public boolean isLiked(Users user, Music music) {
    if (user == null)
      return false;
    return likesRepository.existsByUserAndMusic(user, music);
  }

  public long getLikeCount(Music music) {
    return likesRepository.countByMusic(music);
  }
}
