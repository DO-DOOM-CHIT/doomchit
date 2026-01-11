package com.mysite.doomchit.reviews;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysite.doomchit.musics.Music;
import com.mysite.doomchit.musics.MusicService;
import com.mysite.doomchit.users.UserService;
import com.mysite.doomchit.users.Users;

import lombok.RequiredArgsConstructor;

@RequestMapping("/doomchit")
@RequiredArgsConstructor
@Controller
public class ReviewController {

	private final ReviewService reviewService;
	private final MusicService musicService;
	private final UserService userService;

	@GetMapping("/reviews/{mno}")
	public String reviewDetail(@PathVariable("mno") Long mno, Model model) {

		Music music = musicService.getMusic(mno);
		List<Review> reviewList = reviewService.getReviewList(music);

		// 앨범 수록곡 가져오기
		if (music.getAlbumId() != null) {
			List<Music> tracks = musicService.getAlbumTracklist(music.getAlbumId());
			model.addAttribute("tracks", tracks);
		}

		model.addAttribute("music", music);
		model.addAttribute("reviewList", reviewList);

		return "reviews";
	}

	@GetMapping("/music/detail/{musicId}")
	public String musicDetailBridge(@PathVariable("musicId") Long musicId) {
		try {
			Music music = musicService.getOrCreateMusicByMusicId(musicId);
			return "redirect:/doomchit/reviews/" + music.getMno();
		} catch (Exception e) {
			e.printStackTrace(); // 콘솔에 에러 출력
			return "redirect:/doomchit/main"; // 에러 발생 시 메인으로 복귀
		}
	}

	// 리뷰 작성 ===============================================
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{mno}")
	public String createReview(@PathVariable("mno") Long mno, @RequestParam String content) {

		Music music = musicService.getMusic(mno);
		Users user = userService.getCurrentUser();

		reviewService.create(music, user, content);

		return "redirect:/doomchit/reviews/" + mno;

	}

	// 리뷰 수정 ===============================================
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{rno}")
	public String modifyReview(@PathVariable("rno") Integer rno, @RequestParam String content) {

		Review review = reviewService.getReview(rno);
		Users user = userService.getCurrentUser();

		reviewService.modify(user, review, content);

		return "redirect:/doomchit/reviews/" + review.getMusic().getMno();

	}

	// 리뷰 삭제 ===============================================
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/delete/{rno}")
	public String deleteReview(
			@PathVariable("rno") Integer rno) {
		Review review = reviewService.getReview(rno);
		Users user = userService.getCurrentUser();

		reviewService.delete(user, review);

		return "redirect:/doomchit/reviews/" + review.getMusic().getMno();

	}

}
