package com.mysite.doomchit.reviews;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mysite.doomchit.musics.Music;
import com.mysite.doomchit.users.Users;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // 특정 음악 + 유저 리뷰 조회 (등록 / 수정 분기용)
    Optional<Review> findByMusicAndUser(Music music, Users user);

    // 특정 음악 리뷰 목록 (기본)
    List<Review> findByMusic(Music music);

    // 특정 음악 리뷰 목록 (최신순)
    List<Review> findByMusicOrderByCreDateDesc(Music music);

    // 리뷰 개수
    long countByMusic(Music music);

    // 평균 평점 (소수점 한 자리로 반올림, JPQL 문법 사용)
    @Query("""
             SELECT ROUND(AVG(r.rating), 1)
             FROM Review r
             WHERE r.music = :music
            """)
    BigDecimal getAverageRatingByMusic(@Param("music") Music music);
}
