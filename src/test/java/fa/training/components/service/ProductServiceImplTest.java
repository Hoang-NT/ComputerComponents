package fa.training.components.service;

import fa.training.components.dto.ProductDTO;
import fa.training.components.entity.Product;
import fa.training.components.repository.ProductRepository;
import fa.training.components.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Autowired
    private final ModelMapper modelMapper = new ModelMapper();

    private Product product1;
    private Product product2;
    private Product product3;

    List<Product> listProduct;
    List<Product> listProduct1;

    @BeforeEach
    void setup() {
        listProduct = new ArrayList<Product>();
        listProduct1 = new ArrayList<Product>();
        product1 = new Product("1", "Keyboard1", "keyboard", 2050, 0.1f, 15, false);
        product2 = new Product("2", "Keyboard2", "keyboard", 3000, 0.1f, 12, true);
        product3 = new Product("3", "Mouse1", "mouse", 2050, 0.2f, 12, true);
        listProduct.add(product1);
        listProduct.add(product2);
        listProduct.add(product3);
        listProduct1.add(product1);
    }
    @Test
    void findProductByCategoryTest() {
        String category = "keyboard";
        listProduct1.add(product2);
        Mockito.when(productRepository.findByCategory(category,PageRequest.of(0, 5))).thenReturn(listProduct1);
        List<ProductDTO> productDTOS = productService.findProductByCategory(category, PageRequest.of(0, 5), true);
        assertEquals("Keyboard1", productDTOS.get(0).getName());
        assertEquals("Keyboard2", productDTOS.get(1).getName());
        assertEquals(listProduct1.get(0).getId(), productDTOS.get(0).getId());
     }

    @Test
    void findProductByCategoryAndIsDeletedTest() {
        String category = "keyboard";
        Product product4 = new Product("4", "Keyboard3", "keyboard", 3000, 0.1f, 12, false);
        listProduct.add(product1);
        listProduct.add(product2);
        listProduct.add(product3);
        listProduct1.add(product1);
        listProduct1.add(product4);
        Mockito.when(productRepository.findByCategoryAndIsDeleted(category,false, PageRequest.of(0, 5))).thenReturn(listProduct1);
        List<ProductDTO> productDTOS = productService.findProductByCategory(category, PageRequest.of(0, 5), false);
        assertEquals("Keyboard1", productDTOS.get(0).getName());
        assertEquals(listProduct1.get(0).getId(), productDTOS.get(0).getId());
    }

    @Test
    void findProductByIdTest() {
        String id = "2";
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(product2));
        ProductDTO productDTO = productService.findProductById(id, true);
        assertEquals("Keyboard2", productDTO.getName());
        assertEquals("keyboard", productDTO.getCategory());
        assertEquals(product2.getId(), productDTO.getId());
    }

    @Test
    void findProductByIdAndIsDeletedTest() {
        String id = "1";
        Mockito.when(productRepository.findByIdAndIsDeleted(id, false)).thenReturn(Optional.of(product1));
        ProductDTO productDTO = productService.findProductById(id, false);
        assertEquals("Keyboard1", productDTO.getName());
        assertEquals("keyboard", productDTO.getCategory());
        assertEquals(product1.getId(), productDTO.getId());
    }

    @Test
    void findProductByNameTest() {
        String name = "Keyboard2";
        Mockito.when(productRepository.findByName(name)).thenReturn(Optional.of(product2));
        ProductDTO productDTO = productService.findProductByName(name, true);
        assertEquals("Keyboard2", productDTO.getName());
        assertEquals("keyboard", productDTO.getCategory());
        assertEquals(product2.getId(), productDTO.getId());
    }

    @Test
    void findProductByNameAndIsDeletedTest() {
        String name = "Keyboard1";
        Mockito.when(productRepository.findByNameAndIsDeleted(name, false)).thenReturn(Optional.of(product1));
        ProductDTO productDTO = productService.findProductByName(name, false);
        assertEquals("Keyboard1", productDTO.getName());
        assertEquals("keyboard", productDTO.getCategory());
        assertEquals(product1.getId(), productDTO.getId());
    }

    @Test
    void findProductByPriceTest() {
        int price = 3000;
        listProduct1.add(product3);
        Mockito.when(productRepository.findByPrice(price,PageRequest.of(0, 5))).thenReturn(listProduct1);
        List<ProductDTO> productDTOS = productService.findProductByPrice(price, PageRequest.of(0, 5), true);
        assertEquals("Keyboard1", productDTOS.get(0).getName());
        assertEquals("Mouse1", productDTOS.get(1).getName());
        assertEquals(listProduct1.get(0).getId(), productDTOS.get(0).getId());
    }

    @Test
    void findProductByPriceAndIsDeletedTest() {
        int price = 3000;
        Product product4 = new Product("4", "Keyboard3", "keyboard", 3000, 0.1f, 12, false);
        listProduct1.add(product4);
        Mockito.when(productRepository.findByPriceAndIsDeleted(price, false,PageRequest.of(0, 5))).thenReturn(listProduct1);
        List<ProductDTO> productDTOS = productService.findProductByPrice(price, PageRequest.of(0, 5), false);
        assertEquals("Keyboard3", productDTOS.get(1).getName());
        assertEquals(listProduct1.get(0).getId(), productDTOS.get(0).getId());
    }

    @Test
    void findAllProductTest() {
        Page<Product> productPage = new PageImpl<>(listProduct);
        Mockito.when(productRepository.findAll(PageRequest.of(0, 5))).thenReturn(productPage);
        List<ProductDTO> productDTOS = productService.findAllProduct(PageRequest.of(0, 5), true);
        assertEquals("Keyboard1", productDTOS.get(0).getName());
        assertEquals("Mouse1", productDTOS.get(2).getName());
        assertEquals(listProduct.get(1).getId(), productDTOS.get(1).getId());
    }

    @Test
    void findAllProductAndIsDeletedTest() {
        Page<Product> productPage = new PageImpl<>(listProduct1);
        Mockito.when(productRepository.findByIsDeleted(false, PageRequest.of(0, 5))).thenReturn(productPage);
        List<ProductDTO> productDTOS = productService.findAllProduct(PageRequest.of(0, 5), false);
        assertEquals("Keyboard1", productDTOS.get(0).getName());
        assertEquals(2050, productDTOS.get(0).getPrice());
        assertEquals(listProduct.get(0).getId(), productDTOS.get(0).getId());
    }

}
