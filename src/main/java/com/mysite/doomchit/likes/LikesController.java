package com.mysite.doomchit.likes;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysite.doomchit.musics.Music;
import com.mysite.doomchit.musics.MusicService;
import com.mysite.doomchit.users.UserService;
import com.mysite.doomchit.users.Users;

import lombok.RequiredArgsConstructor;

@RequestMapping("/doomchit/like")
@RequiredArgsConstructor
@Controller
public class LikesController {

  private final LikesService likesService;
  private final MusicService musicService;
  private final UserService userService;

  // 좋아요 토글 (기존 ReviewController에서 이사 옴)
  // URL: /doomchit/like/toggle/{mno}
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/toggle/{mno}")
  public String toggleLike(@PathVariable("mno") Long mno,
      @RequestParam(value = "redirectUrl", required = false) String redirectUrl) {
    Music music = musicService.getMusic(mno);
    Users user = userService.getCurrentUser();

    likesService.toggleLike(user, music);

    // 기본적으로 리뷰 페이지로 돌아가되, 만약 다른 곳에서 호출했다면 그쪽으로 리다이렉트 가능하도록 확장성 고려
    // (일단 기존 로직대로 리뷰 페이지로 복귀)
    return "redirect:/doomchit/reviews/" + mno;
  }
}
