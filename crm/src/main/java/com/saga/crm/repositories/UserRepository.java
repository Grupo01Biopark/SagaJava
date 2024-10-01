package com.saga.crm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saga.crm.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    boolean existsByEmail(String email);

}
