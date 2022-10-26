package fa.training.components.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class ReviewDTO {

    private String id;

    @NotBlank
    private String content;

    @Range(min = 0, max = 5)
    private Integer rating;

    private String productId;

    private String createdBy;

    private String lastModifiedBy;

    private boolean isDeleted;

    private LocalDateTime createDate;

    private LocalDateTime lastModifyDate;
}