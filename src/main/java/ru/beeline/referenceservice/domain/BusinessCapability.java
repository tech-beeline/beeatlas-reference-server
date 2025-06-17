package ru.beeline.referenceservice.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "business_capability", schema = "capability")
public class BusinessCapability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;

    private String status;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private BusinessCapability parent;

    @Column(name = "is_domain")
    private Boolean isDomain;
}
