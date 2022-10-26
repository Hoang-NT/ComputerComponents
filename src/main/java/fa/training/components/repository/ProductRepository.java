package fa.training.components.repository;

import fa.training.components.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    Optional<Product> findByIdAndIsDeleted(String id, boolean isDeleted);
    Optional<Product> findByName(String name);
    Optional<Product> findByNameAndIsDeleted(String name, boolean isDeleted);
    List<Product> findByPrice(int price, Pageable pageable);
    List<Product> findByPriceAndIsDeleted(int price, boolean isDeleted, Pageable pageable);
    List<Product> findByCategory(String category,  Pageable pageable);
    List<Product> findByCategoryAndIsDeleted(String category, boolean isDeleted, Pageable pageable);
    Page<Product> findByIsDeleted(boolean isDeleted, Pageable pageable);

}
