package fa.training.components.repository;

import fa.training.components.entity.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    Customer findByUsername(String username);
    Customer findByEmail(String email);
    Customer findByPhone(String phone);
    Customer findByUsernameAndEnable(String username, boolean enable);
    List<Customer> findByFullNameIgnoreCaseContains(String fullName, Pageable pageable);
}
