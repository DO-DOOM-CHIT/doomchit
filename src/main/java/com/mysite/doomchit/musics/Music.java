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
    private Long mno; // 음악 고유 번호 (PK) - ERD에 맞춰 Long으로 변경

    // 주요 정보
    @Column(nullable = false, length = 150)
    private String title; // 곡 제목

    @Column(nullable = false, length = 100)
    private String artist; // 아티스트명

    @Column(length = 150)
    private String albumName; // 앨범명 (ERD: albumTitle)

    @Column(length = 300, name = "image")
    private String imageUrl; // 앨범 이미지 URL (ERD: image)

    // ERD 추가 필드
    private Integer duration; // 재생 시간 (초 단위)

    @Column(length = 50)
    private String genre; // 장르

    private LocalDate relDate; // 발매일 (DATE)

    @Column(length = 100)
    private String publisher; // 발매사

    @Column(length = 100)
    private String agency; // 기획사

    @Column(length = 100)
    private String lyricist; // 작사가

    @Column(length = 100)
    private String composer; // 작곡가

    // 차트용 필드 (ERD에는 없지만 차트 순위 표시에 필요할 수 있음. 혹은 musicId로 대체?)
    // ERD에 musicId(API고유번호)가 있으므로 추가
    private Long musicId; // 멜론/스포티파이 등 외부 API의 고유 ID

    private Long albumId; // 앨범 고유 번호 (크롤링용)

    // 차트 순위 (DB 저장용이라기보단 뷰 용도지만 편의상 유지하거나, 매번 갱신되는 값임)
    // ERD에 'rank'는 없지만 화면 표시용으로 필요함. @Transient를 쓸 수도 있지만 일단 필드로 유지.
    private Integer rank;
}