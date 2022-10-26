package fa.training.components.service;

import fa.training.components.dto.OrderDetailDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderDetailService {

    OrderDetailDTO findOrderDetailsById(String id, boolean isAdmin);

    List<OrderDetailDTO> findOrderDetailsByOrderId(String orderId, boolean isAdmin, Pageable pageable);

    List<OrderDetailDTO> findOrderDetailsByProductId(String productId, boolean isAdmin, Pageable pageable);

    List<OrderDetailDTO> findOrderDetailsByAmount(Integer amount, boolean isAdmin, Pageable pageable);

    List<OrderDetailDTO> findOrderDetailsByTotal(Integer total, boolean isAdmin, Pageable pageable);

    OrderDetailDTO addOrderDetail(OrderDetailDTO orderDetailDTO, boolean isAdmin);

    OrderDetailDTO editOrderDetailById(String id, OrderDetailDTO orderDetailDTO, boolean isAdmin);

    OrderDetailDTO deleteOrderDetailsById(String id, boolean isAdmin);

}
