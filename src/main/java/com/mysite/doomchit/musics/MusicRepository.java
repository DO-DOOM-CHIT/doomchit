package com.mysite.doomchit.musics;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicRepository extends JpaRepository<Music, Long> {
  Optional<Music> findByTitleAndArtist(String title, String artist);
}
