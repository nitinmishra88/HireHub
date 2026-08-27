package com.boot.hirehub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.boot.hirehub.entity.Employer;
import com.boot.hirehub.repository.EmployerRepository;

import jakarta.validation.Valid;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Controller
public class ERegisterController {

    @Autowired
    private EmployerRepository employerRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register/employer")
    public String registerEmployer(
            @Valid Employer employer,
            BindingResult result,
            @RequestParam String confirmPassword,
	        RedirectAttributes redirectAttributes  

    ) {

        if (!employer.getPassword().equals(confirmPassword)) {
            result.rejectValue("password", "error.employer", "Passwords do not match");
        }

        if (employerRepository.existsByEmail(employer.getEmail())) {
            result.rejectValue("email", "error.employer", "Email already registered");
        }

        if (result.hasErrors()) {
            return "eregister";
        }
        employer.setPassword(passwordEncoder.encode(employer.getPassword()));
         
        employerRepository.save(employer);

	    redirectAttributes.addFlashAttribute("successMessage", "Your have been registerd successfully!");
        return "redirect:/employer/login";
    }
}
