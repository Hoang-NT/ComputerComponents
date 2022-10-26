package fa.training.components.service;

import fa.training.components.dto.ReviewDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    ReviewDTO findReviewById(String id, boolean isAdminAndStaff);
    List<ReviewDTO> findReviewByRating(Integer rating, boolean isAdminAndStaff, Pageable pageable);
    List<ReviewDTO> findReviewByProductName(String productName, boolean isAdminAndStaff, Pageable pageable);
    List<ReviewDTO> findReviewByCreatedBy(String createdBy, boolean isAdminAndStaff, Pageable pageable);
    ReviewDTO deleteReviewById(String id, boolean isAdmin);
    ReviewDTO editReview(String id, ReviewDTO reviewDTO, boolean isAdmin);
    ReviewDTO addReview(ReviewDTO reviewDTO);
}
