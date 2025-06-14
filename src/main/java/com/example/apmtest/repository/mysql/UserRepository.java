package com.example.apmtest.repository.mysql;

import com.example.apmtest.entity.mysql.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
} 