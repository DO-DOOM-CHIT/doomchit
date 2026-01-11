package com.mysite.doomchit;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mysite.doomchit.musics.Music;
import com.mysite.doomchit.musics.MusicService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final MusicService musicService;
    private final com.mysite.doomchit.likes.LikesRepository likesRepository;
    private final com.mysite.doomchit.users.UserService userService;
    private final com.mysite.doomchit.likes.LikesService likesService;

    @GetMapping("/")
    public String root() {
        return "redirect:/doomchit/main";
    }

    @GetMapping("/doomchit/main")
    public String main(Model model) {
        // 메인 페이지는 기본 정보만 빠르게 로딩 (0.1초)
        List<Music> chart = musicService.getMelonChartBasic();

        // 좋아요 카운트 채우기 (임시: N+1 문제 있지만 차트 개수가 적으므로 허용)
        for (Music m : chart) {
            // 차트의 musicId로 DB Music 찾아서 mno 가져오기 (만약 DB에 없으면 0)
            try {
                // findByMusicId는 Optional이 아닐 수 있음, MusicService 확인 필요
                // getOrCreate 로직이 있어야 mno가 생기는데, 차트는 실시간 크롤링이라 DB에 없을 수도 있음.
                // 만약 DB에 저장된 Music이라면 mno가 있을 것이고 그 mno로 count.
                // 하지만 차트 객체는 API에서 왔으므로 mno가 null일 수 있음.
                // 여기서는 MusicService의 getOrCreateMusicByMusicId를 쓰면 DB 저장이 일어나서 느려짐.
                // 일단 DB에 있는 경우만 count해야 함.
                Music dbMusic = musicService.findMusicByMusicId(m.getMusicId());
                if (dbMusic != null) {
                    m.setLikeCount(likesRepository.countByMusic(dbMusic));
                } else {
                    m.setLikeCount(0L);
                }
            } catch (Exception e) {
                m.setLikeCount(0L);
            }
        }

        model.addAttribute("pageName", "main");
        model.addAttribute("chart", chart);
        return "main";
    }

    @GetMapping("/doomchit/likes")
    public String likes(Model model) {
        List<Music> chart = new java.util.ArrayList<>();

        try {
            com.mysite.doomchit.users.Users user = userService.getCurrentUser();
            if (user != null) {
                chart = likesService.getLikedMusic(user);
            }
        } catch (Exception e) {
            // 비로그인 시 빈 목록
        }

        // 좋아요 카운트 채우기
        for (Music m : chart) {
            try {
                // 이미 DB에서 가져온 Music이므로 mno가 있음. countByMusic 바로 가능
                m.setLikeCount(likesRepository.countByMusic(m));
            } catch (Exception e) {
                m.setLikeCount(0L);
            }
        }

        model.addAttribute("pageName", "likes");
        model.addAttribute("chart", chart);
        return "likes";
    }

    // JSON 데이터 확인용 임시 페이지
    @org.springframework.web.bind.annotation.ResponseBody
    @GetMapping("/debug")
    public String debug() {
        return musicService.getRawMelonChartJson();
    }
}