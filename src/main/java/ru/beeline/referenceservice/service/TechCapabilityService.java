package ru.beeline.referenceservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.beeline.referenceservice.domain.BusinessCapability;
import ru.beeline.referenceservice.domain.Product;
import ru.beeline.referenceservice.domain.TechCapability;
import ru.beeline.referenceservice.domain.TechCapabilityRelations;
import ru.beeline.referenceservice.dto.PutTechCapabilityDTO;
import ru.beeline.referenceservice.dto.TechCapabilityDTO;
import ru.beeline.referenceservice.exception.ValidationException;
import ru.beeline.referenceservice.helper.pagination.OffsetBasedPageRequest;
import ru.beeline.referenceservice.mapper.TechCapabilityMapper;
import ru.beeline.referenceservice.repository.BusinessCapabilityRepository;
import ru.beeline.referenceservice.repository.ProductRepository;
import ru.beeline.referenceservice.repository.TechCapabilityRelationsRepository;
import ru.beeline.referenceservice.repository.TechCapabilityRepository;
import ru.beeline.referenceservice.util.UrlWrapper;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@Transactional
public class TechCapabilityService {

    private final TechCapabilityRepository techCapabilityRepository;

    private final ProductRepository productRepository;

    private final TechCapabilityMapper techCapabilityMapper;

    private final BusinessCapabilityRepository businessCapabilityRepository;

    private final TechCapabilityRelationsRepository techCapabilityRelationsRepository;

    public TechCapabilityService(TechCapabilityRepository techCapabilityRepository, ProductRepository productRepository,
                                 TechCapabilityMapper techCapabilityMapper, BusinessCapabilityRepository businessCapabilityRepository,
                                 TechCapabilityRelationsRepository techCapabilityRelationsRepository) {
        this.techCapabilityRepository = techCapabilityRepository;
        this.productRepository = productRepository;
        this.techCapabilityMapper = techCapabilityMapper;
        this.businessCapabilityRepository = businessCapabilityRepository;
        this.techCapabilityRelationsRepository = techCapabilityRelationsRepository;
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

    public void createOrUpdate(PutTechCapabilityDTO techCapability) {
        validateTechCapabilityDTO(techCapability);
        Product product = null;
        Integer productId = null;
        if (techCapability.getTargetSystemCode() != null && !techCapability.getTargetSystemCode().isEmpty()) {
            product = productRepository.findByAliasCaseInsensitive(techCapability.getTargetSystemCode());
            if (product != null) {
                productId = product.getId();
            }else {
                techCapability.setTargetSystemCode(null);
            }
        }
        Optional<TechCapability> currentTechCapabilityOpt = techCapabilityRepository.findByCode(techCapability.getCode());
        boolean hasParents = techCapability.getParents() != null && !techCapability.getParents().isEmpty();
        log.info("techCapabilityHaveParents: {}", hasParents);
        if (currentTechCapabilityOpt.isEmpty()) {
            log.info("Creating new TechCapability");
            TechCapability newTechCapability = createTechCapability(techCapability, product);
            if (hasParents) {
                log.info("Creating new relations");
                createRelations(newTechCapability, businessCapabilityRepository.findAllByCodeIn(techCapability.getParents()));
            }
        } else {
            TechCapability existing = currentTechCapabilityOpt.get();
            Product tcProduct = existing.getResponsibilityProduct();
            Integer tcProductId = tcProduct != null ? tcProduct.getId() : null;
            PutTechCapabilityDTO existingDTO = techCapabilityMapper.convertToPutTechCapabilityDTO(existing);
            boolean isDifferent = isDifferentFromCurrent(techCapability, existingDTO);
            boolean productChanged = !Objects.equals(productId, tcProductId);
            boolean isDeleted = existing.getDeletedDate() != null;
            boolean shouldUpdate = isDifferent || isDeleted || productChanged;
            if (shouldUpdate) {
                log.info("Updating TechCapability with code: {}", techCapability.getCode());
                updateTechCapabilityWithRelations(existing, techCapability, product, hasParents);
            }
        }
    }

    private void updateTechCapabilityWithRelations(TechCapability existing, PutTechCapabilityDTO techCapability, Product product, boolean hasParents) {
        updateTechCapability(existing, techCapability, product);
        log.info("Deleting old relations");
        techCapabilityRelationsRepository.deleteAllByTechCapability(existing);
        if (hasParents) {
            log.info("Creating new relations");
            createRelations(existing, businessCapabilityRepository.findAllByCodeIn(techCapability.getParents()));
        }
    }

    private Boolean isDifferentFromCurrent (PutTechCapabilityDTO techCapability, PutTechCapabilityDTO currentTechCapabilityDTO) {
        techCapability.setDescription(UrlWrapper.proxyUrl(techCapability.getDescription()));
        techCapability.setParents(normalizeParents(techCapability.getParents()));
        currentTechCapabilityDTO.setParents(normalizeParents(currentTechCapabilityDTO.getParents()));
        return !techCapability.equals(currentTechCapabilityDTO);
    }

    private List<String> normalizeParents(List<String> parents) {
        if (parents == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(new TreeSet<>(parents));
    }

    public void validateTechCapabilityDTO(PutTechCapabilityDTO techCapability) {
        StringBuilder errMsg = new StringBuilder();
        if (techCapability.getCode() == null || techCapability.getCode().isEmpty()) {
            errMsg.append("Отсутствует обязательное поле code\n");
        }
        if (techCapability.getName() == null) {
            errMsg.append("Отсутствует обязательное поле name\n");
        }
        if (!errMsg.toString().isEmpty()) {
            throw new ValidationException(errMsg.toString());
        }
    }

    private TechCapability createTechCapability(PutTechCapabilityDTO techCapability, Product product) {
        TechCapability newTechCapability = TechCapability.builder()
                .code(techCapability.getCode())
                .name(techCapability.getName())
                .createdDate(LocalDateTime.now())
                .description(UrlWrapper.proxyUrl(techCapability.getDescription()))
                .status(techCapability.getStatus())
                .responsibilityProduct(product)
                .build();
        newTechCapability = techCapabilityRepository.save(newTechCapability);
        return newTechCapability;
    }

    private void updateTechCapability(TechCapability currentTechCapability, PutTechCapabilityDTO techCapability, Product product) {
        currentTechCapability.setName(techCapability.getName());
        currentTechCapability.setDescription(UrlWrapper.proxyUrl(techCapability.getDescription()));
        currentTechCapability.setLastModifiedDate(LocalDateTime.now());
        currentTechCapability.setDeletedDate(null);
        currentTechCapability.setStatus(techCapability.getStatus());
        currentTechCapability.setResponsibilityProduct(product);
        techCapabilityRepository.save(currentTechCapability);
    }

    private void createRelations(TechCapability currentTechCapability, List<BusinessCapability> businessCapabilities) {
        List<TechCapabilityRelations> techCapabilityRelations = new ArrayList<>();
        for (BusinessCapability businessCapability : businessCapabilities) {
            TechCapabilityRelations techCapabilityRelation = new TechCapabilityRelations();
            techCapabilityRelation.setBusinessCapability(businessCapability);
            techCapabilityRelation.setTechCapability(currentTechCapability);
            log.info("check exist relations idBC=" + businessCapability.getId() + "and idTC=" + currentTechCapability.getId());
            if (!techCapabilityRelationsRepository.existsByBusinessCapabilityAndTechCapability(businessCapability, currentTechCapability)) {
                techCapabilityRelations.add(techCapabilityRelation);
            }
        }
        techCapabilityRelationsRepository.saveAll(techCapabilityRelations);
    }
}
