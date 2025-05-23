package com.dev.pranay.user_passport_demo.repository;

import com.dev.pranay.user_passport_demo.models.Passport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassportRepository extends JpaRepository<Passport, Long> {
}
