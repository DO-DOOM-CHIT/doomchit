package com.mysite.doomchit.likes;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.doomchit.musics.Music;
import com.mysite.doomchit.users.Users;

public interface LikesRepository extends JpaRepository<Likes, Long> {
  Optional<Likes> findByUserAndMusic(Users user, Music music);

  long countByMusic(Music music);

  boolean existsByUserAndMusic(Users user, Music music);

  java.util.List<Likes> findByUserOrderByCreDateDesc(Users user);
}
