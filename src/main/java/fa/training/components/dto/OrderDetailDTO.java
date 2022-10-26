package fa.training.components.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDetailDTO {
//
//    @JsonIgnore
    private String id;

    private String orderId;

    private String productId;

    private Integer amount;

    private Integer total;

    private LocalDate createDate;

    private LocalDate lastModifyDate;

    private String lastModifiedBy;

    private Boolean isDeleted;

}
