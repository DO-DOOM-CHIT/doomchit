package com.mysite.doomchit.reviews;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mysite.doomchit.musics.Music;
import com.mysite.doomchit.users.Users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class ReviewService {

	private final ReviewRepository reviewRepository;

	// 리뷰 생성 =============================================
	public Review create(Music music, Users user, String content) {
		// 리뷰 중복 체크
		if (reviewRepository.existsByMusicAndUser(music, user)) {
			throw new IllegalStateException("이미 이 음악에 대한 리뷰를 작성했습니다.");
	    }
      
		Review review = new Review();
		review.setMusic(music);
		review.setUser(user);
		review.setContent(content);
		reviewRepository.save(review);
		
		return review;
		
	}
	
	// 리뷰 조회 =============================================
	public List<Review> getReviewList(Music music) {
		return reviewRepository.findByMusic(music);
	}


	// 리뷰 수정 =============================================
	public Review modify(Users user, Review review, String content) {
		
		// 작성자 체크
        if (!review.getUser().getUno().equals(user.getUno())) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }
        
		review.setContent(content);
		reviewRepository.save(review);
		
		return review;
		
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
	
}
