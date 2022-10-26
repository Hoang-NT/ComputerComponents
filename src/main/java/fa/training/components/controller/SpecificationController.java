package fa.training.components.controller;

import fa.training.components.dto.SpecificationDTO;
import fa.training.components.model.ResponseObject;
import fa.training.components.service.SpecificationService;
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
@RequestMapping("/specification")
public class SpecificationController {
    @Autowired
    private SpecificationService specificationService;

    @GetMapping
    public ResponseObject getSpecification(@RequestParam(required = false) String productID,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        // Check if current customer is admin
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<? extends GrantedAuthority> authority = authentication.getAuthorities().stream().findFirst();
        boolean isAdmin = "ROLE_ADMIN".equals(authority.get().getAuthority());

        if (productID != null) {
            return new ResponseObject(specificationService.findByProductID(productID, isAdmin));
        }

        // Create Pageable object with sort
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        return new ResponseObject(specificationService.findAll(isAdmin, pageable));
    }

    @PostMapping
    public ResponseObject createSpecification(@RequestBody @Valid SpecificationDTO specificationDTO, BindingResult result) {
        // Return error if any
        if (result.hasErrors()) {
            return new ResponseObject("406", result.getFieldError().toString());
        }
        return new ResponseObject(specificationService.createSpecification(specificationDTO));
    }

    @PutMapping
    public ResponseObject editSpecification(@RequestBody SpecificationDTO specificationDTO) {
        return new ResponseObject(specificationService.editSpecification(specificationDTO));
    }
}
