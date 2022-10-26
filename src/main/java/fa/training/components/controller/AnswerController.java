package fa.training.components.controller;

import fa.training.components.dto.AnswerDTO;
import fa.training.components.model.ResponseObject;
import fa.training.components.service.AnswerService;
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
@RequestMapping("/answer")
public class AnswerController {
    @Autowired
    private AnswerService answerService;

    @GetMapping
    public ResponseObject getAnswer(@RequestParam(required = false) String id,
                                    @RequestParam(required = false) String question,
                                    @RequestParam(required = false) String owner,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        // Create Pageable object with sort
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate"));

        // Check if current customer is admin
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<? extends GrantedAuthority> authority = authentication.getAuthorities().stream().findFirst();
        boolean isAdmin = "ROLE_ADMIN".equals(authority.get().getAuthority());

        // Check if any request parameter provided to call correspond service
        if (id != null) {
            return new ResponseObject(answerService.findByID(id, isAdmin));
        } else if (question != null) {
            return new ResponseObject(answerService.findByQuestion(question, isAdmin, pageable));
        } else if (owner != null) {
            return new ResponseObject(answerService.findByOwner(owner, isAdmin, pageable));
        } else {
            return new ResponseObject(answerService.findAll(isAdmin, pageable));
        }
    }

    @PostMapping
    public ResponseObject createAnswer(@RequestBody @Valid AnswerDTO answerDTO, BindingResult result) {
        // Return error if any
        if (result.hasErrors()) {
            return new ResponseObject("406", result.getFieldError().toString());
        }
        return new ResponseObject(answerService.createAnswer(answerDTO));
    }

    @PutMapping
    public ResponseObject editAnswer(@RequestBody @Valid AnswerDTO answerDTO, BindingResult result) {
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

        return new ResponseObject(answerService.editAnswer(answerDTO, username, isAdmin));
    }

    @DeleteMapping
    public ResponseObject deleteAnswer(@RequestParam String id) {
        // Get current logged in customer
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // and check if is admin
        Optional<? extends GrantedAuthority> authority = authentication.getAuthorities().stream().findFirst();
        boolean isAdmin = "ROLE_ADMIN".equals(authority.get().getAuthority());

        return new ResponseObject(answerService.deleteAnswer(id, username, isAdmin));
    }
}
