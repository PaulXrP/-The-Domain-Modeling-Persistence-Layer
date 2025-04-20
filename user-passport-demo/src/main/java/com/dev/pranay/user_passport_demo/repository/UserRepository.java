package com.dev.pranay.user_passport_demo.repository;

import com.dev.pranay.user_passport_demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
