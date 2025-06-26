package ru.beeline.referenceservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.beeline.referenceservice.domain.Product;
import ru.beeline.referenceservice.dto.ProductDTO;
import ru.beeline.referenceservice.dto.PutProductDTO;
import ru.beeline.referenceservice.exception.ValidationException;
import ru.beeline.referenceservice.mapper.ProductMapper;
import ru.beeline.referenceservice.repository.ProductRepository;

import java.time.LocalDateTime;
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

    public void createOrUpdate(PutProductDTO productPutDto, String alias) {
        validateProductPutDto(productPutDto);
        Product product = productRepository.findByAliasCaseInsensitive(alias);
        if (product == null) {
            if (productRepository.existsByName(productPutDto.getName())) {
                throw new ValidationException("Продукт с таким именем уже существует");
            }
            product = Product.builder()
                    .alias(alias)
                    .name(productPutDto.getName())
                    .description(productPutDto.getDescription())
                    .createdDate(LocalDateTime.now())
                    .build();
        } else {
            product.setName(productPutDto.getName());
            product.setDescription(productPutDto.getDescription());
            product.setLastModifiedDate(LocalDateTime.now());
        }
        productRepository.save(product);
    }

    public void validateProductPutDto(PutProductDTO productPutDto) {
        StringBuilder errMsg = new StringBuilder();
        if (productPutDto.getName() == null || productPutDto.getName().equals("")) {
            errMsg.append("Отсутствует обязательное поле name");
        }
        if (!errMsg.toString().isEmpty()) {
            throw new ValidationException(errMsg.toString());
        }
    }
}