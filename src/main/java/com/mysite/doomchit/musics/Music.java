package com.mysite.doomchit.musics;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "musics")
public class Music {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mno; // 음악 고유 번호 (PK)

    private Long music_id; // 음악 API 고유 번호

    @Column(nullable = false, length = 100)
    private String artist; // 가수명

    @Column(nullable = false, length = 150)
    private String title; // 음악 제목

    @Column(nullable = false)
    private Integer duration; // 음악 길이

    @Column(length = 50)
    private String genre; // 장르

    private LocalDate rel_date; // 발매일 (DATE)

    @Column(length = 150)
    private String album_title; // 앨범명

    @Column(length = 300)
    private String image; // 앨범 이미지 URL

    @Column(length = 100)
    private String publisher; // 발매사

    @Column(length = 100)
    private String agency; // 기획사

    @Column(length = 100)
    private String lyricist; // 작사가

    @Column(length = 100)
    private String composer; // 작곡가

    // 로직용 추가 필드 (ERD 이미지에는 없지만 크롤링에 필요)
    private Long album_id;
    private Integer rank;
}