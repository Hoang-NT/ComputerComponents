package fa.training.components.service.impl;

import fa.training.components.dto.ProductDTO;
import fa.training.components.entity.Product;
import fa.training.components.entity.Specification;
import fa.training.components.exception.MyException;
import fa.training.components.repository.ProductRepository;
import fa.training.components.repository.SpecificationRepository;
import fa.training.components.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SpecificationRepository specificationRepository;
    @Autowired
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public ProductDTO findProductById(String id, boolean isAdminOrStaff) {
        Optional<Product> product;
        if (isAdminOrStaff) {
            product = productRepository.findById(id);
        } else {
            product = productRepository.findByIdAndIsDeleted(id, false);
        }
        return product.map(value -> modelMapper.map(value, ProductDTO.class)).orElse(null);
    }

    @Override
    public ProductDTO findProductByName(String name, boolean isAdminOrStaff) {
        Optional<Product> product;
        if (isAdminOrStaff) {
            product = productRepository.findByName(name);
        } else {
            product = productRepository.findByNameAndIsDeleted(name, false);
        }
        return product.map(value -> modelMapper.map(value, ProductDTO.class)).orElse(null);
    }

    @Override
    public List<ProductDTO> findProductByPrice(Integer price, Pageable pageable, boolean isAdminOrStaff) {
        List<Product> products;
        if (isAdminOrStaff) {
            products = productRepository.findByPrice(price, pageable);
        } else {
            products = productRepository.findByPriceAndIsDeleted(price, false, pageable);
        }
        return products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> findProductByCategory(String category, Pageable pageable, boolean isAdminOrStaff) {
        List<Product> products;
        if (isAdminOrStaff) {
            products = productRepository.findByCategory(category, pageable);
        } else {
            products = productRepository.findByCategoryAndIsDeleted(category, false, pageable);
        }
        return products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> findAllProduct(Pageable pageable, boolean isAdminOrStaff) {
        Page<Product> products;
        if (isAdminOrStaff) {
            products = productRepository.findAll(pageable);
        } else {
            products = productRepository.findByIsDeleted(false, pageable);
        }
        return products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).collect(Collectors.toList());
    }

    @Override
    public ProductDTO deleteProductByName(String name, boolean isAdmin) {
        ProductDTO productToDelete = findProductByName(name, isAdmin);
        if (productToDelete != null) {
            productToDelete.setDeleted(true);
            Product deletedProduct = productRepository.saveAndFlush(modelMapper.map(productToDelete, Product.class));

            // Also delete specification of this product
            Optional<Specification> specification = specificationRepository.findByProductIDAndIsDeleted(deletedProduct.getId(), false);
            if (specification.isPresent()) {
                Specification specificationToDelete = specification.get();
                specificationToDelete.setIsDeleted(true);
                specificationRepository.saveAndFlush(specificationToDelete);
            }

            return modelMapper.map(deletedProduct, ProductDTO.class);
        }
        throw new MyException("400", "Product not found");
    }

    @Override
    public ProductDTO editProduct(String name, ProductDTO productDTO, boolean isAdmin) {
        ProductDTO productToEdit = findProductByName(name, isAdmin);
        if (!Objects.isNull(productDTO) && productToEdit.isDeleted()){
            productToEdit.setName(Objects.isNull(productDTO.getName()) ? productToEdit.getName() : productDTO.getName());
            productToEdit.setCategory(Objects.isNull(productDTO.getCategory()) ? productToEdit.getCategory() : productDTO.getCategory());
            productToEdit.setDiscount(Objects.isNull(productDTO.getDiscount()) ? productToEdit.getDiscount() : productDTO.getDiscount());
            productToEdit.setPrice(Objects.isNull(productDTO.getPrice()) ? productToEdit.getPrice() : productDTO.getPrice());
            productToEdit.setStock(Objects.isNull(productDTO.getStock()) ? productToEdit.getStock() : productDTO.getStock());
            productToEdit.setImageName(Objects.isNull(productDTO.getImageName()) ? productToEdit.getImageName() : productDTO.getImageName());
            productToEdit.setDeleted(false);
            Product editedProduct = productRepository.saveAndFlush(modelMapper.map(productToEdit, Product.class));
            return modelMapper.map(editedProduct, ProductDTO.class);
        } else if (!Objects.isNull(productDTO) && !productToEdit.isDeleted()) {
            productToEdit.setName(Objects.isNull(productDTO.getName()) ? productToEdit.getName() : productDTO.getName());
            productToEdit.setCategory(Objects.isNull(productDTO.getCategory()) ? productToEdit.getCategory() : productDTO.getCategory());
            productToEdit.setDiscount(Objects.isNull(productDTO.getDiscount()) ? productToEdit.getDiscount() : productDTO.getDiscount());
            productToEdit.setPrice(Objects.isNull(productDTO.getPrice()) ? productToEdit.getPrice() : productDTO.getPrice());
            productToEdit.setStock(Objects.isNull(productDTO.getStock()) ? productToEdit.getStock() : productDTO.getStock());
            productToEdit.setImageName(Objects.isNull(productDTO.getImageName()) ? productToEdit.getImageName() : productDTO.getImageName());
            Product editedProduct = productRepository.saveAndFlush(modelMapper.map(productToEdit, Product.class));
            return modelMapper.map(editedProduct, ProductDTO.class);
        }
        return null;
    }

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, boolean isAdmin) {
        ProductDTO productInDB = findProductByName(productDTO.getName(), isAdmin);
        if (productInDB != null) {
            throw new MyException("405", "Product existed");
        }
        UUID id = UUID.randomUUID();
        productDTO.setId(id.toString());
        productDTO.setDeleted(false);
        Product savedProduct = productRepository.save(modelMapper.map(productDTO, Product.class));
        return modelMapper.map(savedProduct, ProductDTO.class);
    }
}