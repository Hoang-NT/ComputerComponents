package fa.training.components.service.impl;

import fa.training.components.dto.OrderDTO;
import fa.training.components.entity.Order;
import fa.training.components.exception.MyException;
import fa.training.components.repository.OrderRepository;
import fa.training.components.security.AuditorAwareImpl;
import fa.training.components.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuditorAwareImpl auditorProvider;
    @Override
    public OrderDTO findOrderById(String id, boolean isAdminAndStaff) {
        String editor = auditorProvider.getCurrentAuditor().get();
        Optional<Order> order = orderRepository.findById(id);
        if (isAdminAndStaff) {
            return modelMapper.map(order, OrderDTO.class);
        } else if (editor.equals(order.get().getCreatedBy())) {
            order = orderRepository.findByIdAndIsDeleted(id, false);
        } else {
            throw new MyException("400", "Order not found");
        }
        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public List<OrderDTO> findAllOrder(Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.stream().map(order -> modelMapper.map(order, OrderDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> findOrderByCreateDate(String createDate, Pageable pageable) {
        List<Order> orders = orderRepository.findByCreateDate(LocalDateTime.parse(createDate), pageable);
        return orders.stream().map(order -> modelMapper.map(order, OrderDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> findOrderByLastModifyDate(String lastModifyDate, Pageable pageable) {
        List<Order> orders = orderRepository.findByLastModifyDate(LocalDateTime.parse(lastModifyDate), pageable);
        return orders.stream().map(order -> modelMapper.map(order, OrderDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> findOrderByCreateBy(String createBy, boolean isAdmin, Pageable pageable) {
        List<Order> orders = orderRepository.findByCreatedBy(createBy, pageable);
        String owner = auditorProvider.getCurrentAuditor().get();
        if (isAdmin) {
            return orders.stream().map(order -> modelMapper.map(order, OrderDTO.class)).collect(Collectors.toList());
        }
        List<Order> ordersByOwner = new ArrayList<>();
        for(Order order : orders) {
            if (owner.equals(order.getCreatedBy())) {
                ordersByOwner.add(order);
            }
        }
        return ordersByOwner.stream().map(order -> modelMapper.map(order, OrderDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> findOrderByLastModifiedBy(String lastModifiedBy, Pageable pageable) {
        List<Order> orders = orderRepository.findByLastModifiedBy(lastModifiedBy, pageable);
        return orders.stream().map(order -> modelMapper.map(order, OrderDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> findOrderByStatus(String status, boolean isAdmin, Pageable pageable) {
        List<Order> orders = orderRepository.findByStatus(status, pageable);
        String owner = auditorProvider.getCurrentAuditor().get();
        if (isAdmin) {
            return orders.stream().map(order -> modelMapper.map(order, OrderDTO.class)).collect(Collectors.toList());
        }
        List<Order> ordersByOwner = new ArrayList<>();
        for(Order order : orders) {
            if (owner.equals(order.getCreatedBy())) {
                ordersByOwner.add(order);
            }
        }
        return ordersByOwner.stream().map(order -> modelMapper.map(order, OrderDTO.class)).collect(Collectors.toList());
    }

    @Override
    public OrderDTO addOrder(OrderDTO orderDTO) {
        UUID id = UUID.randomUUID();
        orderDTO.setId(id.toString());
        orderDTO.setDeleted(false);
        Order savedOrder = orderRepository.save(modelMapper.map(orderDTO, Order.class));
        return modelMapper.map(savedOrder, OrderDTO.class);
    }

    @Override
    public OrderDTO editOrderById(String id, OrderDTO orderDTO, boolean isAdmin) {
        OrderDTO orderToEdit = findOrderById(id, isAdmin);
        String owner = auditorProvider.getCurrentAuditor().get();
        if (owner.equals(orderToEdit.getCreatedBy()) && !orderToEdit.isDeleted()) {
            if ("Delivering".equals(orderToEdit.getStatus()) || "Completed".equals(orderToEdit.getStatus())) {
                throw new MyException("408","Order is being delivered!");
            }
        } else if (owner.equals(orderToEdit.getCreatedBy()) && orderToEdit.isDeleted()) {
                throw new MyException("400", "Order not found");
        } else if (isAdmin) {
            orderToEdit.setDeleted(false);
        } else if (!isAdmin && !owner.equals(orderToEdit.getCreatedBy())) {
            throw new MyException("400", "Order not found");
        }
        orderToEdit.setStatus(Objects.isNull(orderDTO.getStatus()) ? orderToEdit.getStatus() : orderDTO.getStatus());
        Order editedOrder = orderRepository.saveAndFlush(modelMapper.map(orderToEdit, Order.class));
        return modelMapper.map(editedOrder, OrderDTO.class);
    }

    @Override
    public OrderDTO deleteOrderById(String id, boolean isAdmin) {
        OrderDTO orderToDelete = findOrderById(id, isAdmin);
        String status;
        Order deletedOrder;
        String owner = auditorProvider.getCurrentAuditor().get();
        if (orderToDelete == null) {
            throw new MyException("400", "Order not found");
        } else {
            status = orderToDelete.getStatus();
        }
        if (isAdmin || (owner.equals(orderToDelete.getCreatedBy()) && status.equals("Awaiting confirm"))) {
            orderToDelete.setDeleted(true);
            deletedOrder = orderRepository.saveAndFlush(modelMapper.map(orderToDelete, Order.class));
        } else {
            throw new MyException("400", "Order not found");
        }
        return modelMapper.map(deletedOrder, OrderDTO.class);
    }
}