package fa.training.components.service;

import fa.training.components.dto.ProductDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ProductService {

    ProductDTO findProductById(String id, boolean isAdminOrStaff);
    ProductDTO findProductByName(String name, boolean isAdminOrStaff);
    List<ProductDTO> findProductByPrice(Integer price, Pageable pageable, boolean isAdminOrStaff);
    List<ProductDTO> findProductByCategory(String category,Pageable pageable, boolean isAdminOrStaff);
    List<ProductDTO> findAllProduct(Pageable pageable, boolean isAdminOrStaff);
    ProductDTO deleteProductByName(String name, boolean isAdmin);
    ProductDTO editProduct(String name, ProductDTO productDTO, boolean isAdmin);
    ProductDTO addProduct(ProductDTO productDTO, boolean isAdmin);

}