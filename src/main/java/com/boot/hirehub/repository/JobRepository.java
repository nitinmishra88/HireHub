package com.boot.hirehub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.boot.hirehub.entity.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer> {

    List<Job> findByEmployerId(int employerId);

    long countByEmployerId(int employerId);

    List<Job> findTop6ByOrderByPostedDateDesc();
   

	List<Job> findByJobTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(String keyword, String location);
}