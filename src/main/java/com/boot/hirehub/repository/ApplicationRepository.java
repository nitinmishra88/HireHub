package com.boot.hirehub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boot.hirehub.entity.Application;

public interface ApplicationRepository
        extends JpaRepository<Application, Integer> {

    List<Application> findByUserId(Long userId);

    List<Application> findByJobEmployerId(int employerId);
    long countByJobEmployerId(int employerId);
	boolean existsByUserIdAndJobId(Long id, Integer jobId);}