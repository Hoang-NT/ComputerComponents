package fa.training.components.controller;

import fa.training.components.dto.ReviewDTO;
import fa.training.components.service.ReviewService;
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
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

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
    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ReviewDTO addReview(@RequestBody @Valid ReviewDTO reviewDTO) {
        return reviewService.addReview(reviewDTO);
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF') or hasRole('ROLE_USER')")
    public ReviewDTO findReviewById(@PathVariable("id") String id) {
        boolean isAdminOrStaff = checkRoleAdminAndStaff();
        return reviewService.findReviewById(id, isAdminOrStaff);
    }

    @GetMapping(value = "rating/{rating}/{pageNo}/{pageSize}")
    public List<ReviewDTO> findReviewByRating(@PathVariable("rating") int rating,  @PathVariable("pageNo") int pageNo
            , @PathVariable("pageSize") int pageSize) {
        boolean isAdminOrStaff = checkRoleAdminAndStaff();
        return reviewService.findReviewByRating(rating, isAdminOrStaff, PageRequest.of(pageNo, pageSize));
    }

    @GetMapping(value = "createdBy/{createdBy}/{pageNo}/{pageSize}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    public List<ReviewDTO> findReviewByCreatedBy(@PathVariable("createdBy") String createdBy,  @PathVariable("pageNo") int pageNo
            , @PathVariable("pageSize") int pageSize) {
        boolean isAdminOrStaff = checkRoleAdminAndStaff();
        return reviewService.findReviewByCreatedBy(createdBy, isAdminOrStaff, PageRequest.of(pageNo, pageSize));
    }

    @GetMapping(value = "product/{productName}/{pageNo}/{pageSize}")
    public List<ReviewDTO> findReviewByProductName(@PathVariable("productName") String productName, @PathVariable("pageNo") int pageNo
            , @PathVariable("pageSize") int pageSize) {
        boolean isAdminOrStaff = checkRoleAdminAndStaff();
        return reviewService.findReviewByProductName(productName, isAdminOrStaff, PageRequest.of(pageNo, pageSize));
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ReviewDTO deleteReviewById(@PathVariable("id") String id) {
        boolean isAdmin = checkRoleAdmin();
        return reviewService.deleteReviewById(id, isAdmin);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ReviewDTO editReviewById(@PathVariable("id") String id, @RequestBody @Valid ReviewDTO reviewDTO) {
        boolean isAdmin = checkRoleAdmin();
        return reviewService.editReview(id, reviewDTO, isAdmin);
    }
}