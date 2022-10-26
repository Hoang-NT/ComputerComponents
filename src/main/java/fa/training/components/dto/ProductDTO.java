package fa.training.components.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@RequiredArgsConstructor
public class ProductDTO {

    private String id;

    private String name;

    private String category;

    private Integer price;

    private Float discount;

    private Integer stock;

    private String createdBy;

    private String lastModifiedBy;

    private boolean isDeleted;

    private LocalDateTime createDate;

    private LocalDateTime lastModifyDate;

    private String imageName;

}
