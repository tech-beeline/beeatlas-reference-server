/*
 * Copyright (c) 2024 PJSC VimpelCom
 */
package ru.beeline.referenceservice.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.beeline.referenceservice.dto.ProductDTO;
import ru.beeline.referenceservice.dto.PutProductDTO;
import ru.beeline.referenceservice.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/product/api/v1/product")
@Api(value = "Product API", tags = "product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @ApiOperation(value = "Получить все продукты", response = List.class)
    public List<ProductDTO> getProducts() {
        return productService.getProducts();
    }

    @PutMapping("/{alias}")
    @ApiOperation(value = "Редактирование продукта")
    public ResponseEntity putProducts(@PathVariable String alias,
                                      @RequestBody PutProductDTO productPutDto) {
        productService.createOrUpdate(productPutDto, alias);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
