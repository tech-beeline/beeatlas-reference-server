package ru.beeline.referenceservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.beeline.referenceservice.domain.BusinessCapability;
import ru.beeline.referenceservice.dto.BusinessCapabilityDTO;
import ru.beeline.referenceservice.helper.pagination.OffsetBasedPageRequest;
import ru.beeline.referenceservice.mapper.BusinessCapabilityMapper;
import ru.beeline.referenceservice.repository.BusinessCapabilityRepository;

import java.util.List;

@Service
public class BusinessCapabilityService {

    private final BusinessCapabilityRepository businessCapabilityRepository;
    private final BusinessCapabilityMapper businessCapabilityMapper;

    public BusinessCapabilityService(BusinessCapabilityRepository businessCapabilityRepository,
                                     BusinessCapabilityMapper businessCapabilityMapper
    ) {
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
}
