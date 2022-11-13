package fa.training.components.repository;

import fa.training.components.entity.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpecificationRepository extends JpaRepository<Specification, String> {
    Optional<Specification> findByProductIDAndIsDeleted(String productID, boolean isDeleted);
    Page<Specification> findByIsDeleted(boolean isDeleted, Pageable pageable);
}
