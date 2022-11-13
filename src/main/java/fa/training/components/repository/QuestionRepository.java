package fa.training.components.repository;

import fa.training.components.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {
    List<Question> findByProductID(String productID, Pageable pageable);
    List<Question> findByCreatedBy(String createdBy, Pageable pageable);
    List<Question> findByProductIDAndCreatedBy(String productID, String createdBy, Pageable pageable);
    Optional<Question> findByIdAndIsDeleted(String id, boolean isDeleted);
    List<Question> findByProductIDAndIsDeleted(String productID, boolean isDeleted, Pageable pageable);
    List<Question> findByCreatedByAndIsDeleted(String createdBy, boolean isDeleted, Pageable pageable);
    List<Question> findByProductIDAndCreatedByAndIsDeleted(String productID, String createdBy, boolean isDeleted, Pageable pageable);
    Page<Question> findByIsDeleted(boolean isDeleted, Pageable pageable);
}
