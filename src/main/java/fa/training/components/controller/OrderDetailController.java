package fa.training.components.controller;

import fa.training.components.dto.OrderDetailDTO;
import fa.training.components.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order-detail")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    private boolean checkRoleAdminAndStaff() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<? extends GrantedAuthority> authority = authentication.getAuthorities().stream().findFirst();
        if ("ROLE_ADMIN".equals(authority.get().getAuthority()) || "ROLE_STAFF".equals(authority.get().getAuthority())) {
            return true;
        }
        return false;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    public OrderDetailDTO addOrder(@RequestBody @Valid OrderDetailDTO orderDetailDTO){
        boolean isAdminAndStaff = checkRoleAdminAndStaff();
        return orderDetailService.addOrderDetail(orderDetailDTO, isAdminAndStaff);
    }
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF') or hasRole('ROLE_USER')")
    public OrderDetailDTO findOrderDetailById(@PathVariable("id") String id){
        boolean isAdminAndStaff = checkRoleAdminAndStaff();
        return orderDetailService.findOrderDetailsById(id, isAdminAndStaff);
    }

    @GetMapping(value = "orderId/{orderId}/{pageNo}/{pageSize}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF') or hasRole('ROLE_USER')")
    public List<OrderDetailDTO> findOrderDetailByOrderId(@PathVariable("orderId") String orderId, @PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize) {
        boolean isAdminAndStaff = checkRoleAdminAndStaff();
        return orderDetailService.findOrderDetailsByOrderId(orderId, isAdminAndStaff, PageRequest.of(pageNo, pageSize));
    }

    @GetMapping(value = "productId/{productId}/{pageNo}/{pageSize}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    public List<OrderDetailDTO> findOrderDetailByProductId(@PathVariable("productId") String productId, @PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize) {
        boolean isAdminAndStaff = checkRoleAdminAndStaff();
        return orderDetailService.findOrderDetailsByProductId(productId, isAdminAndStaff, PageRequest.of(pageNo, pageSize));
    }

    @GetMapping(value = "amount/{amount}/{pageNo}/{pageSize}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    public List<OrderDetailDTO> findOrderDetailByAmount(@PathVariable("amount") Integer amount, @PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize) {
        boolean isAdminAndStaff = checkRoleAdminAndStaff();
        return orderDetailService.findOrderDetailsByAmount(amount, isAdminAndStaff, PageRequest.of(pageNo, pageSize));
    }

    @GetMapping(value = "total/{total}/{pageNo}/{pageSize}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    public List<OrderDetailDTO> findOrderDetailByTotal(@PathVariable("total") Integer total, @PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize) {
        boolean isAdminAndStaff = checkRoleAdminAndStaff();
        return orderDetailService.findOrderDetailsByTotal(total, isAdminAndStaff, PageRequest.of(pageNo, pageSize));
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    public OrderDetailDTO editOrderDetailById(@PathVariable("id") String id, @RequestBody @Valid OrderDetailDTO orderDetailDTO){
        boolean isAdminAndStaff = checkRoleAdminAndStaff();
        return orderDetailService.editOrderDetailById(id, orderDetailDTO, isAdminAndStaff);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    public OrderDetailDTO deleteOrderDetailById(@PathVariable("id") String id){
        boolean isAdminAndStaff = checkRoleAdminAndStaff();
        return orderDetailService.deleteOrderDetailsById(id, isAdminAndStaff);
    }
}
