package ru.beeline.referenceservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "business_capability", schema = "capability")
public class BusinessCapability {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "business_capability_id_generator")
    @SequenceGenerator(name = "business_capability_id_generator", sequenceName = "BC_id_seq", allocationSize = 1)
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

    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "is_domain")
    private Boolean isDomain;

    @ManyToOne
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private BusinessCapability parentEntity;

    @OneToMany
    @JoinColumn(name = "id_parent")
    private List<TechCapabilityRelations> children;
}
