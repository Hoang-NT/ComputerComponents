package fa.training.components.repository;

import fa.training.components.entity.OrderDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetail, String> {

    List<OrderDetail> findByOrderId(String orderId, Pageable pageable);

    List<OrderDetail> findByOrderIdAndIsDeleted(String orderId, boolean isDeleted, Pageable pageable);

    Optional<OrderDetail> findByIdAndIsDeleted(String id, boolean isDeleted);

    List<OrderDetail> findByProductId(String productId, Pageable pageable);

    List<OrderDetail> findByAmount(Integer amount, Pageable pageable);

    List<OrderDetail> findByTotal(Integer total, Pageable pageable);
}
