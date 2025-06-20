package ru.beeline.referenceservice.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
}
