package fa.training.components.controller;

import fa.training.components.dto.QuestionDTO;
import fa.training.components.model.ResponseObject;
import fa.training.components.service.QuestionService;
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
@RequestMapping("/question")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @GetMapping
    public ResponseObject getQuestion(@RequestParam(required = false) String id,
                                      @RequestParam(required = false) String product,
                                      @RequestParam(required = false) String owner,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        // Create Pageable object with sort
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate"));

        // Check if current customer is admin
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<? extends GrantedAuthority> authority = authentication.getAuthorities().stream().findFirst();
        boolean isAdmin = "ROLE_ADMIN".equals(authority.get().getAuthority());

        // Check if any request param provided to call correspond service
        if (id != null) {
            return new ResponseObject(questionService.findByID(id, isAdmin));
        } else if (product != null && owner != null) {
            return new ResponseObject(questionService.findByProductAndOwner(product, owner, isAdmin, pageable));
        } else if (product != null) {
            return new ResponseObject(questionService.findByProduct(product, isAdmin, pageable));
        } else if (owner != null) {
            return new ResponseObject(questionService.findByOwner(owner, isAdmin, pageable));
        } else {
            return new ResponseObject(questionService.findAll(isAdmin, pageable));
        }
    }

    @PostMapping
    public ResponseObject createQuestion(@RequestBody @Valid QuestionDTO questionDTO, BindingResult result) {
        // Return error if any
        if (result.hasErrors()) {
            return new ResponseObject("406", result.getFieldError().toString());
        }

        return new ResponseObject(questionService.createQuestion(questionDTO));
    }

    @PutMapping
    public ResponseObject editQuestion(@RequestBody @Valid QuestionDTO questionDTO, BindingResult result) {
        // Return error if any
        if (result.hasErrors()) {
            return new ResponseObject("406", result.getFieldError().toString());
        }

        // Get current logged in customer
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // and check if is admin
        Optional<? extends GrantedAuthority> authority = authentication.getAuthorities().stream().findFirst();
        boolean isAdmin = "ROLE_ADMIN".equals(authority.get().getAuthority());

        return new ResponseObject(questionService.editQuestion(questionDTO, username, isAdmin));
    }

    @DeleteMapping
    public ResponseObject deleteQuestion(@RequestParam String id) {
        // Get current logged in customer
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // and check if is admin
        Optional<? extends GrantedAuthority> authority = authentication.getAuthorities().stream().findFirst();
        boolean isAdmin = "ROLE_ADMIN".equals(authority.get().getAuthority());

        return new ResponseObject(questionService.deleteQuestion(id, username, isAdmin));
    }
}
