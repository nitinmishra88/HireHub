package com.boot.hirehub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.boot.hirehub.entity.Activity;

@Repository
public interface ActivityRepository
extends JpaRepository<Activity,Integer>{

    List<Activity> findTop5ByOrderByCreatedAtDesc();

}