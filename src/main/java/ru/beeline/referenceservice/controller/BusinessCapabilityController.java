package ru.beeline.referenceservice.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.beeline.referenceservice.dto.BusinessCapabilityDTO;
import ru.beeline.referenceservice.service.BusinessCapabilityService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/business-capability")
public class BusinessCapabilityController {

    private final BusinessCapabilityService businessCapabilityService;

    public BusinessCapabilityController(BusinessCapabilityService businessCapabilityService) {
        this.businessCapabilityService = businessCapabilityService;
    }

    @GetMapping
    @ApiOperation(value = "Получение бизнес возможностей")
    public List<BusinessCapabilityDTO> getBusinessCapabilities(@RequestParam(value = "limit", required = false) Integer limit,
                                                               @RequestParam(value = "findBy", required = false, defaultValue = "ALL") String findBy,
                                                               @RequestParam(value = "offset", required = false) Integer offset) {
        return businessCapabilityService.getCapabilities(limit, offset, findBy);
    }
}
