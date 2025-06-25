package ru.beeline.referenceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.beeline.referenceservice.domain.BusinessCapability;
import ru.beeline.referenceservice.domain.TechCapability;
import ru.beeline.referenceservice.domain.TechCapabilityRelations;

import java.util.List;

@Repository
public interface TechCapabilityRelationsRepository extends JpaRepository<TechCapabilityRelations, Integer> {

    @Query("SELECT DISTINCT tcr.businessCapability.id FROM TechCapabilityRelations tcr " +
            "WHERE tcr.techCapability.deletedDate IS NULL " +
            "AND tcr.businessCapability.deletedDate IS NULL " +
            "AND tcr.businessCapability.id IN :businessCapabilityIds")
    List<Integer> findActiveTechCapabilities(@Param("businessCapabilityIds") List<Integer> businessCapabilityIds);

    void deleteAllByTechCapability(TechCapability techCapability);

    Boolean existsByBusinessCapabilityAndTechCapability(BusinessCapability businessCapability, TechCapability techCapability);
}
