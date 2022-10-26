package fa.training.components.service;

import fa.training.components.dto.OrderDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    public OrderDTO findOrderById(String id, boolean isAdminAndStaff);

    public List<OrderDTO> findAllOrder(Pageable pageable);

    public List<OrderDTO> findOrderByCreateDate(String createDate, Pageable pageable);

    public List<OrderDTO> findOrderByLastModifyDate(String lastModifyDate, Pageable pageable);

    public List<OrderDTO> findOrderByCreateBy(String createBy, boolean isAdmin, Pageable pageable);

    public List<OrderDTO> findOrderByLastModifiedBy(String createBy, Pageable pageable);

    public List<OrderDTO> findOrderByStatus(String status, boolean isAdmin, Pageable pageable);

    public OrderDTO addOrder(OrderDTO orderDTO);

    public OrderDTO editOrderById(String id, OrderDTO orderDTO, boolean isAdmin);

    public OrderDTO deleteOrderById(String id, boolean isAdmin);

}