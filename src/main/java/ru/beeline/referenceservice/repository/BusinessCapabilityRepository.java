package ru.beeline.referenceservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.beeline.referenceservice.domain.BusinessCapability;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessCapabilityRepository extends JpaRepository<BusinessCapability, Integer> {

    @Query(
            value = "SELECT c FROM BusinessCapability c LEFT JOIN FETCH c.parentEntity p" +
                    " WHERE c.deletedDate IS NULL AND (p.deletedDate IS NULL OR p IS NULL) ORDER BY c.name",
            countQuery = "SELECT count(c) FROM BusinessCapability c LEFT JOIN c.parentEntity p " +
                    "WHERE c.deletedDate IS NULL AND (p.deletedDate IS NULL OR p IS NULL)"
    )
    Page<BusinessCapability> findCapabilities(Pageable pageable);

    @Query("SELECT c FROM BusinessCapability c WHERE c.deletedDate is NULL " +
            "and c.parentId is null and c.isDomain is true ORDER BY c.name")
    Page<BusinessCapability> findCapabilitiesWithoutParent(Pageable pageable);

    @Query("SELECT bc.parentId FROM BusinessCapability bc " +
            "WHERE bc.parentId IN :parentIds " +
            "AND bc.deletedDate IS NULL")
    List<Integer> findActiveBusinessCapabilities(@Param("parentIds") List<Integer> parentIds);

    Page<BusinessCapability> findByIsDomainTrueAndDeletedDateIsNull(Pageable pageable);

    Optional<BusinessCapability> findByCode(String code);

    BusinessCapability findFirstByOrderByIdDesc();

    List<BusinessCapability> findAllByCodeIn(List<String> codes);
}
