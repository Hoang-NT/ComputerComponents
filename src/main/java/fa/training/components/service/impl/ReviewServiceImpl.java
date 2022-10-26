package fa.training.components.service.impl;

import fa.training.components.dto.ProductDTO;
import fa.training.components.dto.ReviewDTO;
import fa.training.components.entity.Review;
import fa.training.components.exception.MyException;
import fa.training.components.repository.ReviewRepository;
import fa.training.components.security.AuditorAwareImpl;
import fa.training.components.service.ProductService;
import fa.training.components.service.ReviewService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuditorAwareImpl auditorProvider;

    @Override
    public ReviewDTO findReviewById(String id, boolean isAdminAndStaff) {
        Optional<Review> review;
        if (isAdminAndStaff) {
            review = reviewRepository.findById(id);
        } else {
            review = reviewRepository.findByIdAndIsDeleted(id, false);
        }
        return review.map(value -> modelMapper.map(value, ReviewDTO.class)).orElse(null);
    }
    @Override
    public List<ReviewDTO> findReviewByRating(Integer rating, boolean isAdminAndStaff, Pageable pageable) {
        List<Review> reviews;
        if (isAdminAndStaff) {
            reviews = reviewRepository.findByRating(rating, pageable);
        } else {
            reviews = reviewRepository.findByRatingAndIsDeleted(rating, false, pageable);
        }
        return reviews.stream().map(review -> modelMapper.map(review, ReviewDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<ReviewDTO> findReviewByCreatedBy(String createdBy, boolean isAdminAndStaff, Pageable pageable) {
        List<Review> reviews;
        if (isAdminAndStaff) {
            reviews = reviewRepository.findByCreatedBy(createdBy, pageable);
        } else {
            reviews = reviewRepository.findByCreatedByAndIsDeleted(createdBy, false, pageable);
        }
        return reviews.stream().map(review -> modelMapper.map(review, ReviewDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<ReviewDTO> findReviewByProductName(String productName, boolean isAdminAndStaff, Pageable pageable) {
        List<Review> reviews;
        ProductDTO productDTO;
        if (isAdminAndStaff) {
            productDTO = productService.findProductByName(productName, true);
            reviews = reviewRepository.findByProductId(productDTO.getId(), pageable);
        } else {
            productDTO = productService.findProductByName(productName, false);
            reviews = reviewRepository.findByProductIdAndIsDeleted(productDTO.getId(), false, pageable);
        }
        return reviews.stream().map(review -> modelMapper.map(review, ReviewDTO.class)).collect(Collectors.toList());
    }

    @Override
    public ReviewDTO deleteReviewById(String id, boolean isAdmin) {
        ReviewDTO reviewToDelete = findReviewById(id, isAdmin);
        String owner = auditorProvider.getCurrentAuditor().get();
        if (isAdmin || owner.equals(reviewToDelete.getCreatedBy())) {
            reviewToDelete.setDeleted(true);
            Review deletedReview = reviewRepository.saveAndFlush(modelMapper.map(reviewToDelete, Review.class));
            return modelMapper.map(deletedReview, ReviewDTO.class);
        }
        throw new MyException("400", "Review not found");
    }

    @Override
    public ReviewDTO editReview(String id, ReviewDTO reviewDTO, boolean isAdmin) {
        ReviewDTO reviewToEdit = findReviewById(id, isAdmin);
        String owner = auditorProvider.toString();
        String createdBy = reviewToEdit.getCreatedBy();
        if (!Objects.isNull(reviewDTO) && reviewToEdit.isDeleted()){
            reviewToEdit.setContent(Objects.isNull(reviewDTO.getContent()) ? reviewToEdit.getContent() : reviewDTO.getContent());
            reviewToEdit.setRating(Objects.isNull(reviewDTO.getRating()) ? reviewToEdit.getRating() : reviewDTO.getRating());
            reviewToEdit.setDeleted(false);
            Review editedReview = reviewRepository.saveAndFlush(modelMapper.map(reviewToEdit, Review.class));
            return modelMapper.map(editedReview, ReviewDTO.class);
        } else if (!Objects.isNull(reviewDTO) && !reviewToEdit.isDeleted() && !isAdmin && !owner.equals(createdBy)) {
            throw new MyException("400","Review not found");
        }
        reviewToEdit.setContent(Objects.isNull(reviewDTO.getContent()) ? reviewToEdit.getContent() : reviewDTO.getContent());
        reviewToEdit.setRating(Objects.isNull(reviewDTO.getRating()) ? reviewToEdit.getRating() : reviewDTO.getRating());
        Review editedReview = reviewRepository.saveAndFlush(modelMapper.map(reviewToEdit, Review.class));
        return modelMapper.map(editedReview, ReviewDTO.class);
    }

    @Override
    public ReviewDTO addReview(ReviewDTO reviewDTO) {
        ProductDTO productDTO = productService.findProductById(reviewDTO.getProductId(), false);
        if (productDTO == null) {
            throw new MyException("400", "Product not found");
        }
        UUID id = UUID.randomUUID();
        reviewDTO.setId(id.toString());
        reviewDTO.setDeleted(false);
        Review savedReview = reviewRepository.save(modelMapper.map(reviewDTO, Review.class));
        return modelMapper.map(savedReview, ReviewDTO.class);
    }
}