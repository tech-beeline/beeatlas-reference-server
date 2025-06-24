package ru.beeline.referenceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PutTechCapabilityDTO {

    private String code;
    private String description;
    private String name;
    private String status;
    private String targetSystemCode;
    private List<String> parents;
}
