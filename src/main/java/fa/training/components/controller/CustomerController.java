package fa.training.components.controller;

import fa.training.components.dto.CustomerDTO;
import fa.training.components.model.ResponseObject;
import fa.training.components.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseObject getCustomer(@RequestParam(required = false) String username,
                                      @RequestParam(required = false) String email,
                                      @RequestParam(required = false) String phone,
                                      @RequestParam(required = false) String fullName,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        // Create Pageable object with sort
        Pageable pageable = PageRequest.of(page, size, Sort.by("role").and(Sort.by("username")));

        // Check if current customer is admin
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<? extends GrantedAuthority> authority = authentication.getAuthorities().stream().findFirst();
        boolean isAdmin = "ROLE_ADMIN".equals(authority.get().getAuthority());

        // If not an admin, just can get information of himself
        if (!isAdmin) {
            return new ResponseObject(customerService.findByUsernameAndEnable(authentication.getName()));
        }

        // Check if any request param provided to call correspond service
        if (username != null) {
            return new ResponseObject(customerService.findByUsername(username));
        } else if (phone != null) {
            return new ResponseObject(customerService.findByPhone(phone));
        } else if (email != null) {
            return new ResponseObject(customerService.findByEmail(email));
        } else if (fullName != null) {
            return new ResponseObject(customerService.findByFullName(fullName, pageable));
        } else {
            return new ResponseObject(customerService.findAll(pageable));
        }
    }

    @DeleteMapping
    public ResponseObject deleteCustomer(@RequestParam String username) {
        // Get current logged in customer
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();

        // and check if is admin
        Optional<? extends GrantedAuthority> authority = authentication.getAuthorities().stream().findFirst();
        boolean isAdmin = "ROLE_ADMIN".equals(authority.get().getAuthority());

        return new ResponseObject(customerService.deleteCustomer(username, currentUser, isAdmin));
    }

    @PutMapping
    public ResponseObject editCustomer(@RequestBody CustomerDTO customerDTO) {
        // Get current logged in customer
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // and check if is admin
        Optional<? extends GrantedAuthority> authority = authentication.getAuthorities().stream().findFirst();
        boolean isAdmin = "ROLE_ADMIN".equals(authority.get().getAuthority());

        return new ResponseObject(customerService.editCustomer(customerDTO, username, isAdmin));
    }

}
