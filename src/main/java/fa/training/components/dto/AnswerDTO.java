package fa.training.components.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnswerDTO {
    private String id;

    @NotBlank
    private String content;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    private String questionID;

    private String createdBy;

    private String lastModifiedBy;

    private Boolean isDeleted;
}
