package com.example.apmtest.service;

import com.example.apmtest.entity.mysql.User;
import com.example.apmtest.entity.oracle.Product;
import com.example.apmtest.repository.mysql.UserRepository;
import com.example.apmtest.repository.oracle.ProductRepository;
import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

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

    /**
     * Intentionally causes a database error by trying to find a non-existent user
     * This will generate a NoSuchElementException that Sentry can capture
     */
    @Transactional(readOnly = true)
    public User findNonExistentUser() {
        try {
            // Try to find a user with an ID that doesn't exist
            return userRepository.findById(999999L)
                    .orElseThrow(() -> new NoSuchElementException("User not found with ID: 999999"));
        } catch (Exception e) {
            // Log the exception to Sentry
            Sentry.captureException(e);
            // Re-throw the exception to propagate it to the controller
            throw e;
        }
    }

    /**
     * Intentionally causes a database error by trying to delete a non-existent user
     * This will generate an EmptyResultDataAccessException that Sentry can capture
     */
    @Transactional
    public void deleteNonExistentUser() {
        try {
            // Try to delete a user with an ID that doesn't exist
            userRepository.deleteById(999999L);
        } catch (EmptyResultDataAccessException e) {
            // Log the exception to Sentry
            Sentry.captureException(e);
            // Re-throw the exception to propagate it to the controller
            throw e;
        }
    }

    /**
     * Intentionally causes a database error by trying to create a user with invalid data
     * This will generate a DataIntegrityViolationException if there's a unique constraint on email
     */
    @Transactional
    public User createInvalidUser() {
        try {
            // Create a user with null values which might violate not-null constraints
            User user = new User();
            // Depending on the database constraints, this might cause an error
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            // Log the exception to Sentry
            Sentry.captureException(e);
            // Re-throw the exception to propagate it to the controller
            throw e;
        }
    }

    /**
     * Intentionally causes a database error by trying to find a non-existent product
     * This will generate a NoSuchElementException that Sentry can capture
     */
    @Transactional(readOnly = true)
    public Product findNonExistentProduct() {
        if (!oracleEnabled || productRepository == null) {
            throw new UnsupportedOperationException("Oracle database is not enabled. Please enable it in application.yml with oracle.enabled=true");
        }

        try {
            // Try to find a product with an ID that doesn't exist
            return productRepository.findById(999999L)
                    .orElseThrow(() -> new NoSuchElementException("Product not found with ID: 999999"));
        } catch (Exception e) {
            // Log the exception to Sentry
            Sentry.captureException(e);
            // Re-throw the exception to propagate it to the controller
            throw e;
        }
    }

    /**
     * Intentionally causes a database error by trying to delete a non-existent product
     * This will generate an EmptyResultDataAccessException that Sentry can capture
     */
    @Transactional
    public void deleteNonExistentProduct() {
        if (!oracleEnabled || productRepository == null) {
            throw new UnsupportedOperationException("Oracle database is not enabled. Please enable it in application.yml with oracle.enabled=true");
        }

        try {
            // Try to delete a product with an ID that doesn't exist
            productRepository.deleteById(999999L);
        } catch (EmptyResultDataAccessException e) {
            // Log the exception to Sentry
            Sentry.captureException(e);
            // Re-throw the exception to propagate it to the controller
            throw e;
        }
    }

    /**
     * Simulates a general database access error
     * This method will throw a custom DataAccessException that Sentry can capture
     */
    public void simulateDatabaseError() {
        try {
            // Simulate a database connection error
            throw new DataAccessException("Simulated database connection error") {};
        } catch (DataAccessException e) {
            // Log the exception to Sentry
            Sentry.captureException(e);
            // Re-throw the exception to propagate it to the controller
            throw e;
        }
    }
} 
