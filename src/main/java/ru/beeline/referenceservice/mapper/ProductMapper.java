/*
 * Copyright (c) 2024 PJSC VimpelCom
 */
package ru.beeline.referenceservice.mapper;

import org.springframework.stereotype.Component;
import ru.beeline.referenceservice.domain.Product;
import ru.beeline.referenceservice.dto.ProductDTO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public List<ProductDTO> mapToDto(List<Product> products) {
        if (products != null && !products.isEmpty()) {
            return products.stream()
                    .map(product -> ProductDTO.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .alias(product.getAlias())
                            .description(product.getDescription())
                            .createdDate(product.getCreatedDate())
                            .updateDate(product.getLastModifiedDate())
                            .deletedDate(product.getDeletedDate())
                            .build())
                    .sorted(Comparator.comparing(ProductDTO::getId))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
