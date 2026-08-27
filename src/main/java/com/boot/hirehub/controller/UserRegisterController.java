package com.boot.hirehub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.boot.hirehub.entity.User;
import com.boot.hirehub.repository.UserRepository;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Controller
public class UserRegisterController {

	@Autowired
	private UserRepository userRepository;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@PostMapping("/register/user")
	public String registerUser(@Valid User user, BindingResult result, @RequestParam String confirmPassword,
			RedirectAttributes redirectAttributes) {
		if (!user.getPassword().equals(confirmPassword)) {
			result.rejectValue("password", "error.user", "Passwords do not match");
		}

		if (userRepository.existsByEmail(user.getEmail())) {
			result.rejectValue("email", "error.user", "Email already registered");
		}

		if (result.hasErrors()) {
			return "uregister";
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);

		redirectAttributes.addFlashAttribute("successMessage", "You have been registered successfully!");
		return "redirect:/user/login";

	}
}
