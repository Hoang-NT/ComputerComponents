package fa.training.components.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class OrderDTO {

    private String id;

    private LocalDateTime createDate;

    private LocalDateTime lastModifyDate;

    private String createdBy;

    private String lastModifiedBy;

    private String status;

    private boolean isDeleted;

}