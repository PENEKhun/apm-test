package com.example.apmtest.controller;

import com.example.apmtest.entity.mysql.User;
import com.example.apmtest.entity.oracle.Product;
import com.example.apmtest.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

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
} 