package ru.beeline.referenceservice.mapper;

import org.springframework.stereotype.Component;
import ru.beeline.referenceservice.domain.BusinessCapability;
import ru.beeline.referenceservice.dto.BusinessCapabilityDTO;
import ru.beeline.referenceservice.repository.BusinessCapabilityRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BusinessCapabilityMapper {

    private final BusinessCapabilityRepository businessCapabilityRepository;

    public BusinessCapabilityMapper(BusinessCapabilityRepository businessCapabilityRepository) {
        this.businessCapabilityRepository = businessCapabilityRepository;
    }

    public List<BusinessCapabilityDTO> convertToBusinessCapabilityShortDTOList(
            List<BusinessCapability> businessCapabilities, String findBy) {
        List<Long> parentIds;
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

        return businessCapabilities.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    public BusinessCapabilityDTO convert(BusinessCapability businessCapability) {
        return BusinessCapabilityDTO.builder()
                .id(businessCapability.getId())
                .code(businessCapability.getCode())
                .name(businessCapability.getName())
                .description(businessCapability.getDescription())
                .createdDate(businessCapability.getCreatedDate())
                .lastModifiedDate(businessCapability.getLastModifiedDate())
                .deletedDate(businessCapability.getDeletedDate())
                .isDomain(businessCapability.isDomain())
                .parent(BCParentDTO.convert(businessCapability.getParentEntity()))
                .build();
    }
}
