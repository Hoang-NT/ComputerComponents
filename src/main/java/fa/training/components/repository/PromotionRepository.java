package fa.training.components.repository;

import fa.training.components.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, String> {
    List<Promotion> findByProductID(String productID, Pageable pageable);
    Optional<Promotion> findByIdAndActive(String id, boolean active);
    List<Promotion> findByProductIDAndActive(String productID, boolean active, Pageable pageable);
    Promotion findByProductIDAndContent(String productID, String content);
    Page<Promotion> findByActive(boolean active, Pageable pageable);
}
