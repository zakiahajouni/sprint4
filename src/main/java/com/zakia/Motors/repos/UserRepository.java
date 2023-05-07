package com.zakia.Motors.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import  com.zakia.Motors.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername (String username);
}
