package fa.training.components.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDTO {
    @JsonIgnore
    private String id;

    @NotBlank
    @Length(min = 3, max = 50)
    private String username;

    @NotBlank
    @Length(min = 6, max = 60)
    private String password;

    @NotBlank
    private String fullName;

    @NotBlank
    private String phone;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String address;

    private String role;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    private String lastModifiedBy;

    private Boolean enable;
}
