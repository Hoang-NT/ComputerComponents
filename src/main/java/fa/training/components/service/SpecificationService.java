package fa.training.components.service;

import fa.training.components.dto.SpecificationDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SpecificationService {
    /**
     * Find specification of a product in database
     * @param productID ID of product
     * @param isAdmin is current logged in customer an admin
     * @return specification found or throw not found exception
     */
    SpecificationDTO findByProductID(String productID, boolean isAdmin);

    /**
     * Find all specifications in database
     * @param isAdmin is current logged in customer an admin
     * @param pageable a Pageable object
     * @return list of specification found or empty list
     */
    List<SpecificationDTO> findAll(boolean isAdmin, Pageable pageable);

    /**
     * Create specification and save to database
     * @param specificationDTO specification to create
     * @return specification saved to database
     */
    SpecificationDTO createSpecification(SpecificationDTO specificationDTO);

    /**
     * Edit specification in database
     * @param specificationDTO specification with update field
     * @return edited specification
     */
    SpecificationDTO editSpecification(SpecificationDTO specificationDTO);
}
