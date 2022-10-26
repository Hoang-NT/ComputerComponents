package fa.training.components.service.impl;

import fa.training.components.dto.CustomerDTO;
import fa.training.components.entity.Customer;
import fa.training.components.exception.MyException;
import fa.training.components.repository.CustomerRepository;
import fa.training.components.service.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public CustomerDTO findByUsername(String username) {
        Customer customer = customerRepository.findByUsername(username);
        if (customer == null) {
            throw new MyException("400", "Customer not found");
        }
        customer.setPassword(null);
        return modelMapper.map(customer, CustomerDTO.class);
    }

    @Override
    public CustomerDTO findByUsernameAndEnable(String username) {
        Customer customer = customerRepository.findByUsernameAndEnable(username, true);
        if (customer == null) {
            throw new MyException("400", "Customer not found");
        }
        customer.setPassword(null);
        customer.hideSensitiveInfo();
        return modelMapper.map(customer, CustomerDTO.class);
    }

    @Override
    public CustomerDTO findByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            throw new MyException("400", "Customer not found");
        }
        customer.setPassword(null);
        return modelMapper.map(customer, CustomerDTO.class);
    }

    @Override
    public CustomerDTO findByPhone(String phone) {
        Customer customer = customerRepository.findByPhone(phone);
        if (customer == null) {
            throw new MyException("400", "Customer not found");
        }
        customer.setPassword(null);
        return modelMapper.map(customer, CustomerDTO.class);
    }

    @Override
    public List<CustomerDTO> findByFullName(String fullName, Pageable pageable) {
        List<Customer> customers = customerRepository.findByFullNameIgnoreCaseContains(fullName, pageable);
        return customers.stream().map(customer -> {
            CustomerDTO customerDTO = modelMapper.map(customer, CustomerDTO.class);
            customerDTO.setPassword(null);
            return customerDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<CustomerDTO> findAll(Pageable pageable) {
        Page<Customer> customers = customerRepository.findAll(pageable);
        return customers.stream().map(customer -> {
            CustomerDTO customerDTO = modelMapper.map(customer, CustomerDTO.class);
            customerDTO.setPassword(null);
            return customerDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public CustomerDTO deleteCustomer(String username, String currentCustomer, boolean isAdmin) {
        Customer customerToDelete = customerRepository.findByUsernameAndEnable(username, true);
        if (customerToDelete == null) {
            throw new MyException("400", "Customer not found");
        }
        if (!isAdmin) {
            if (!username.equals(currentCustomer)) {
                throw new MyException("403", "Just owner or admin can delete customer");
            }
        }
        customerToDelete.setEnable(false);
        Customer deletedCustomer = customerRepository.saveAndFlush(customerToDelete);
        deletedCustomer.setPassword(null);
        if (!isAdmin) {
            deletedCustomer.hideSensitiveInfo();
        }
        return modelMapper.map(deletedCustomer,CustomerDTO.class);
    }

    @Override
    public CustomerDTO addCustomer(CustomerDTO customerDTO) {
        // Check if username and email and phone already used (even disable customer)
        Customer customerInDB = customerRepository.findByUsername(customerDTO.getUsername());
        if (customerInDB != null) {
            throw new MyException("405", "Username existed");
        }
        customerInDB = customerRepository.findByEmail(customerDTO.getEmail());
        if (customerInDB != null) {
            throw new MyException("405", "Email used");
        }
        customerInDB = customerRepository.findByPhone(customerDTO.getPhone());
        if (customerInDB != null) {
            throw new MyException("405", "Phone number used");
        }

        // Set ID
        UUID id = UUID.randomUUID();
        customerDTO.setId(id.toString());

        // Encode and set encoded password
        String encodedPassword = passwordEncoder.encode(customerDTO.getPassword());
        customerDTO.setPassword(encodedPassword);

        // Set default role and enable
        customerDTO.setRole("ROLE_USER");
        customerDTO.setEnable(true);

        Customer savedCustomer = customerRepository.save(modelMapper.map(customerDTO, Customer.class));
        savedCustomer.setPassword(null);
        savedCustomer.hideSensitiveInfo();
        return modelMapper.map(savedCustomer, CustomerDTO.class);
    }

    @Override
    public CustomerDTO editCustomer(CustomerDTO customerDTO, String currentCustomer, boolean isAdmin) {
        Customer customerToEdit;
        if (isAdmin) {
            customerToEdit = customerRepository.findByUsername(customerDTO.getUsername());
        } else {
            customerToEdit = customerRepository.findByUsernameAndEnable(customerDTO.getUsername(), true);
        }
        if (customerToEdit == null) {
            throw new MyException("400", "Customer not found");
        }
        if (!isAdmin) {
            if (!customerToEdit.getUsername().equals(currentCustomer)) {
                throw new MyException("403", "Just owner or admin can edit customer");
            }
        }

        // If change email then need to check if new email used
        if (customerDTO.getEmail() != null && !customerToEdit.getEmail().equals(customerDTO.getEmail())) {
            Customer emailUsed = customerRepository.findByEmail(customerDTO.getEmail());
            if (emailUsed != null) {
                throw new MyException("405", "Email used");
            }
            customerToEdit.setEmail(customerDTO.getEmail());
        }

        // If change phone number, then need to check if new number used
        if (customerDTO.getPhone() != null && !customerToEdit.getPhone().equals(customerDTO.getPhone())) {
            Customer phoneUsed = customerRepository.findByPhone(customerDTO.getPhone());
            if (phoneUsed != null) {
                throw new MyException("405", "Phone number used");
            }
            customerToEdit.setPhone(customerDTO.getPhone());
        }

        // If change password, check valid length
        if (customerDTO.getPassword() != null) {
            if (customerDTO.getPassword().length() < 6 || customerDTO.getPassword().length() > 60) {
                throw new MyException("406", "Field error in object 'customerDTO' on field 'password': length must be between 6 and 60");
            }
            customerToEdit.setPassword(passwordEncoder.encode(customerDTO.getPassword()));
        }

        // Only admin can change role and enable
        if (isAdmin) {
            // If change role, check if role is valid
            if (customerDTO.getRole() != null) {
                if (!"ROLE_USER".equals(customerDTO.getRole())
                        && !"ROLE_ADMIN".equals(customerDTO.getRole())
                        && !"ROLE_STAFF".equals(customerDTO.getRole())) {
                    throw new MyException("406",
                            "Field error in object 'customerDTO' on field 'role': must be 'ROLE_USER', 'ROLE_STAFF' or 'ROLE_ADMIN'");
                }
                customerToEdit.setRole(customerDTO.getRole());
            }
            if (customerDTO.getEnable() != null) {
                customerToEdit.setEnable(customerDTO.getEnable());
            }
        }

        // If change full name or address, check if it is not blank
        if (customerDTO.getFullName() != null) {
            if (customerDTO.getFullName().trim().length() == 0) {
                throw new MyException("406", "Field error in object 'customerDTO' on field 'fullName': must not be blank");
            }
            customerToEdit.setFullName(customerDTO.getFullName());
        }
        if (customerDTO.getAddress() != null) {
            if (customerDTO.getAddress().trim().length() == 0) {
                throw new MyException("406", "Field error in object 'customerDTO' on field 'address': must not be blank");
            }
            customerToEdit.setAddress(customerDTO.getAddress());
        }

        Customer editedCustomer = customerRepository.saveAndFlush(customerToEdit);
        editedCustomer.setPassword(null);
        if (!isAdmin) {
            editedCustomer.hideSensitiveInfo();
        }
        return modelMapper.map(editedCustomer, CustomerDTO.class);
    }
}
