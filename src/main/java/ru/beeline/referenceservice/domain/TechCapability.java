package ru.beeline.referenceservice.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@ToString(exclude = "businessCapability")
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tech_capability", schema = "capability")
public class TechCapability {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tech_capability_id_generator")
    @SequenceGenerator(name = "tech_capability_id_generator", sequenceName = "capability.tech_capability_id_seq", allocationSize = 1)
    private Integer id;

    private String code;

    private String name;

    private String description;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;

    private String status;

    @OneToMany
    @JoinColumn(name = "child_id")
    private List<TechCapabilityRelations> parents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsibility_product_id")
    private Product responsibilityProduct;
}
