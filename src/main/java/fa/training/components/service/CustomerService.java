package fa.training.components.service;

import fa.training.components.dto.CustomerDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    /**
     * Find customer from database by username
     * @param username of customer
     * @return customer found or throw not found exception
     */
    CustomerDTO findByUsername(String username);

    /**
     * Find customer in database by username whose account is enable
     * @param username of customer
     * @return customer found or throw not found exception
     */
    CustomerDTO findByUsernameAndEnable(String username);

    /**
     * Find customer from database by email
     * @param email of customer
     * @return customer found or throw not found exception
     */
    CustomerDTO findByEmail(String email);

    /**
     * Find customer from database by phone number
     * @param phone of customer
     * @return customer found or throw not found exception
     */
    CustomerDTO findByPhone(String phone);

    /**
     * Find customer from database by full name
     * @param fullName of customer
     * @param pageable a Pageable object
     * @return list of customer found or empty list
     */
    List<CustomerDTO> findByFullName(String fullName, Pageable pageable);

    /**
     * Find all customer from database
     * @param pageable a Pageable object
     * @return list of customer found or empty list
     */
    List<CustomerDTO> findAll(Pageable pageable);

    /**
     * Delete a customer in database
     * @param username of customer
     * @param currentCustomer username of current logged in customer
     * @param isAdmin is current logged in customer an admin
     * @return deleted customer
     */
    CustomerDTO deleteCustomer(String username, String currentCustomer, boolean isAdmin);

    /**
     * Add a customer to database
     * @param customerDTO customer to add
     * @return customer saved to database
     */
    CustomerDTO addCustomer(CustomerDTO customerDTO);

    /**
     * Edit customer in database
     * @param customerDTO customer with update information
     * @param currentCustomer username of current logged in customer
     * @param isAdmin is current logged in customer an admin
     * @return edited customer
     */
    CustomerDTO editCustomer(CustomerDTO customerDTO, String currentCustomer, boolean isAdmin);
}
