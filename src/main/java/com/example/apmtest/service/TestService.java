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
     * 
     * @param userId the ID of the user to find (typically a non-existent ID)
     */
    @Transactional(readOnly = true)
    public User findNonExistentUser(Long userId) {
        try {
            // Try to find a user with the provided ID (which likely doesn't exist)
            return userRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));
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
     * 
     * @param userId the ID of the user to delete (typically a non-existent ID)
     */
    @Transactional
    public void deleteNonExistentUser(Long userId) {
        try {
            // Try to delete a user with the provided ID (which likely doesn't exist)
            userRepository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            // Log the exception to Sentry
            Sentry.captureException(e);
            // Re-throw the exception to propagate it to the controller
            throw e;
        }
    }

    /**
     * Intentionally causes a database error by trying to create a user with invalid data
     * This will generate a DataIntegrityViolationException due to null values for required fields
     * 
     * @param name the name of the user (can be null to trigger validation error)
     * @param email the email of the user (can be null to trigger validation error)
     */
    @Transactional
    public User createInvalidUser(String name, String email) {
        try {
            // Create a user with the provided values (which might be null)
            User user = new User();
            user.setName(name);
            user.setEmail(email);

            // This will cause a DataIntegrityViolationException if name or email is null
            // because they are required fields with @Column(nullable = false)
            User savedUser = userRepository.save(user);

            // If we get here, it means the save operation succeeded, which is not what we want
            // if the parameters were null. Let's throw an exception manually to ensure it's captured by Sentry
            if (savedUser.getName() == null || savedUser.getEmail() == null) {
                throw new DataIntegrityViolationException("User created with null values for required fields");
            }

            return savedUser;
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
