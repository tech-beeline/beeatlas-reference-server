package ru.beeline.referenceservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessCapabilityDTO {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Date createdDate;
    private Date lastModifiedDate;
    private Date deletedDate;
    private boolean isDomain;
    private boolean hasChildren;


}
