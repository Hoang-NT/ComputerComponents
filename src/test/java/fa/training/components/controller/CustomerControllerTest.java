package fa.training.components.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fa.training.components.dto.CustomerDTO;
import fa.training.components.security.JwtTokenUtil;
import fa.training.components.security.JwtUserDetailsService;
import fa.training.components.service.CustomerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@WithMockUser(roles = {"ADMIN"})
public class CustomerControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper; // to write object as String in JSON format
    @MockBean
    private CustomerService customerService;
    @MockBean
    private JwtUserDetailsService jwtUserDetailsService;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    private static CustomerDTO customerDTO;
    private static List<CustomerDTO> customerDTOS;

    @BeforeAll
    static void setup() {
        customerDTO = new CustomerDTO("Test customer", "username", "12345678", "Test Customer",
                "0908010203", "testcustomer@demo.com", "Address of test customer", "ROLE_USER",
                LocalDateTime.now(), LocalDateTime.now(), "username", true);

        customerDTOS = new ArrayList<>();
        customerDTOS.add(customerDTO);
    }

    @Test
    @DisplayName("Role user get customer by username")
    @Order(1)
    @WithMockUser
    void getCustomerByUsernameRoleUser() throws Exception {
        given(customerService.findByUsernameAndEnable("user")).willReturn(customerDTO);

        mvc.perform(get("/customer"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data.username").value("username"))
                .andExpect(jsonPath("$.data.phone").value("0908010203"));
    }

    @Test
    @DisplayName("Get customer by username")
    @Order(2)
    void getCustomerByUsername() throws Exception {
        String username = customerDTO.getUsername();
        given(customerService.findByUsername(username)).willReturn(customerDTO);

        mvc.perform(get("/customer").param("username", username))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data.phone").value("0908010203"))
                .andExpect(jsonPath("$.data.username").value("username"));
    }

    @Test
    @DisplayName("Get customer by phone")
    @Order(3)
    void getCustomerByPhone() throws Exception {
        String phone = customerDTO.getPhone();
        given(customerService.findByPhone(phone)).willReturn(customerDTO);

        mvc.perform(get("/customer").param("phone", phone))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data.phone").value("0908010203"))
                .andExpect(jsonPath("$.data.username").value("username"));
    }

    @Test
    @DisplayName("Get customer by email")
    @Order(4)
    void getCustomerByEmail() throws Exception {
        String email = customerDTO.getEmail();
        given(customerService.findByEmail(email)).willReturn(customerDTO);

        mvc.perform(get("/customer").param("email", email))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data.phone").value("0908010203"))
                .andExpect(jsonPath("$.data.username").value("username"));
    }

    @Test
    @DisplayName("Get customer by full name")
    @Order(5)
    void getCustomerByFullName() throws Exception {
        String fullName = customerDTO.getFullName();
        given(customerService.findByFullName(eq(fullName), any(Pageable.class))).willReturn(customerDTOS);

        mvc.perform(get("/customer").param("fullName", fullName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data[0].phone").value("0908010203"))
                .andExpect(jsonPath("$.data[0].username").value("username"));
    }

    @Test
    @DisplayName("Get all customer")
    @Order(6)
    void getAllCustomer() throws Exception {
        given(customerService.findAll(any(Pageable.class))).willReturn(customerDTOS);

        mvc.perform(get("/customer"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data[0].phone").value("0908010203"))
                .andExpect(jsonPath("$.data[0].username").value("username"));
    }

    @Test
    @DisplayName("Delete customer")
    @Order(7)
    void deleteCustomer() throws Exception {
        String username = customerDTO.getUsername();
        given(customerService.deleteCustomer(username, "user", true)).willReturn(customerDTO);

        mvc.perform(delete("/customer").param("username", username).with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data.phone").value("0908010203"))
                .andExpect(jsonPath("$.data.username").value("username"));
    }

    @Test
    @DisplayName("Edit customer")
    @Order(8)
    void editCustomer() throws Exception {
        String username = customerDTO.getUsername();
        given(customerService.editCustomer(any(), eq("user"), eq(true))).willReturn(customerDTO);

        mvc.perform(put("/customer")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data.phone").value("0908010203"))
                .andExpect(jsonPath("$.data.username").value("username"));
    }
}
