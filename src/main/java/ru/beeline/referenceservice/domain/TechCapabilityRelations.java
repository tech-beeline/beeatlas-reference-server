package ru.beeline.referenceservice.domain;

import lombok.*;

import javax.persistence.*;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tech_capability_relations", schema = "capability")
public class TechCapabilityRelations {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tech_capability_relations_id_generator")
    @SequenceGenerator(name = "tech_capability_relations_id_generator", sequenceName = "TCR_id_seq", allocationSize = 1)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private BusinessCapability businessCapability;

    @ManyToOne
    @JoinColumn(name = "child_id")
    private TechCapability techCapability;
}