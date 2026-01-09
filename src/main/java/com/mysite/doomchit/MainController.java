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

    @GetMapping("/")
    public String root() {
        return "redirect:/doomchit/main";
    }

    @GetMapping("/doomchit/main")
    public String main(Model model) {
        List<Music> chart = musicService.getMelonChart();
        model.addAttribute("chart", chart);
        return "chart";
    }

    // JSON 데이터 확인용 임시 페이지
    @org.springframework.web.bind.annotation.ResponseBody
    @GetMapping("/debug")
    public String debug() {
        return musicService.getRawMelonChartJson();
    }
}