package ru.beeline.referenceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.beeline.referenceservice.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE LOWER(p.alias) = LOWER(:code)")
    Product findByAliasCaseInsensitive(@Param("code") String code);
}
