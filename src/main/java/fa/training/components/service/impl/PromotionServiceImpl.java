package fa.training.components.service.impl;

import fa.training.components.dto.PromotionDTO;
import fa.training.components.entity.Product;
import fa.training.components.entity.Promotion;
import fa.training.components.exception.MyException;
import fa.training.components.repository.ProductRepository;
import fa.training.components.repository.PromotionRepository;
import fa.training.components.service.PromotionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PromotionServiceImpl implements PromotionService {
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public PromotionDTO findByID(String id, boolean isAdmin) {
        Optional<Promotion> promotion;
        if (isAdmin) {
            promotion = promotionRepository.findById(id);
        } else {
            promotion = promotionRepository.findByIdAndActive(id, true);
        }
        if (!promotion.isPresent()) {
            throw new MyException("400", "Promotion not found");
        }
        return modelMapper.map(promotion.get(), PromotionDTO.class);
    }

    @Override
    public List<PromotionDTO> findByProduct(String productID, boolean isAdmin, Pageable pageable) {
        List<Promotion> promotions;
        if (isAdmin) {
            promotions = promotionRepository.findByProductID(productID, pageable);
        } else {
            promotions = promotionRepository.findByProductIDAndActive(productID, true, pageable);
        }
        return promotions.stream().map(promotion -> modelMapper.map(promotion, PromotionDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<PromotionDTO> findAll(boolean isAdmin, Pageable pageable) {
        Page<Promotion> promotions;
        if (isAdmin) {
            promotions = promotionRepository.findAll(pageable);
        } else {
            promotions = promotionRepository.findByActive(true, pageable);
        }
        return promotions.stream().map(promotion -> modelMapper.map(promotion, PromotionDTO.class)).collect(Collectors.toList());
    }

    @Override
    public PromotionDTO createPromotion(PromotionDTO promotionDTO) {
        // Check product ID
        Optional<Product> product = productRepository.findByIdAndIsDeleted(promotionDTO.getProductID(), false);
        if (!product.isPresent()) {
            throw new MyException("400", "Product not found");
        }

        // Check if promotion existed for the product
        Promotion promotionInDB = promotionRepository.findByProductIDAndContent(promotionDTO.getProductID(), promotionDTO.getContent());
        if (promotionInDB != null) {
            throw new MyException("405", "Promotion existed for this product");
        }

        UUID id = UUID.randomUUID();
        promotionDTO.setId(id.toString());

        // Default set field active value true
        if (promotionDTO.getActive() == null) {
            promotionDTO.setActive(true);
        }

        Promotion savedPromotion = promotionRepository.save(modelMapper.map(promotionDTO, Promotion.class));
        return modelMapper.map(savedPromotion, PromotionDTO.class);
    }

    @Override
    public PromotionDTO editPromotion(PromotionDTO promotionDTO) {
        // Get promotion in database
        Optional<Promotion> promotion = promotionRepository.findById(promotionDTO.getId());
        if (!promotion.isPresent()) {
            throw new MyException("400", "Promotion not found");
        }
        Promotion promotionToEdit = promotion.get();

        // Set update
        if (promotionDTO.getContent() != null) {
            if (promotionDTO.getContent().trim().length() == 0) {
                throw new MyException("406", "Field error in object 'promotionDTO' on field 'content': must not be blank");
            }
            promotionToEdit.setContent(promotionDTO.getContent());
        }
        if (promotionDTO.getActive() != null) {
            promotionToEdit.setActive(promotionDTO.getActive());
        }

        Promotion editedPromotion = promotionRepository.saveAndFlush(promotionToEdit);
        return modelMapper.map(editedPromotion, PromotionDTO.class);
    }
}
