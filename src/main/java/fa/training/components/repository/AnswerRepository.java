package fa.training.components.repository;

import fa.training.components.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, String> {
    List<Answer> findByQuestionID(String questionID, Pageable pageable);
    List<Answer> findByCreatedBy(String createdBy, Pageable pageable);
    Optional<Answer> findByIdAndIsDeleted(String id, boolean isDeleted);
    List<Answer> findByQuestionIDAndIsDeleted(String questionID, boolean isDeleted, Pageable pageable);
    List<Answer> findByCreatedByAndIsDeleted(String createdBy, boolean isDeleted, Pageable pageable);
    Page<Answer> findByIsDeleted(boolean isDeleted, Pageable pageable);
}
