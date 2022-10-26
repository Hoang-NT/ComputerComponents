package fa.training.components.repository;

import fa.training.components.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findByRating(int rating, Pageable pageable);
    List<Review> findByRatingAndIsDeleted(int rating, boolean isDeleted, Pageable pageable);
    Optional<Review> findByIdAndIsDeleted(String id, boolean isDeleted);
    List<Review> findByProductId(String productId, Pageable pageable);
    List<Review> findByProductIdAndIsDeleted(String productId, boolean isDeleted, Pageable pageable);
    List<Review> findByCreatedByAndIsDeleted(String createdBy, boolean isDeleted, Pageable pageable);
    List<Review> findByCreatedBy(String createdBy, Pageable pageable);

}