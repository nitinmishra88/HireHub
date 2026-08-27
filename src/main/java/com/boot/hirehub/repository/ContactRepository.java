package com.boot.hirehub.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.boot.hirehub.entity.ContactQuery;

public interface ContactRepository extends JpaRepository<ContactQuery, Integer> {
}

