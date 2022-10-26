package fa.training.components.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "imageName", unique = true)
        private String name;
        @Column(name = "type")
        private String type;
        @Lob
        @Column(name = "imageData",length = 1000)
        private byte[] imageData;

    }

