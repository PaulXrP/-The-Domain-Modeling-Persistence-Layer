package com.dev.pranay.user_passport_demo.repository;

import com.dev.pranay.user_passport_demo.dtos.UserDto;
import com.dev.pranay.user_passport_demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM users u JOIN FETCH u.passport WHERE u.id = :id")
    User fetchUserWithPassport(@Param("id") Long id);

    @Query("SELECT u from users u JOIN FETCH u.passport") //Avoids 10 queries (N+1 problem). Only 1 optimized JOIN.
    List<User> findAllUsersWithPassports();
}
