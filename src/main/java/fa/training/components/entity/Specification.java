package fa.training.components.entity;

import fa.training.components.utils.HashMapConverter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Specification {
    @Id
    private String productID;

    @Column(name = "product_info")
    @NotEmpty
    @Convert(converter = HashMapConverter.class)
    private Map<String, String> productInfo;

    @Column(name = "design_and_weight")
    @Convert(converter = HashMapConverter.class)
    private Map<String, String> designAndWeight;

    @NotEmpty
    @Convert(converter = HashMapConverter.class)
    private Map<String, String> details;

    @Column(name = "accessory_in_box")
    @Convert(converter = HashMapConverter.class)
    private Map<String, String> accessoryInBox;

    @Column(name = "created_date")
    @CreatedDate
    private LocalDate createdDate;

    @Column(name = "last_modified_date")
    @LastModifiedDate
    private LocalDate lastModifiedDate;

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;

    @Column(name = "last_modified_by")
    @LastModifiedBy
    private String lastModifiedBy;

    @Column(name = "is_deleted")
    private Boolean isDeleted;
}
