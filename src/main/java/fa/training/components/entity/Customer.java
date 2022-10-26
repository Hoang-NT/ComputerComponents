package fa.training.components.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Customer {
    @Id
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

    @Column(name = "created_date")
    @CreatedDate
    private LocalDateTime createDate;

    @Column(name = "last_modified_date")
    @LastModifiedDate
    private LocalDateTime modifyDate;

    @Column(name = "last_modified_by")
    @LastModifiedBy
    private String lastModifiedBy;

    private Boolean enable;

    public void hideSensitiveInfo() {
        this.role = null;
        this.enable = null;
    }
}
