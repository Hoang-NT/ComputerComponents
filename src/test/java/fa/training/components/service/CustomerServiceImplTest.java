package fa.training.components.service;

import fa.training.components.dto.CustomerDTO;
import fa.training.components.entity.Customer;
import fa.training.components.exception.MyException;
import fa.training.components.repository.CustomerRepository;
import fa.training.components.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {
    @InjectMocks
    private CustomerServiceImpl customerService;
    @Mock
    private CustomerRepository customerRepository;
    @Spy
    private ModelMapper modelMapper;
    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    private static CustomerDTO customerDTO;
    private static List<CustomerDTO> customerDTOS;
    private static Pageable pageable;
    private static List<Customer> customers;
    private Customer customer;

    @BeforeAll
    static void setup() {
        customerDTO = new CustomerDTO("Test customer", "username", "12345678", "Test Customer", "0908010203",
                "testcustomer@demo.com", "Address of test customer", "ROLE_USER", LocalDateTime.now(),
                LocalDateTime.now(), "username", true);

        customerDTOS = new ArrayList<>();
        customerDTOS.add(customerDTO);

        customers = new ArrayList<>();

        pageable = PageRequest.of(0, 10, Sort.by("role").and(Sort.by("username")));
    }

    @BeforeEach
    void init() {
        customer = modelMapper.map(customerDTO, Customer.class);
        customer.setPassword(passwordEncoder.encode(customerDTO.getPassword()));

        customers.add(customer);
    }

    @AfterEach
    void tearDown() {
        customers.clear();
    }

    @Test
    @DisplayName("Edit customer success")
    @Order(1)
    void editCustomerSuccess() {
        // Given
        Customer customerInDB = new Customer("Customer in database", "username", "12345678",
                "customer to edit", "0907050406", "customertoedit@demo.com",
                "Address od customer to edit", "ROLE_USER", LocalDateTime.now(), LocalDateTime.now(),
                "usernametoedit", true);
        given(customerRepository.findByUsernameAndEnable(customerDTO.getUsername(), true)).willReturn(customerInDB);
        given(customerRepository.findByEmail(customerDTO.getEmail())).willReturn(null);
        given(customerRepository.findByPhone(customerDTO.getPhone())).willReturn(null);
        given(customerRepository.saveAndFlush(any(Customer.class))).willReturn(customer);

        // when
        CustomerDTO actual = customerService.editCustomer(customerDTO, "username", false);
        CustomerDTO expected = modelMapper.map(customer, CustomerDTO.class);

        // then
        assertEquals(expected.getPhone(), actual.getPhone());
        assertEquals(expected.getUsername(), actual.getUsername());
    }

    @Test
    @DisplayName("Edit customer throw exception \"Customer not found\"")
    @Order(2)
    void editCustomerNotFound() {
        // Given
        given(customerRepository.findByUsernameAndEnable(customerDTO.getUsername(), true)).willReturn(null);

        // when
        MyException exception = assertThrows(MyException.class, () -> customerService.editCustomer(customerDTO, "username", false));

        // then
        assertEquals("Customer not found", exception.getMessage());
        assertEquals("400", exception.getCode());
    }

    @Test
    @DisplayName("Edit customer throw exception \"Just owner or admin can edit customer\"")
    @Order(3)
    void editCustomerNotOwnerNorAdmin() {
        // Given
        Customer customerInDB = new Customer("Customer to edit", "usernametoedit", "12345678",
                "customer to edit", "0907050406", "customertoedit@demo.com",
                "Address od customer to edit", "ROLE_USER", LocalDateTime.now(), LocalDateTime.now(),
                "usernametoedit", true);
        given(customerRepository.findByUsernameAndEnable(customerDTO.getUsername(), true)).willReturn(customerInDB);

        // when
        MyException exception = assertThrows(MyException.class, () -> customerService.editCustomer(customerDTO, "username", false));

        // then
        assertEquals("Just owner or admin can edit customer", exception.getMessage());
        assertEquals("403", exception.getCode());
    }

    @Test
    @DisplayName("Edit customer throw exception \"Email used\"")
    @Order(4)
    void editCustomerNewEmailUsed() {
        // Given
        Customer customerInDB = new Customer("Customer to edit", "usernametoedit", "12345678",
                "customer to edit", "0907050406", "customertoedit@demo.com",
                "Address od customer to edit", "ROLE_USER", LocalDateTime.now(), LocalDateTime.now(),
                "usernametoedit", true);
        given(customerRepository.findByUsername(customerDTO.getUsername())).willReturn(customerInDB);
        given(customerRepository.findByEmail(customerDTO.getEmail())).willReturn(customerInDB);

        // when
        MyException exception = assertThrows(MyException.class, () -> customerService.editCustomer(customerDTO, "username", true));

        // then
        assertEquals("Email used", exception.getMessage());
        assertEquals("405", exception.getCode());
    }

    @Test
    @DisplayName("Edit customer throw exception \"Phone number used\"")
    @Order(5)
    void editCustomerNewPhoneNumberUsed() {
        // Given
        Customer customerInDB = new Customer("Customer to edit", "usernametoedit", "12345678",
                "customer to edit", "0907050406", "customertoedit@demo.com",
                "Address od customer to edit", "ROLE_USER", LocalDateTime.now(), LocalDateTime.now(),
                "usernametoedit", true);
        given(customerRepository.findByUsername(customerDTO.getUsername())).willReturn(customerInDB);
        given(customerRepository.findByEmail(customerDTO.getEmail())).willReturn(null);
        given(customerRepository.findByPhone(customerDTO.getPhone())).willReturn(customerInDB);

        // when
        MyException exception = assertThrows(MyException.class, () -> customerService.editCustomer(customerDTO, "username", true));

        // then
        assertEquals("Phone number used", exception.getMessage());
        assertEquals("405", exception.getCode());
    }

    @Test
    @DisplayName("Edit customer throw exception field 'password' error")
    @Order(6)
    void editCustomerFieldPasswordError() {
        // Given
        CustomerDTO customerInvalidPassword = new CustomerDTO();
        customerInvalidPassword.setUsername("Invalid password");
        customerInvalidPassword.setPassword("12345");
        given(customerRepository.findByUsername(customerInvalidPassword.getUsername())).willReturn(customer);

        // when
        MyException exception = assertThrows(MyException.class, () -> customerService.editCustomer(customerInvalidPassword, "username", true));

        // then
        assertTrue(exception.getMessage().contains("Field error"));
        assertTrue(exception.getMessage().contains("password"));
        assertTrue(exception.getMessage().contains("length must be between 6 and 60"));
        assertEquals("406", exception.getCode());
    }

    @Test
    @DisplayName("Edit customer throw exception field 'role' error")
    @Order(7)
    void editCustomerFieldRoleError() {
        // Given
        CustomerDTO customerInvalidRole = new CustomerDTO();
        customerInvalidRole.setUsername("Invalid role");
        customerInvalidRole.setRole("Invalid role");
        given(customerRepository.findByUsername(customerInvalidRole.getUsername())).willReturn(customer);

        // when
        MyException exception = assertThrows(MyException.class, () -> customerService.editCustomer(customerInvalidRole, "username", true));

        // then
        assertTrue(exception.getMessage().contains("Field error"));
        assertTrue(exception.getMessage().contains("role"));
        assertTrue(exception.getMessage().contains("must be 'ROLE_USER' or 'ROLE_ADMIN'"));
        assertEquals("406", exception.getCode());
    }

    @Test
    @DisplayName("Edit customer throw exception field 'fullName' error")
    @Order(8)
    void editCustomerFieldFullNameError() {
        // Given
        CustomerDTO customerInvalidName = new CustomerDTO();
        customerInvalidName.setUsername("Invalid full name");
        customerInvalidName.setFullName(" ");
        given(customerRepository.findByUsername(customerInvalidName.getUsername())).willReturn(customer);

        // when
        MyException exception = assertThrows(MyException.class, () -> customerService.editCustomer(customerInvalidName, "username", true));

        // then
        assertTrue(exception.getMessage().contains("Field error"));
        assertTrue(exception.getMessage().contains("fullName"));
        assertTrue(exception.getMessage().contains("must not be blank"));
        assertEquals("406", exception.getCode());
    }

    @Test
    @DisplayName("Edit customer throw exception field 'address' error")
    @Order(9)
    void editCustomerFieldAddressError() {
        // Given
        CustomerDTO customerInvalidAddress = new CustomerDTO("Invalid address", "usernametoedit", "12345678",
                "customer to edit", "0907050406", "customertoedit@demo.com",
                " ", "ROLE_USER", LocalDateTime.now(), LocalDateTime.now(),
                "usernametoedit", true);
        given(customerRepository.findByUsername(customerInvalidAddress.getUsername())).willReturn(customer);

        // when
        MyException exception = assertThrows(MyException.class, () -> customerService.editCustomer(customerInvalidAddress, "username", true));

        // then
        assertTrue(exception.getMessage().contains("Field error"));
        assertTrue(exception.getMessage().contains("address"));
        assertTrue(exception.getMessage().contains("must not be blank"));
        assertEquals("406", exception.getCode());
    }

    @Test
    @DisplayName("Add customer success")
    @Order(10)
    void addCustomerSuccess() {
        // Given
        given(customerRepository.findByUsername(customerDTO.getUsername())).willReturn(null);
        given(customerRepository.findByEmail(customerDTO.getEmail())).willReturn(null);
        given(customerRepository.findByPhone(customerDTO.getPhone())).willReturn(null);
        given(customerRepository.save(any(Customer.class))).willReturn(customer);

        // when
        CustomerDTO actual = customerService.addCustomer(customerDTO);
        CustomerDTO expected = modelMapper.map(customer, CustomerDTO.class);

        // then
        assertEquals(expected.getPhone(), actual.getPhone());
        assertEquals(expected.getUsername(), actual.getUsername());
    }

    @Test
    @DisplayName("Add customer throw exception \"Phone number used\"")
    @Order(11)
    void addCustomerPhoneNumberUsed() {
        // Given
        given(customerRepository.findByUsername(customerDTO.getUsername())).willReturn(null);
        given(customerRepository.findByEmail(customerDTO.getEmail())).willReturn(null);
        given(customerRepository.findByPhone(customerDTO.getPhone())).willReturn(customer);

        // when
        MyException exception = assertThrows(MyException.class, () -> customerService.addCustomer(customerDTO));

        // then
        assertEquals("Phone number used", exception.getMessage());
        assertEquals("405", exception.getCode());
    }

    @Test
    @DisplayName("Add customer throw exception \"Email used\"")
    @Order(12)
    void addCustomerEmailUsed() {
        // Given
        given(customerRepository.findByUsername(customerDTO.getUsername())).willReturn(null);
        given(customerRepository.findByEmail(customerDTO.getEmail())).willReturn(customer);

        // when
        MyException exception = assertThrows(MyException.class, () -> customerService.addCustomer(customerDTO));

        // then
        assertEquals("Email used", exception.getMessage());
        assertEquals("405", exception.getCode());
    }

    @Test
    @DisplayName("Add customer throw exception \"Username existed\"")
    @Order(13)
    void addCustomerUsernameExisted() {
        // Given
        given(customerRepository.findByUsername(customerDTO.getUsername())).willReturn(customer);

        // when
        MyException exception = assertThrows(MyException.class, () -> customerService.addCustomer(customerDTO));

        // then
        assertEquals("Username existed", exception.getMessage());
        assertEquals("405", exception.getCode());
    }

    @Test
    @DisplayName("Delete customer success")
    @Order(14)
    void deleteCustomerSuccess() {
        // Given
        String username = customer.getUsername();
        given(customerRepository.findByUsernameAndEnable(username, true)).willReturn(customer);
        given(customerRepository.saveAndFlush(any(Customer.class))).willReturn(customer);

        // when
        CustomerDTO actual = customerService.deleteCustomer(username, "username", false);
        CustomerDTO expected = modelMapper.map(customer, CustomerDTO.class);

        // then
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUsername(), actual.getUsername());
    }

    @Test
    @DisplayName("Delete customer throw exception \"Customer not found\"")
    @Order(15)
    void deleteCustomerNotFound() {
        // Given
        String username = customer.getUsername();
        given(customerRepository.findByUsernameAndEnable(username, true)).willReturn(null);

        // when
        MyException exception = assertThrows(MyException.class, () -> customerService.deleteCustomer(username, "username", false));

        // then
        assertEquals("Customer not found", exception.getMessage());
        assertEquals("400", exception.getCode());
    }

    @Test
    @DisplayName("Delete customer throw exception \"Just owner or admin can delete customer\"")
    @Order(16)
    void deleteCustomerNotOwnerNorAdmin() {
        // Given
        String username = customer.getUsername();
        given(customerRepository.findByUsernameAndEnable(username, true)).willReturn(customer);

        // when
        MyException exception = assertThrows(MyException.class, () -> customerService.deleteCustomer(username, "notowner", false));

        // then
        assertEquals("Just owner or admin can delete customer", exception.getMessage());
        assertEquals("403", exception.getCode());
    }

    @Test
    @DisplayName("Find all customer")
    @Order(17)
    void findAll() {
        // Given
        Page<Customer> page = new PageImpl<>(customers);
        given(customerRepository.findAll(pageable)).willReturn(page);

        // when
        List<CustomerDTO> actual = customerService.findAll(pageable);

        // then
        assertEquals(customerDTOS.size(), actual.size());
        assertEquals(customerDTOS.get(0).getUsername(), actual.get(0).getUsername());
    }

    @Test
    @DisplayName("Find customer by full name")
    @Order(18)
    void findByFullName() {
        // Given
        given(customerRepository.findByFullNameIgnoreCaseContains(customer.getFullName(), pageable)).willReturn(customers);

        // when
        List<CustomerDTO> actual = customerService.findByFullName(customer.getFullName(), pageable);

        // then
        assertEquals(customerDTOS.size(), actual.size());
        assertEquals(customerDTOS.get(0).getUsername(), actual.get(0).getUsername());
    }

    @Test
    @DisplayName("Find customer by phone success")
    @Order(19)
    void findByPhoneSuccess() {
        // Given
        given(customerRepository.findByPhone(customer.getPhone())).willReturn(customer);

        // when
        CustomerDTO actual = customerService.findByPhone(customer.getPhone());
        CustomerDTO expected = modelMapper.map(customer, CustomerDTO.class);

        // then
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getPhone(), actual.getPhone());
    }

    @Test
    @DisplayName("Find customer by phone not found")
    @Order(20)
    void findByPhoneNotFound() {
        // Given
        given(customerRepository.findByPhone(customer.getPhone())).willReturn(null);

        // when
        MyException exception = assertThrows(MyException.class, () -> customerService.findByPhone(customer.getPhone()));

        // then
        assertEquals("Customer not found", exception.getMessage());
        assertEquals("400", exception.getCode());
    }

    @Test
    @DisplayName("Find customer by email success")
    @Order(21)
    void findByEmailSuccess() {
        // Given
        given(customerRepository.findByEmail(customer.getEmail())).willReturn(customer);

        // when
        CustomerDTO actual = customerService.findByEmail(customer.getEmail());
        CustomerDTO expected = modelMapper.map(customer, CustomerDTO.class);

        // then
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getPhone(), actual.getPhone());
    }

    @Test
    @DisplayName("Find customer by email not found")
    @Order(22)
    void findByEmailNotFound() {
        // Given
        given(customerRepository.findByEmail(customer.getEmail())).willReturn(null);

        // when
        MyException exception = assertThrows(MyException.class, () -> customerService.findByEmail(customer.getEmail()));

        // then
        assertEquals("Customer not found", exception.getMessage());
        assertEquals("400", exception.getCode());
    }

    @Test
    @DisplayName("Find customer by username and enable")
    @Order(23)
    void findByUsernameAndEnable() {
        // Given
        given(customerRepository.findByUsernameAndEnable(customer.getFullName(), true)).willReturn(customer);

        // when
        CustomerDTO actual = customerService.findByUsernameAndEnable(customer.getFullName());
        CustomerDTO expected = modelMapper.map(customer, CustomerDTO.class);

        // then
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getPhone(), actual.getPhone());
    }

    @Test
    @DisplayName("Find customer by username and enable not found")
    @Order(24)
    void findByUsernameAndEnableNotFound() {
        // Given
        given(customerRepository.findByUsernameAndEnable(customer.getUsername(), true)).willReturn(null);

        // when
        MyException exception = assertThrows(MyException.class, () -> customerService.findByUsernameAndEnable(customer.getUsername()));

        // then
        assertEquals("Customer not found", exception.getMessage());
        assertEquals("400", exception.getCode());
    }

    @Test
    @DisplayName("Find customer by username")
    @Order(25)
    void findByUsername() {
        // Given
        given(customerRepository.findByUsername(customer.getFullName())).willReturn(customer);

        // when
        CustomerDTO actual = customerService.findByUsername(customer.getFullName());
        CustomerDTO expected = modelMapper.map(customer, CustomerDTO.class);

        // then
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getPhone(), actual.getPhone());
    }

    @Test
    @DisplayName("Find customer by username not found")
    @Order(26)
    void findByUsernameNotFound() {
        // Given
        given(customerRepository.findByUsername(customer.getUsername())).willReturn(null);

        // when
        MyException exception = assertThrows(MyException.class, () -> customerService.findByUsername(customer.getUsername()));

        // then
        assertEquals("Customer not found", exception.getMessage());
        assertEquals("400", exception.getCode());
    }
}
