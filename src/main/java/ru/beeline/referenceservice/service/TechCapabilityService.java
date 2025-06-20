package ru.beeline.referenceservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.beeline.referenceservice.domain.TechCapability;
import ru.beeline.referenceservice.dto.TechCapabilityDTO;
import ru.beeline.referenceservice.helper.pagination.OffsetBasedPageRequest;
import ru.beeline.referenceservice.mapper.TechCapabilityMapper;
import ru.beeline.referenceservice.repository.TechCapabilityRepository;

import java.util.List;

@Service
public class TechCapabilityService {

    private final TechCapabilityRepository techCapabilityRepository;

    private final TechCapabilityMapper techCapabilityMapper;

    public TechCapabilityService(TechCapabilityRepository techCapabilityRepository, TechCapabilityMapper techCapabilityMapper) {
        this.techCapabilityRepository = techCapabilityRepository;
        this.techCapabilityMapper = techCapabilityMapper;
    }

    public List<TechCapabilityDTO> getCapabilities(Integer limit, Integer offset) {
        if (offset == null) {
            offset = 0;
        }
        Pageable pageable = new OffsetBasedPageRequest(offset, limit == null ||
                limit == 0 ? Integer.MAX_VALUE : limit, Sort.by(Sort.Direction.ASC, "name"));
        Page<TechCapability> techCapabilities = techCapabilityRepository.findCapabilities(pageable);
        return techCapabilityMapper.convert(techCapabilities.toList());
    }

}
