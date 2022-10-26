package fa.training.components.controller;

import fa.training.components.dto.OrderDTO;
import fa.training.components.service.OrderService;
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
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    private boolean checkRoleAmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<? extends GrantedAuthority> authority = authentication.getAuthorities().stream().findFirst();
        return "ROLE_ADMIN".equals(authority.get().getAuthority());

    }
    private boolean checkRoleAdminAndStaff() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<? extends GrantedAuthority> authority = authentication.getAuthorities().stream().findFirst();
        if ("ROLE_ADMIN".equals(authority.get().getAuthority()) || "ROLE_STAFF".equals(authority.get().getAuthority())) {
            return true;
        }
        return false;

    }
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF') or hasRole('ROLE_USER')")
    public OrderDTO findOrderById(@PathVariable("id") String id){
        boolean isAminAndStaff = checkRoleAdminAndStaff();
        return orderService.findOrderById(id, isAminAndStaff);
    }

    @GetMapping(value = "/{pageNo}/{pageSize}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    public List<OrderDTO> findAllOrder(@PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize){
        return orderService.findAllOrder(PageRequest.of(pageNo, pageSize));
    }

    @GetMapping(value = "/createDate/{createDate}/{pageNo}/{pageSize}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    List<OrderDTO> findOrderByCreateDate(@PathVariable("createDate") String createDate
            , @PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize){
        return orderService.findOrderByCreateDate(createDate, PageRequest.of(pageNo, pageSize));
    }

    @GetMapping(value = "/lastModifyDate/{lastModifyDate}/{pageNo}/{pageSize}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    List<OrderDTO> findOrderByLastModifyDate(@PathVariable("lastModifyDate") String lastModifyDate
            , @PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize){
        return orderService.findOrderByLastModifyDate(lastModifyDate, PageRequest.of(pageNo, pageSize));
    }

    @GetMapping(value = "/createdBy/{createdBy}/{pageNo}/{pageSize}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    List<OrderDTO> findOrderByCreatedBy(@PathVariable("createdBy") String createdBy
            , @PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize){
        boolean isAminAndStaff = checkRoleAdminAndStaff();
        return orderService.findOrderByCreateBy(createdBy, isAminAndStaff, PageRequest.of(pageNo, pageSize));
    }

    @GetMapping(value = "/lastModifiedBy/{lastModifiedBy}/{pageNo}/{pageSize}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    List<OrderDTO> findOrderByLastModifiedBy(@PathVariable("lastModifiedBy") String lastModifiedBy
            , @PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize){
        return orderService.findOrderByLastModifiedBy(lastModifiedBy, PageRequest.of(pageNo, pageSize));
    }

    @GetMapping(value = "/status/{status}/{pageNo}/{pageSize}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_STAFF')")
    List<OrderDTO> findOrderByStatus(@PathVariable("status") String status
            , @PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize){
        boolean isAminAndStaff = checkRoleAdminAndStaff();
        return orderService.findOrderByStatus(status, isAminAndStaff, PageRequest.of(pageNo, pageSize));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    public OrderDTO addOrder(@RequestBody @Valid OrderDTO orderDTO){
        return orderService.addOrder(orderDTO);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    public OrderDTO editOrderById(@PathVariable("id") String id, @RequestBody @Valid OrderDTO orderDTO){
        boolean isAdminAndStaff = checkRoleAdminAndStaff();
        return orderService.editOrderById(id, orderDTO, isAdminAndStaff);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    public OrderDTO deleteOrderById(@PathVariable("id") String id){
        boolean isAdminAndStaff = checkRoleAdminAndStaff();
        return orderService.deleteOrderById(id, isAdminAndStaff);
    }

}