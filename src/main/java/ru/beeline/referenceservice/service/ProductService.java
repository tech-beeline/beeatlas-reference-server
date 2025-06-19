package ru.beeline.referenceservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.beeline.referenceservice.dto.ProductDTO;
import ru.beeline.referenceservice.mapper.ProductMapper;
import ru.beeline.referenceservice.repository.ProductRepository;

import java.util.List;

@Transactional
@Service
public class ProductService {

    private final ProductMapper productMapper;

    private final ProductRepository productRepository;

    public ProductService(ProductMapper productMapper, ProductRepository productRepository) {
        this.productMapper = productMapper;
        this.productRepository = productRepository;
    }

    public List<ProductDTO> getProducts() {
        return productMapper.mapToDto(productRepository.findAll());
    }
}