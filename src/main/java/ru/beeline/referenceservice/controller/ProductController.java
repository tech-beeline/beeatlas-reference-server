package ru.beeline.referenceservice.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.beeline.referenceservice.dto.ProductDTO;
import ru.beeline.referenceservice.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
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
}
