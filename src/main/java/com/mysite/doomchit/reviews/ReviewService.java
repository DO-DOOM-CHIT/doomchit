package com.mysite.doomchit.reviews;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.doomchit.musics.Music;
import com.mysite.doomchit.users.Users;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class ReviewService {

	private final ReviewRepository reviewRepository;

	// 리뷰 생성 =============================================
	public Review create(Music music, Users user, String content, BigDecimal rating) {
		// 리뷰 중복 체크
		if (reviewRepository.findByMusicAndUser(music, user).isPresent()) {
            throw new IllegalStateException("이미 이 음악에 대한 리뷰를 작성했습니다.");
        }
      
		Review review = new Review();
		review.setMusic(music);
		review.setUser(user);
		review.setContent(content);
		review.setRating(rating);
		reviewRepository.save(review);
		
		return review;
		
	}
	
	// 리뷰 조회 (최신순) =============================================
	public List<Review> getReviewList(Music music) {
        return reviewRepository.findByMusicOrderByCreDateDesc(music);
    }


	// 리뷰 수정 =============================================
	public Review modify(Users user, Review review, String content, BigDecimal rating) {

        // 작성자 체크
        if (!review.getUser().getUno().equals(user.getUno())) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        review.setContent(content);
        review.setRating(rating);

        return reviewRepository.save(review);
    }
	
	// 리뷰 삭제 =============================================
	public void delete(Users user, Review review) {

        // 작성자 체크
        if (!review.getUser().getUno().equals(user.getUno())) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        reviewRepository.delete(review);
    }
	
	public Review getReview(Integer rno) {
	    return reviewRepository.findById(rno)
	            .orElseThrow(() -> 
	                new IllegalArgumentException("리뷰를 찾을 수 없습니다.")
	            );
	}
	
	// 평균 평점 (없으면 0.0)
    public BigDecimal getAverageRatingOrZero(Music music) {
        BigDecimal avg = reviewRepository.getAverageRatingByMusic(music);
        if (avg == null) {
            return BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP);
        }
        return avg.setScale(1, RoundingMode.HALF_UP);
    }

    // 리뷰 개수
    public long getReviewCount(Music music) {
        return reviewRepository.countByMusic(music);
    }
    
    // 로그인 유저의 해당 음악 리뷰 조회
    public Optional<Review> getMyReview(Music music, Users user) {
        return reviewRepository.findByMusicAndUser(music, user);
    }

	
}
