package fa.training.components.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "order_details")
public class OrderDetail {

    @Id
    private String id;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "total")
    private Integer total;

    @Column(name = "create_date")
    @CreatedDate
    private LocalDate createDate;

    @Column(name = "last_modify_date")
    @LastModifiedDate
    private LocalDate lastModifyDate;

    @Column(name = "last_modified_by")
    @LastModifiedBy
    private String lastModifiedBy;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

}
