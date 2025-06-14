package com.example.apmtest.service;

import com.example.apmtest.entity.mysql.User;
import com.example.apmtest.entity.oracle.Product;
import com.example.apmtest.repository.mysql.UserRepository;
import com.example.apmtest.repository.oracle.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TestService {
    
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    
    @Transactional
    public User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }
    
    @Transactional
    public Product createProduct(String name, Double price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        return productRepository.save(product);
    }
} 