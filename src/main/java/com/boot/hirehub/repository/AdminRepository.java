package com.boot.hirehub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.boot.hirehub.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Admin findByEmail(String email);
}
