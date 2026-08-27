
package com.boot.hirehub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.boot.hirehub.entity.Employer;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, Integer> {

    Employer findByEmail(String email);

    boolean existsByEmail(String email);

}


