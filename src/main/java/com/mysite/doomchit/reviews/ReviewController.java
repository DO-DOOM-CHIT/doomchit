package com.mysite.doomchit.reviews;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mysite.doomchit.musics.Music;
import com.mysite.doomchit.musics.MusicService;
import com.mysite.doomchit.users.UserService;
import com.mysite.doomchit.users.Users;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/doomchit")
@RequiredArgsConstructor
@Controller
public class ReviewController {

	private final ReviewService reviewService;
	private final MusicService musicService;
	private final UserService userService;
	private final com.mysite.doomchit.likes.LikesService likesService;

	// 리뷰 페이지 (음악 상세 + 리뷰 목록)
	@GetMapping("/reviews/{mno}")
	public String reviewDetail(@PathVariable("mno") Long mno, Model model) {

		Music music = musicService.getMusic(mno);

		// 전체 리뷰 목록 (최신순)
		List<Review> reviewList = reviewService.getReviewList(music);

		// 평균 평점 / 리뷰 개수
		model.addAttribute("avgRating", reviewService.getAverageRatingOrZero(music));
		model.addAttribute("reviewCount", reviewService.getReviewCount(music));

		// 좋아요 정보
		model.addAttribute("likeCount", likesService.getLikeCount(music));
		Users currentUser = null;
		try {
			currentUser = userService.getCurrentUser();
		} catch (Exception e) {
		}
		model.addAttribute("isLiked", likesService.isLiked(currentUser, music));

		// 앨범 수록곡
		if (music.getAlbumId() != null) {
			model.addAttribute("tracks",
					musicService.getAlbumTracklist(music.getAlbumId()));
		}

		// 로그인 유저의 내 리뷰 처리
		try {
			Users user = currentUser; // 재사용

			reviewService.getMyReview(music, user)
					.ifPresent(myReview -> {
						model.addAttribute("myReview", myReview);

						// 전체 리뷰 목록에서 내 리뷰 제거 (중복 방지)
						reviewList.removeIf(r -> r.getRno().equals(myReview.getRno()));
					});

		} catch (Exception e) {
			// 비로그인 상태면 그냥 넘어감
		}

		model.addAttribute("music", music);
		model.addAttribute("reviewList", reviewList);
		model.addAttribute("reviewForm", new ReviewForm());

		return "reviews";
	}

	// 좋아요 토글
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/like/toggle/{mno}")
	public String toggleLike(@PathVariable("mno") Long mno) {
		Music music = musicService.getMusic(mno);
		Users user = userService.getCurrentUser();
		likesService.toggleLike(user, music);
		return "redirect:/doomchit/reviews/" + mno;
	}

	// 외부 musicId → 내부 mno 브릿지
	@GetMapping("/music/detail/{musicId}")
	public String musicDetailBridge(@PathVariable("musicId") Long musicId) {
		try {
			Music music = musicService.getOrCreateMusicByMusicId(musicId);
			return "redirect:/doomchit/reviews/" + music.getMno();
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/doomchit/main";
		}
	}

	// 리뷰 작성 =================================================
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{mno}")
	public String createReview(@PathVariable("mno") Long mno, @Valid ReviewForm reviewForm,
			BindingResult bindingResult, Model model) {
		Music music = musicService.getMusic(mno);

		if (bindingResult.hasErrors()) {
			// 기존 상세페이지 데이터 다시 세팅
			List<Review> reviewList = reviewService.getReviewList(music);

			model.addAttribute("music", music);
			model.addAttribute("reviewList", reviewList);
			model.addAttribute("avgRating", reviewService.getAverageRatingOrZero(music));
			model.addAttribute("reviewCount", reviewService.getReviewCount(music));

			return "reviews"; // 다시 리뷰 페이지
		}

		Users user = userService.getCurrentUser();
		reviewService.create(music, user, reviewForm.getContent(), reviewForm.getRating());

		return "redirect:/doomchit/reviews/" + mno;
	}

	// 리뷰 수정 =================================================
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{rno}")
	public String modifyReview(@PathVariable("rno") Integer rno, @Valid ReviewForm reviewForm,
			BindingResult bindingResult) {
		Review review = reviewService.getReview(rno);
		Users user = userService.getCurrentUser();

		if (bindingResult.hasErrors()) {
			return "redirect:/doomchit/reviews/" + review.getMusic().getMno();
		}

		reviewService.modify(user, review, reviewForm.getContent(), reviewForm.getRating());

		return "redirect:/doomchit/reviews/" + review.getMusic().getMno();
	}

	// 리뷰 삭제 =================================================
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/delete/{rno}")
	public String deleteReview(@PathVariable("rno") Integer rno) {

		Review review = reviewService.getReview(rno);
		Users user = userService.getCurrentUser();

		reviewService.delete(user, review);

		return "redirect:/doomchit/reviews/" + review.getMusic().getMno();
	}

}
