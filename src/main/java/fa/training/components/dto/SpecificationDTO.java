package fa.training.components.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpecificationDTO {
    private String productID;
    @NotEmpty
    private Map<String, String> productInfo;
    private Map<String, String> designAndWeight;
    @NotEmpty
    private Map<String, String> details;
    private Map<String, String> accessoryInBox;
    private LocalDate createdDate;
    private LocalDate lastModifiedDate;
    private String createdBy;
    private String lastModifiedBy;
    private Boolean isDeleted;
}
