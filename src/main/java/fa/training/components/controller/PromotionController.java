package fa.training.components.controller;

import fa.training.components.dto.PromotionDTO;
import fa.training.components.model.ResponseObject;
import fa.training.components.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/promotion")
public class PromotionController {
    @Autowired
    private PromotionService promotionService;

    @GetMapping
    public ResponseObject getPromotion(@RequestParam(required = false) String id,
                                       @RequestParam(required = false) String product,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        // Create Pageable object with sort
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lastModifiedDate"));

        // Check if current customer is admin
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<? extends GrantedAuthority> authority = authentication.getAuthorities().stream().findFirst();
        boolean isAdmin = "ROLE_ADMIN".equals(authority.get().getAuthority());

        // Check if any request param provided to call correspond service
        if (id != null) {
            return new ResponseObject(promotionService.findByID(id, isAdmin));
        } else if (product != null) {
            return new ResponseObject(promotionService.findByProduct(product, isAdmin, pageable));
        } else {
            return new ResponseObject(promotionService.findAll(isAdmin, pageable));
        }
    }

    @PostMapping
    public ResponseObject createPromotion(@RequestBody @Valid PromotionDTO promotionDTO, BindingResult result) {
        // Return error if any
        if (result.hasErrors()) {
            return new ResponseObject("406", result.getFieldError().toString());
        }
        return new ResponseObject(promotionService.createPromotion(promotionDTO));
    }

    @PutMapping
    public ResponseObject editPromotion(@RequestBody PromotionDTO promotionDTO) {
        return new ResponseObject(promotionService.editPromotion(promotionDTO));
    }
}
