package ru.beeline.referenceservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.beeline.referenceservice.domain.BusinessCapability;
import ru.beeline.referenceservice.dto.BusinessCapabilityDTO;
import ru.beeline.referenceservice.dto.PutBusinessCapabilityDTO;
import ru.beeline.referenceservice.exception.ValidationException;
import ru.beeline.referenceservice.helper.pagination.OffsetBasedPageRequest;
import ru.beeline.referenceservice.mapper.BusinessCapabilityMapper;
import ru.beeline.referenceservice.repository.BusinessCapabilityRepository;
import ru.beeline.referenceservice.util.UrlWrapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class BusinessCapabilityService {

    private final BusinessCapabilityRepository businessCapabilityRepository;
    private final BusinessCapabilityMapper businessCapabilityMapper;

    public BusinessCapabilityService(BusinessCapabilityRepository businessCapabilityRepository,
                                     BusinessCapabilityMapper businessCapabilityMapper) {
        this.businessCapabilityRepository = businessCapabilityRepository;
        this.businessCapabilityMapper = businessCapabilityMapper;
    }

    public List<BusinessCapabilityDTO> getCapabilities(Integer limit, Integer offset, String findBy) {
        if (offset == null) {
            offset = 0;
        }
        Pageable pageable = new OffsetBasedPageRequest(offset, limit == null || limit == 0 ? Integer.MAX_VALUE : limit,
                Sort.by(Sort.Direction.ASC, "name"));
        Page<BusinessCapability> businessCapabilities;
        switch (findBy.toUpperCase()) {
            case "ALL":
                businessCapabilities = businessCapabilityRepository.findCapabilities(pageable);
                break;
            case "CORE":
                businessCapabilities = businessCapabilityRepository.findCapabilitiesWithoutParent(pageable);
                break;
            case "DOMAIN":
                businessCapabilities = businessCapabilityRepository.findByIsDomainTrueAndDeletedDateIsNull(pageable);
                break;
            default:
                throw new IllegalArgumentException("Unsupported FindBy value");
        }
        return businessCapabilityMapper.convertToBusinessCapabilityShortDTOList(businessCapabilities.toList(), findBy);
    }

    public void putCapability(PutBusinessCapabilityDTO capabilityDTO) {
        validateBusinessCapabilityDTO(capabilityDTO);
        Optional<BusinessCapability> businessCapabilityOptional = businessCapabilityRepository.findByCode(capabilityDTO.getCode());
        BusinessCapability businessCapability;
        if (businessCapabilityOptional.isPresent()) {
            businessCapability = businessCapabilityOptional.get();
            capabilityDTO.setDescription(UrlWrapper.proxyUrl(capabilityDTO.getDescription()));
            boolean shouldUpdate = !capabilityDTO.equals(businessCapabilityMapper.convertToPutCapabilityDTO(businessCapability)) ||
                    (capabilityDTO.equals(businessCapabilityMapper.convertToPutCapabilityDTO(businessCapability)) &&
                            businessCapability.getDeletedDate() != null);
            if (shouldUpdate) {
                log.info("update capability");
                log.info("businessCapability from BD : " + businessCapability);
                log.info("capabilityDTO from Dashboard: " + capabilityDTO + " PutCapability from bd: "
                        + businessCapabilityMapper.convertToPutCapabilityDTO(businessCapability).toString());
                updateCapability(businessCapability, capabilityDTO);
            }
        } else {
            log.info("create capability");
            createCapabilities(capabilityDTO);
        }
    }

    public void validateBusinessCapabilityDTO(PutBusinessCapabilityDTO capabilityDTO) {
        StringBuilder errMsg = new StringBuilder();
        if (capabilityDTO.getCode() == null || capabilityDTO.getCode().isEmpty()) {
            capabilityDTO.setCode(getPrefix(capabilityDTO) +
                    (businessCapabilityRepository.findFirstByOrderByIdDesc().getId() + 1));
        }
        if (!capabilityDTO.getIsDomain() && (capabilityDTO.getParent() == null || capabilityDTO.getParent().isEmpty())) {
            errMsg.append("Отсутствует обязательное поле parent\n");
        }
        if (capabilityDTO.getName() == null) {
            errMsg.append("Отсутствует обязательное поле name\n");
        }
        if (capabilityDTO.getCode().equals(capabilityDTO.getParent())) {
            errMsg.append("Возможность не может быть собственным родителем\n");
        }
        if (!errMsg.toString().isEmpty()) {
            throw new ValidationException(errMsg.toString());
        }
    }

    private String getPrefix(PutBusinessCapabilityDTO businessCapability) {
        String prefix;
        if (!businessCapability.getIsDomain()) {
            prefix = "BC.";
        } else {
            if (businessCapability.getParent() == null || businessCapability.getParent().isEmpty()) {
                prefix = "GRP.";
            } else {
                prefix = "DMN.";
            }
        }
        return prefix;
    }

    private BusinessCapability updateCapability(BusinessCapability businessCapability, PutBusinessCapabilityDTO capabilityDTO) {
        businessCapability.setName(capabilityDTO.getName());
        businessCapability.setDescription(UrlWrapper.proxyUrl(capabilityDTO.getDescription()));
        businessCapability.setStatus(capabilityDTO.getStatus());
        businessCapability.setLastModifiedDate(LocalDateTime.now());
        businessCapability.setDeletedDate(null);
        businessCapability.setParentId(getParentId(capabilityDTO));
        businessCapability.setIsDomain(capabilityDTO.getIsDomain());
        return businessCapabilityRepository.save(businessCapability);
    }

    private Integer getParentId(PutBusinessCapabilityDTO capability) {
        if (capability == null || capability.getParent() == null) {
            return null;
        }
        return businessCapabilityRepository.findByCode(capability.getParent()).map(BusinessCapability::getId).orElse(null);
    }

    private BusinessCapability createCapabilities(PutBusinessCapabilityDTO capability) {
        BusinessCapability result = businessCapabilityRepository.save(
                BusinessCapability.builder()
                        .code(capability.getCode())
                        .name(capability.getName())
                        .description(UrlWrapper.proxyUrl(capability.getDescription()))
                        .status(capability.getStatus())
                        .createdDate(LocalDateTime.now())
                        .parentId(getParentId(capability))
                        .isDomain(capability.getIsDomain())
                        .build());
        return result;
    }
}
