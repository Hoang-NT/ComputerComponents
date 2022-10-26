package fa.training.components.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
public class PromotionDTO {
    private String id;

    @NotBlank
    private String content;

    private Boolean active;

    private LocalDate createdDate;

    private LocalDate lastModifiedDate;

    private String createdBy;

    private String lastModifiedBy;

    @NotBlank
    private String productID;
}
