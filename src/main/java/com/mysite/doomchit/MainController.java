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
    private final com.mysite.doomchit.reviews.ReviewRepository reviewRepository;

    @GetMapping("/")
    public String root() {
        return "redirect:/doomchit/main";
    }

    @GetMapping("/doomchit/main")
    public String main(Model model) {
        List<Music> chart = musicService.getMelonChartBasic();
        populateMusicInfo(chart);

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
        }

        populateMusicInfo(chart);

        model.addAttribute("pageName", "likes");
        model.addAttribute("chart", chart);
        return "likes";
    }

    private void populateMusicInfo(List<Music> chart) {
        for (Music m : chart) {
            try {
                Music dbMusic = m.getMno() != null ? m : musicService.findMusicByMusicId(m.getMusicId());

                if (dbMusic != null) {
                    m.setLikeCount(likesRepository.countByMusic(dbMusic));
                    m.setCommentCount(reviewRepository.countByMusic(dbMusic));
                    java.math.BigDecimal avg = reviewRepository.getAverageRatingByMusic(dbMusic);
                    m.setAverageRating(avg != null ? avg.doubleValue() : 0.0);
                } else {
                    m.setLikeCount(0L);
                    m.setCommentCount(0L);
                    m.setAverageRating(0.0);
                }
            } catch (Exception e) {
                m.setLikeCount(0L);
                m.setCommentCount(0L);
                m.setAverageRating(0.0);
            }
        }
    }

    // JSON 데이터 확인용 임시 페이지
    @org.springframework.web.bind.annotation.ResponseBody
    @GetMapping("/doomchit/search")
    public java.util.List<java.util.Map<String, Object>> search(
            @org.springframework.web.bind.annotation.RequestParam("keyword") String keyword) {
        return musicService.searchMelon(keyword);
    }
}