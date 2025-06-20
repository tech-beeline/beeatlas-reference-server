package ru.beeline.referenceservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @SequenceGenerator(name = "tech_capability_relations_id_generator", sequenceName = "capability.TCR_id_seq", allocationSize = 1)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private BusinessCapability businessCapability;

    @ManyToOne
    @JoinColumn(name = "child_id")
    private TechCapability techCapability;
}