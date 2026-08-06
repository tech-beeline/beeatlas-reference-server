/*
 * Copyright (c) 2024 PJSC VimpelCom
 */
package ru.beeline.referenceservice.mapper;

import org.springframework.stereotype.Component;
import ru.beeline.referenceservice.domain.BusinessCapability;
import ru.beeline.referenceservice.domain.TechCapabilityRelations;
import ru.beeline.referenceservice.dto.BCParentDTO;
import ru.beeline.referenceservice.dto.BusinessCapabilityDTO;
import ru.beeline.referenceservice.dto.PutBusinessCapabilityDTO;
import ru.beeline.referenceservice.repository.BusinessCapabilityRepository;
import ru.beeline.referenceservice.repository.TechCapabilityRelationsRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BusinessCapabilityMapper {

    private final BusinessCapabilityRepository businessCapabilityRepository;

    private final TechCapabilityRelationsRepository techCapabilityRelationsRepository;

    public BusinessCapabilityMapper(BusinessCapabilityRepository businessCapabilityRepository,
                                    TechCapabilityRelationsRepository techCapabilityRelationsRepository) {
        this.businessCapabilityRepository = businessCapabilityRepository;
        this.techCapabilityRelationsRepository = techCapabilityRelationsRepository;
    }

    public List<BusinessCapabilityDTO> convertToBusinessCapabilityShortDTOList(
            List<BusinessCapability> businessCapabilities, String findBy) {
        List<Integer> parentIds;
        if ("CORE".equals(findBy)) {
            parentIds = businessCapabilities.stream()
                    .filter(bc -> bc.getParentId() == null)
                    .map(BusinessCapability::getId)
                    .collect(Collectors.toList());
        } else {
            parentIds = businessCapabilities.stream()
                    .map(BusinessCapability::getId)
                    .collect(Collectors.toList());
        }
        List<Integer> activeBcIds = findActiveBusinessCapabilityIds(parentIds);
        List<Integer> activeTcIds = findActiveTechCapabilities(parentIds);
        return businessCapabilities.stream()
                .map(bc -> {
                    boolean hasActiveChildren = activeBcIds.contains(bc.getId()) || activeTcIds.contains(bc.getId());
                    return convert(bc, hasActiveChildren);
                })
                .collect(Collectors.toList());
    }

    private List<Integer> findActiveBusinessCapabilityIds(List<Integer> parentIds) {
        if (parentIds.isEmpty()) {
            return Collections.emptyList();
        }
        return businessCapabilityRepository.findActiveBusinessCapabilities(parentIds);
    }

    private List<Integer> findActiveTechCapabilities(List<Integer> businessCapabilityIds) {
        if (businessCapabilityIds.isEmpty()) {
            return Collections.emptyList();
        }
        return techCapabilityRelationsRepository.findActiveTechCapabilities(businessCapabilityIds);
    }

    public BusinessCapabilityDTO convert(BusinessCapability businessCapability, boolean hasKids) {
        return BusinessCapabilityDTO.builder()
                .id(businessCapability.getId())
                .code(businessCapability.getCode())
                .name(businessCapability.getName())
                .description(businessCapability.getDescription())
                .createdDate(businessCapability.getCreatedDate())
                .lastModifiedDate(businessCapability.getLastModifiedDate())
                .deletedDate(businessCapability.getDeletedDate())
                .isDomain(businessCapability.getIsDomain())
                .hasChildren(hasKids)
                .parent(convertToBCParentDTO(businessCapability.getParentEntity()))
                .build();
    }

    public List<BCParentDTO> convertToBCParentDTOList(List<TechCapabilityRelations> relations) {
        List<BCParentDTO> parents = new ArrayList<>();
        for (TechCapabilityRelations relation : relations) {
            BusinessCapability bc = relation.getBusinessCapability();
            BCParentDTO dto = convertToBCParentDTO(bc);
            parents.add(dto);
        }
        parents.sort(Comparator.comparing(BCParentDTO::getName));
        return parents;
    }

    public BCParentDTO convertToBCParentDTO(BusinessCapability businessCapability) {
        if (businessCapability == null) {
            return null;
        }
        return BCParentDTO.builder()
                .id(businessCapability.getId())
                .code(businessCapability.getCode())
                .name(businessCapability.getName())
                .description(businessCapability.getDescription())
                .status(businessCapability.getStatus())
                .createdDate(businessCapability.getCreatedDate())
                .lastModifiedDate(businessCapability.getLastModifiedDate())
                .isDomain(businessCapability.getIsDomain())
                .hasChildren(true)
                .build();
    }

    public PutBusinessCapabilityDTO convertToPutCapabilityDTO(BusinessCapability businessCapability) {
        return PutBusinessCapabilityDTO.builder()
                .code(businessCapability.getCode())
                .name(businessCapability.getName())
                .description(businessCapability.getDescription())
                .status(businessCapability.getStatus())
                .isDomain(businessCapability.getIsDomain())
                .parent(getParentCode(businessCapability))
                .build();
    }

    private String getParentCode(BusinessCapability capability) {
        if (capability == null || capability.getParentId() == null)
            return null;
        return businessCapabilityRepository.findById(capability.getParentId())
                .map(BusinessCapability::getCode)
                .orElse(null);
    }
}
