package com.cinar.textile.repository;

import com.cinar.textile.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findUserByEmail(String email);

    @Transactional
    void deleteUserByEmail(String email);
}
