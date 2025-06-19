package ru.beeline.referenceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PutBusinessCapabilityDTO {

    private String code;
    private String description;
    private Boolean isDomain;
    private String name;
    private String parent;
    private String status;
}
