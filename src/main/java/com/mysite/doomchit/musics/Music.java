package com.mysite.doomchit.musics;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Music {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;      // 곡 제목
    private String artist;     // 아티스트명
    private String albumName;  // 앨범명
    private String imageUrl;   // 앨범 이미지 URL
    private Integer rank;      // 차트 순위
}