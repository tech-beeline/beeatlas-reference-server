package ru.beeline.referenceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.beeline.referenceservice.domain.Product;

public interface ProductRepository extends JpaRepository<Product,Integer> {
}
