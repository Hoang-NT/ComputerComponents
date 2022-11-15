package fa.training.components.service.impl;

import fa.training.components.dto.OrderDTO;
import fa.training.components.dto.OrderDetailDTO;
import fa.training.components.dto.ProductDTO;
import fa.training.components.entity.OrderDetail;
import fa.training.components.exception.MyException;
import fa.training.components.repository.OrderDetailsRepository;
import fa.training.components.security.AuditorAwareImpl;
import fa.training.components.service.OrderDetailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private AuditorAwareImpl auditorProvider;

    @Autowired
    private OrderServiceImpl orderService;

    @Override
    public OrderDetailDTO findOrderDetailsById(String id, boolean isAdminAndStaff) {
        String editor = auditorProvider.getCurrentAuditor().get();
        Optional<OrderDetail> orderDetail = orderDetailsRepository.findById(id);
        if (isAdminAndStaff) {
            return modelMapper.map(orderDetail, OrderDetailDTO.class);
        } else if (editor.equals(orderDetail.get().getLastModifiedBy())) {
            orderDetail = orderDetailsRepository.findByIdAndIsDeleted(id, false);
        } else {
            return null;
        }
        return modelMapper.map(orderDetail, OrderDetailDTO.class);
    }

    @Override
    public List<OrderDetailDTO> findOrderDetailsByOrderId(String orderId, boolean isAdminAndStaff, Pageable pageable) {
        String owner = auditorProvider.getCurrentAuditor().get();
        OrderDTO orderDTO = orderService.findOrderById(orderId, isAdminAndStaff);
        List<OrderDetail> orderDetails;
        if (isAdminAndStaff) {
            orderDetails = orderDetailsRepository.findByOrderId(orderId, pageable);
        } else if (owner.equals(orderDTO.getCreatedBy())) {
            orderDetails = orderDetailsRepository.findByOrderIdAndIsDeleted(orderId, false, pageable);
        } else {
            throw new MyException("400", "OrderDetails not found! ");
        }
        return orderDetails.stream().map(orderDetail -> modelMapper.map(orderDetail, OrderDetailDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<OrderDetailDTO> findOrderDetailsByProductId(String productId, boolean isAdmin, Pageable pageable) {
        if (!isAdmin) {
            throw new MyException("400", "OrderDetails not found! ");
        }
        List<OrderDetail> orderDetails = orderDetailsRepository.findByProductId(productId, pageable);
        return orderDetails.stream().map(orderDetail -> modelMapper.map(orderDetail, OrderDetailDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<OrderDetailDTO> findOrderDetailsByAmount(Integer amount, boolean isAdmin, Pageable pageable) {
        if (!isAdmin) {
            throw new MyException("400", "OrderDetails not found! ");
        }
        List<OrderDetail> orderDetails = orderDetailsRepository.findByAmount(amount, pageable);
        return orderDetails.stream().map(orderDetail -> modelMapper.map(orderDetail, OrderDetailDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<OrderDetailDTO> findOrderDetailsByTotal(Integer total, boolean isAdmin, Pageable pageable) {
        if (!isAdmin) {
            throw new MyException("400", "OrderDetails not found! ");
        }
        List<OrderDetail> orderDetails = orderDetailsRepository.findByTotal(total, pageable);
        return orderDetails.stream().map(orderDetail -> modelMapper.map(orderDetail, OrderDetailDTO.class)).collect(Collectors.toList());
    }

    @Override
    public OrderDetailDTO addOrderDetail(OrderDetailDTO orderDetailDTO, boolean isAdminAndStaff) {
        ProductDTO productDTO = productService.findProductById(orderDetailDTO.getProductId(), true);
        int price = productDTO.getPrice();
        float discount = productDTO.getDiscount();
        float afterDiscount = 1 - discount;
        int amount = orderDetailDTO.getAmount();
        int stockAfterOrder = productDTO.getStock() - amount;
        float total = (float) amount * price * afterDiscount;
        orderDetailDTO.setTotal((int) total);
        UUID id = UUID.randomUUID();
        orderDetailDTO.setId(id.toString());
        orderDetailDTO.setIsDeleted(false);
        if (stockAfterOrder < 0) {
            throw new MyException("408","Mount of product in stock not enough!");
        } else {
            productDTO.setStock(stockAfterOrder);
            productService.editProduct(productDTO.getName(), productDTO, true);
        }
        OrderDetail savedOrderDetail = orderDetailsRepository.save(modelMapper.map(orderDetailDTO, OrderDetail.class));
        return modelMapper.map(savedOrderDetail, OrderDetailDTO.class);
    }

    @Override
    public OrderDetailDTO editOrderDetailById(String id, OrderDetailDTO orderDetailDTO, boolean isAdmin) {
        String editor = auditorProvider.toString();
        OrderDetailDTO orderDetailToEdit = findOrderDetailsById(orderDetailDTO.getId(), isAdmin);
        ProductDTO productDTO = productService.findProductById(orderDetailDTO.getProductId(), true);
        int price = productDTO.getPrice();
        float discount = productDTO.getDiscount();
        float afterDiscount = 1 - discount;
        int amount = orderDetailDTO.getAmount();
        if (Objects.isNull(orderDetailDTO.getAmount())) {
            orderDetailToEdit.setIsDeleted(true);
            orderDetailToEdit.setLastModifyDate(orderDetailDTO.getLastModifyDate());
            orderDetailToEdit.setLastModifiedBy(editor);
        } else {
            float total = (float) amount * price * afterDiscount;
            orderDetailToEdit.setAmount(orderDetailDTO.getAmount());
            orderDetailToEdit.setTotal((int) total);
            orderDetailToEdit.setLastModifyDate(orderDetailDTO.getLastModifyDate());
            orderDetailToEdit.setLastModifiedBy(editor);
        }
        OrderDetail editedOrderDetail = orderDetailsRepository.saveAndFlush(modelMapper.map(orderDetailToEdit, OrderDetail.class));
        return modelMapper.map(editedOrderDetail, OrderDetailDTO.class);
    }

    @Override
    public OrderDetailDTO deleteOrderDetailsById(String id, boolean isAdmin) {
        String owner = auditorProvider.getCurrentAuditor().get();
        OrderDetailDTO orderDetailToDelete = findOrderDetailsById(id, isAdmin);
        if (!isAdmin && !owner.equals(orderDetailToDelete.getLastModifiedBy())) {
            throw new MyException("400", "OrderDetails not found! ");
        }
        orderDetailToDelete.setIsDeleted(true);
        OrderDetail deletedOrderDetail = orderDetailsRepository.saveAndFlush(modelMapper.map(orderDetailToDelete, OrderDetail.class));
        return modelMapper.map(deletedOrderDetail, OrderDetailDTO.class);
    }
}
