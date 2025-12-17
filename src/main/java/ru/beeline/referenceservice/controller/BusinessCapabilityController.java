/*
 * Copyright (c) 2024 PJSC VimpelCom
 */
package ru.beeline.referenceservice.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.beeline.referenceservice.dto.BusinessCapabilityDTO;
import ru.beeline.referenceservice.dto.PutBusinessCapabilityDTO;
import ru.beeline.referenceservice.service.BusinessCapabilityService;

import java.util.List;

@RestController
@RequestMapping("/capability/api/v1/business-capability")
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

    @PutMapping
    @ApiOperation(value = "Создание/Обновление бизнес возможности")
    public ResponseEntity putBusinessCapability(@RequestBody PutBusinessCapabilityDTO capability) {
        businessCapabilityService.putCapability(capability);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
