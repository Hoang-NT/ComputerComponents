package fa.training.components.controller;

import fa.training.components.dto.ProductDTO;
import fa.training.components.service.impl.ProductServiceImpl;
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
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductServiceImpl productService;

    private boolean checkRoleAdmin() {
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
    @GetMapping(value = "/{pageNo}/{pageSize}")
    public List<ProductDTO> findAllProduct(@PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize) {
        boolean isAdminAndStaff = checkRoleAdminAndStaff();
        return productService.findAllProduct(PageRequest.of(pageNo, pageSize), isAdminAndStaff);
    }

    @GetMapping(value = "price/{price}/{pageNo}/{pageSize}")
    public List<ProductDTO> findProductByPrice(@PathVariable("price") int price, @PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize) {
        boolean isAdminAndStaff = checkRoleAdminAndStaff();
        return productService.findProductByPrice(price, PageRequest.of(pageNo, pageSize), isAdminAndStaff);
    }

    @GetMapping(value = "name/{name}")
    public ProductDTO findProductByName(@PathVariable("name") String name) {
        boolean isAdminAndStaff = checkRoleAdminAndStaff();
        return productService.findProductByName(name, isAdminAndStaff);
    }

    @GetMapping(value = "category/{category}/{pageNo}/{pageSize}")
    public List<ProductDTO> findProductByCategory(@PathVariable("category") String category, @PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize) {
        boolean isAdminAndStaff = checkRoleAdminAndStaff();
        return productService.findProductByCategory(category, PageRequest.of(pageNo, pageSize), isAdminAndStaff);
    }

    @DeleteMapping(value = "/{name}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ProductDTO deleteProductByName(@PathVariable("name") String name) {
        boolean isAdmin = checkRoleAdmin();
        return productService.deleteProductByName(name, isAdmin);
    }

    @PutMapping(value = "/{name}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ProductDTO editProduct(@RequestBody @Valid ProductDTO productDTO, @PathVariable("name") String name) {
        boolean isAdmin = checkRoleAdmin();
        return productService.editProduct(name, productDTO, isAdmin);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDTO addProduct(@RequestBody @Valid ProductDTO productDTO) {
        boolean isAdmin = checkRoleAdmin();
        return productService.addProduct(productDTO, isAdmin);
    }
}