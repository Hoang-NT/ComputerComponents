package fa.training.components.controller;

import fa.training.components.dto.CustomerDTO;
import fa.training.components.exception.MyException;
import fa.training.components.model.ResponseObject;
import fa.training.components.security.JwtTokenUtil;
import fa.training.components.security.JwtUserDetailsService;
import fa.training.components.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private CustomerService customerService;

    private void authenticate(String username, String password){
        try {
            // Authenticate sign in username and password with user in database
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new MyException("401", "User disabled");
        } catch (BadCredentialsException e) {
            throw new MyException("401", "Invalid credentials");
        }
    }

    @PostMapping("/signup")
    public ResponseObject addCustomer(@RequestBody @Valid CustomerDTO customerDTO, BindingResult result) {
        if (!result.hasErrors()) {
            return new ResponseObject(customerService.addCustomer(customerDTO));
        }
        return new ResponseObject("406", result.getFieldError().toString());
    }

    @PostMapping("/login")
    public ResponseObject createAuthenticationToken(@RequestBody CustomerDTO customerDTO) {
        // First authenticate sign in information with user in database
        authenticate(customerDTO.getUsername(), customerDTO.getPassword());

        // then generate token and return
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(customerDTO.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return new ResponseObject(token);
    }
}
