package com.mysite.doomchit.reviews;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysite.doomchit.musics.Music;
import com.mysite.doomchit.musics.MusicService;
import com.mysite.doomchit.users.Users;
// import com.mysite.doomchit.users.UserService; // 필요 시 추가

import lombok.RequiredArgsConstructor;

@RequestMapping("/doomchit/reviews")
@RequiredArgsConstructor
@Controller
public class ReviewController {

  private final ReviewService reviewService;
  private final MusicService musicService;
  // private final UserService userService; // 작성자 정보를 위해 필요

  @PostMapping("/create")
  public String createReview(@RequestParam("title") String title,
      @RequestParam("artist") String artist,
      @RequestParam("album") String album,
      @RequestParam("image") String image,
      @RequestParam("content") String content) {

    // 1. 노래 정보 DB 확인 및 저장 (없으면 저장)
    Music music = musicService.getOrCreateMusic(title, artist, album, image);

    // 2. 작성자 정보 가져오기 (임시로 null 또는 로그인 구현된 유저)
    Users user = null;
    // user = userService.getUser(principal.getName()); // 로그인 연동 시 사용

    // 3. 리뷰 저장
    //reviewService.create(music, user, content);

    return "redirect:/doomchit/main"; // 작성 후 메인으로 이동
  }
}
