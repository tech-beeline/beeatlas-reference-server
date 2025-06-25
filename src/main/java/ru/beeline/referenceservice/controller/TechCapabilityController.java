package ru.beeline.referenceservice.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.beeline.referenceservice.dto.PutTechCapabilityDTO;
import ru.beeline.referenceservice.dto.TechCapabilityDTO;
import ru.beeline.referenceservice.service.TechCapabilityService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tech-capability")
public class TechCapabilityController {

    private final TechCapabilityService techCapabilityService;

    public TechCapabilityController(TechCapabilityService techCapabilityService) {
        this.techCapabilityService = techCapabilityService;
    }

    @GetMapping
    @ApiOperation(value = "Получение технических возможностей")
    public List<TechCapabilityDTO> getTechCapabilities(@RequestParam(value = "limit", required = false) Integer limit,
                                                       @RequestParam(value = "offset", required = false) Integer offset) {
        return techCapabilityService.getCapabilities(limit, offset);
    }

    @PutMapping
    @ApiOperation(value = "Создание/Обновление технической возможности")
    public ResponseEntity putTechCapability(@RequestBody PutTechCapabilityDTO techCapability) {
        techCapabilityService.createOrUpdate(techCapability);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
