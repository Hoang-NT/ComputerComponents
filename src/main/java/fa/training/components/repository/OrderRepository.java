package fa.training.components.repository;

import fa.training.components.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    Optional<Order> findByIdAndIsDeleted(String id, boolean isDeleted);
    List<Order> findByCreateDate(LocalDateTime createDate, Pageable pageable);

    List<Order> findByLastModifyDate(LocalDateTime lastModifyDate, Pageable pageable);

    List<Order> findByCreatedBy(String createdBy, Pageable pageable);

    List<Order> findByLastModifiedBy(String lastModifiedBy, Pageable pageable);

    List<Order> findByStatus(String status, Pageable pageable);
}