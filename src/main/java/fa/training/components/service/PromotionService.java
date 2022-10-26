package fa.training.components.service;

import fa.training.components.dto.PromotionDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PromotionService {
    /**
     * Find promotion from database by ID
     * @param id of promotion
     * @param isAdmin is current logged in customer an admin
     * @return promotion found or null
     */
    PromotionDTO findByID(String id, boolean isAdmin);

    /**
     * Find promotion from database by product ID
     * @param productID ID of product that promotion belongs
     * @param isAdmin is current logged in customer an admin
     * @param pageable a Pageable object
     * @return list of promotion found or empty list
     */
    List<PromotionDTO> findByProduct(String productID, boolean isAdmin, Pageable pageable);

    /**
     * Find all promotion from database
     * @param isAdmin is current logged in customer an admin
     * @param pageable a Pageable object
     * @return list of promotion found or empty list
     */
    List<PromotionDTO> findAll(boolean isAdmin, Pageable pageable);

    /**
     * Create a promotion and save to database
     * @param promotionDTO promotion to create
     * @return saved promotion
     */
    PromotionDTO createPromotion(PromotionDTO promotionDTO);

    /**
     * Edit a promotion in database
     * @param promotionDTO promotion with update field
     * @return edited promotion
     */
    PromotionDTO editPromotion(PromotionDTO promotionDTO);
}
