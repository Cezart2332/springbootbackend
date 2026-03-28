package com.cezar.backend.repositories;

import com.cezar.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User, Long> {
}