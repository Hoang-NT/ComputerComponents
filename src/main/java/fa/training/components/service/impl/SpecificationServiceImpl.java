package fa.training.components.service.impl;

import fa.training.components.dto.SpecificationDTO;
import fa.training.components.entity.Product;
import fa.training.components.entity.Specification;
import fa.training.components.exception.MyException;
import fa.training.components.repository.ProductRepository;
import fa.training.components.repository.SpecificationRepository;
import fa.training.components.service.SpecificationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SpecificationServiceImpl implements SpecificationService {
    @Autowired
    private SpecificationRepository specificationRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public SpecificationDTO findByProductID(String productID, boolean isAdmin) {
        Optional<Specification> specification;
        if (isAdmin) {
            specification = specificationRepository.findById(productID);
        } else {
            specification = specificationRepository.findByProductIDAndIsDeleted(productID, false);
        }
        if (!specification.isPresent()) {
            throw new MyException("400", "Specification not found");
        }
        Specification specificationFound = specification.get();
        if (!isAdmin) {
            specificationFound.setIsDeleted(null);
        }
        return modelMapper.map(specificationFound, SpecificationDTO.class);
    }

    @Override
    public List<SpecificationDTO> findAll(boolean isAdmin, Pageable pageable) {
        Page<Specification> specifications;
        if (isAdmin) {
            specifications = specificationRepository.findAll(pageable);
        } else {
            specifications = specificationRepository.findByIsDeleted(false, pageable);
        }
        return specifications.stream().map(specification -> {
            SpecificationDTO specificationDTO = modelMapper.map(specification, SpecificationDTO.class);
            if (!isAdmin) {
                specificationDTO.setIsDeleted(null);
            }
            return specificationDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public SpecificationDTO createSpecification(SpecificationDTO specificationDTO) {
        // Check product
        Optional<Product> product = productRepository.findByIdAndIsDeleted(specificationDTO.getProductID(), false);
        if (!product.isPresent()) {
            throw new MyException("400", "Product not found");
        }

        // Check if specification existed
        Optional<Specification> specification = specificationRepository.findById(specificationDTO.getProductID());
        if (specification.isPresent()) {
            throw new MyException("405", "Specification existed for this product");
        }

        specificationDTO.setIsDeleted(false);

        Specification savedSpecification = specificationRepository.save(modelMapper.map(specificationDTO, Specification.class));
        savedSpecification.setIsDeleted(null);
        return modelMapper.map(savedSpecification, SpecificationDTO.class);
    }

    @Override
    public SpecificationDTO editSpecification(SpecificationDTO specificationDTO) {
        // Check specification
        Optional<Specification> specification = specificationRepository.findByProductIDAndIsDeleted(specificationDTO.getProductID(), false);
        if (!specification.isPresent()) {
            throw new MyException("400", "Specification not found");
        }
        Specification specificationToEdit = specification.get();

        // Check update fields, check empty for field productInfo & details if they are updating
        if (specificationDTO.getProductInfo() != null) {
            if (specificationDTO.getProductInfo().isEmpty()) {
                throw new MyException("406", "Field error in object 'specificationDTO' on field 'productInfo': must not be empty");
            }
            specificationToEdit.setProductInfo(specificationDTO.getProductInfo());
        }
        if (specificationDTO.getDetails() != null) {
            if (specificationDTO.getDetails().isEmpty()) {
                throw new MyException("406", "Field error in object 'specificationDTO' on field 'details': must not be empty");
            }
            specificationToEdit.setDetails(specificationDTO.getDetails());
        }
        if (specificationDTO.getDesignAndWeight() != null) {
            specificationToEdit.setDesignAndWeight(specificationDTO.getDesignAndWeight());
        }
        if (specificationDTO.getAccessoryInBox() != null) {
            specificationToEdit.setAccessoryInBox(specificationDTO.getAccessoryInBox());
        }

        Specification editedSpecification = specificationRepository.saveAndFlush(specificationToEdit);
        editedSpecification.setIsDeleted(null);
        return modelMapper.map(editedSpecification, SpecificationDTO.class);
    }
}
