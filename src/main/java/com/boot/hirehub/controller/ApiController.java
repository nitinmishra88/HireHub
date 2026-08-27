package com.boot.hirehub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boot.hirehub.entity.User;
import com.boot.hirehub.repository.UserRepository;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiController {

    @Autowired
    private UserRepository repo;

    @GetMapping("/users")
    public List<User> getUsers() {
        return repo.findAll();
    }
}