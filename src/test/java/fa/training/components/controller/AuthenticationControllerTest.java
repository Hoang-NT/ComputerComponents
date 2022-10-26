package fa.training.components.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fa.training.components.dto.CustomerDTO;
import fa.training.components.security.JwtTokenUtil;
import fa.training.components.security.JwtUserDetails;
import fa.training.components.security.JwtUserDetailsService;
import fa.training.components.service.CustomerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@WithAnonymousUser
// Need remove filters so can test with anonymous user
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper; // to write object as String in JSON format
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private JwtUserDetailsService jwtUserDetailsService;
    @MockBean
    private CustomerService customerService;

    private static CustomerDTO customerDTO;

    @BeforeAll
    static void setup() {
        customerDTO = new CustomerDTO("Test customer", "username", "12345678", "Test Customer",
                "0908010203", "testcustomer@demo.com", "Address of test customer", "ROLE_USER",
                LocalDateTime.now(), LocalDateTime.now(), "username", true);
    }

    @Test
    @DisplayName("Sign up success")
    @Order(1)
    void addCustomerSuccess() throws Exception{
        given(customerService.addCustomer(any())).willReturn(customerDTO);

        mvc.perform(post("/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data.username").value("username"))
                .andExpect(jsonPath("$.data.phone").value("0908010203"));
    }

    @Test
    @DisplayName("Sign up invalid customer")
    @Order(2)
    void addCustomerInvalid() throws Exception{
        CustomerDTO invalidCustomer = new CustomerDTO("Test customer", "username", "12345", "Test Customer",
                "0908010203", "testcustomer@demo.com", "Address of test customer", "ROLE_USER",
                LocalDateTime.now(), LocalDateTime.now(), "username", true);

        mvc.perform(post("/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCustomer)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code").value("406"))
                .andExpect(jsonPath("$.message", containsString("Field error")));
    }

    @Test
    @DisplayName("Login success")
    @Order(3)
    public void createAuthenticationToken() throws Exception {
        String username = customerDTO.getUsername();
        List<GrantedAuthority> authorities = new ArrayList<>();
        GrantedAuthority authority = new SimpleGrantedAuthority(customerDTO.getRole());
        authorities.add(authority);
        UserDetails userDetails = new JwtUserDetails(username, customerDTO.getPassword(), authorities, customerDTO.getEnable());
        String token = "This is token create by JwtTokenUtil";
        given(jwtUserDetailsService.loadUserByUsername(username)).willReturn(userDetails);
        given(jwtTokenUtil.generateToken(any())).willReturn(token);

        mvc.perform(post("/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data").value(token));
    }

    @Test
    @DisplayName("Login user disable")
    @Order(4)
    public void createAuthenticationTokenUserDisable() throws Exception {
        given(authenticationManager.authenticate(any())).willThrow(DisabledException.class);

        mvc.perform(post("/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("User disabled"));
    }

    @Test
    @DisplayName("Login invalid credentials")
    @Order(5)
    public void createAuthenticationTokenInvalidCredentials() throws Exception {
        given(authenticationManager.authenticate(any())).willThrow(BadCredentialsException.class);

        mvc.perform(post("/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }
}
