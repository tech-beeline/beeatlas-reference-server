package ru.beeline.referenceservice.mapper;

import org.springframework.stereotype.Component;
import ru.beeline.referenceservice.domain.Product;
import ru.beeline.referenceservice.domain.TechCapability;
import ru.beeline.referenceservice.domain.TechCapabilityRelations;
import ru.beeline.referenceservice.dto.PutTechCapabilityDTO;
import ru.beeline.referenceservice.dto.TechCapabilityDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TechCapabilityMapper {

    private final BusinessCapabilityMapper businessCapabilityMapper;

    public TechCapabilityMapper(BusinessCapabilityMapper businessCapabilityMapper) {
        this.businessCapabilityMapper = businessCapabilityMapper;
    }

    public List<TechCapabilityDTO> convert(List<TechCapability> techCapabilities) {
        List<TechCapabilityDTO> techCapabilityDTOS = new ArrayList<>();
        for (TechCapability techCapability : techCapabilities) {
            TechCapabilityDTO techCapabilityDTO = convert(techCapability);
            techCapabilityDTOS.add(techCapabilityDTO);
        }
        return techCapabilityDTOS;
    }

    public TechCapabilityDTO convert(TechCapability techCapability) {
        if (Objects.isNull(techCapability)) {
            return null;
        }
        Product product = techCapability.getResponsibilityProduct();
        return TechCapabilityDTO.builder()
                .id(techCapability.getId())
                .code(techCapability.getCode())
                .name(techCapability.getName())
                .description(techCapability.getDescription())
                .createdDate(techCapability.getCreatedDate())
                .lastModifiedDate(techCapability.getLastModifiedDate())
                .deletedDate(techCapability.getDeletedDate())
                .parents(businessCapabilityMapper.convertToBCParentDTOList(techCapability.getParents()))
                .systemId(Optional.ofNullable(product).map(Product::getId).orElse(null))
                .build();
    }

    public PutTechCapabilityDTO convertToPutTechCapabilityDTO(TechCapability techCapability) {
        Product product = techCapability.getResponsibilityProduct();
        String productAlias = product != null ? product.getAlias() : null;
        return PutTechCapabilityDTO.builder()
                .code(techCapability.getCode())
                .name(techCapability.getName())
                .description(techCapability.getDescription())
                .status(techCapability.getStatus())
                .parents(getParentsCodes(techCapability.getParents()))
                .targetSystemCode(productAlias)
                .build();
    }

    public List<String> getParentsCodes(List<TechCapabilityRelations> techCapabilitiesRelations) {
        return techCapabilitiesRelations.stream()
                .map(relation -> relation.getBusinessCapability().getCode())
                .collect(Collectors.toList());
    }
}
