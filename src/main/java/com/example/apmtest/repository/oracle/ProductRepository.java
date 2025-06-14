package com.example.apmtest.repository.oracle;

import com.example.apmtest.entity.oracle.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
} 