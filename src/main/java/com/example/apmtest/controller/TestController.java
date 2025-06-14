package com.example.apmtest.controller;

import com.example.apmtest.entity.mysql.User;
import com.example.apmtest.entity.oracle.Product;
import com.example.apmtest.service.TestService;
import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/error")
    public String error() {
        try {
            log.info("/api/error");
            throw new Exception("This is a test.");
        } catch (Exception e) {
            Sentry.captureException(e);
        }

        return "error";
    }

    @GetMapping("/test")
    public String test() {
        return "Hello APM Test!";
    }

    @PostMapping("/users")
    public User createUser(@RequestParam String name, @RequestParam String email) {
        return testService.createUser(name, email);
    }

    @PostMapping("/products")
    public Product createProduct(@RequestParam String name, @RequestParam Double price) {
        return testService.createProduct(name, price);
    }

    @GetMapping("/sentry-test")
    public String sentryTest() {
        throw new RuntimeException("Sentry 연동 테스트용 예외!");
    }

    /**
     * Database error endpoints for MySQL
     */
    @GetMapping("/db-error/mysql/find")
    public ResponseEntity<?> findNonExistentUser(@RequestParam(defaultValue = "999999") Long userId) {
        try {
            User user = testService.findNonExistentUser(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Error finding non-existent user", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/db-error/mysql/delete")
    public ResponseEntity<?> deleteNonExistentUser(@RequestParam(defaultValue = "999999") Long userId) {
        try {
            testService.deleteNonExistentUser(userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (EmptyResultDataAccessException e) {
            log.error("Error deleting non-existent user", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/db-error/mysql/create-invalid")
    public ResponseEntity<?> createInvalidUser(@RequestParam(required = false) String name, 
                                              @RequestParam(required = false) String email) {
        try {
            User user = testService.createInvalidUser(name, email);
            return ResponseEntity.ok(user);
        } catch (DataIntegrityViolationException e) {
            log.error("Error creating invalid user", e);
            // Ensure Sentry captures this exception
            Sentry.captureException(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }

    /**
     * Database error endpoints for Oracle
     */
    @GetMapping("/db-error/oracle/find")
    public ResponseEntity<?> findNonExistentProduct() {
        try {
            Product product = testService.findNonExistentProduct();
            return ResponseEntity.ok(product);
        } catch (UnsupportedOperationException e) {
            log.error("Oracle database not enabled", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error finding non-existent product", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/db-error/oracle/delete")
    public ResponseEntity<?> deleteNonExistentProduct() {
        try {
            testService.deleteNonExistentProduct();
            return ResponseEntity.ok("Product deleted successfully");
        } catch (UnsupportedOperationException e) {
            log.error("Oracle database not enabled", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Error: " + e.getMessage());
        } catch (EmptyResultDataAccessException e) {
            log.error("Error deleting non-existent product", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        }
    }

    /**
     * General database error simulation
     */
    @GetMapping("/db-error/simulate")
    public ResponseEntity<?> simulateDatabaseError() {
        try {
            testService.simulateDatabaseError();
            return ResponseEntity.ok("This should never be returned");
        } catch (DataAccessException e) {
            log.error("Simulated database error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
} 
