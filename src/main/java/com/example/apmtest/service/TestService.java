package com.example.apmtest.service;

import com.example.apmtest.entity.mysql.User;
import com.example.apmtest.entity.oracle.Product;
import com.example.apmtest.repository.mysql.UserRepository;
import com.example.apmtest.repository.oracle.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestService {

    private final UserRepository userRepository;

    @Value("${oracle.enabled:false}")
    private boolean oracleEnabled;

    private ProductRepository productRepository;

    public TestService(UserRepository userRepository, 
                      @Autowired(required = false) ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    @Transactional
    public Product createProduct(String name, Double price) {
        if (!oracleEnabled || productRepository == null) {
            throw new UnsupportedOperationException("Oracle database is not enabled. Please enable it in application.yml with oracle.enabled=true");
        }

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        return productRepository.save(product);
    }
} 
