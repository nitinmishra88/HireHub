package com.boot.hirehub.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.boot.hirehub.entity.Employer;
import com.boot.hirehub.repository.ApplicationRepository;
import com.boot.hirehub.repository.EmployerRepository;
import com.boot.hirehub.repository.JobRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class ELoginController {

	@Autowired
	private EmployerRepository employerRepository;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private ApplicationRepository applicationRepository;
	
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


	@PostMapping("/login/employer")
	public String loginEmployer(@RequestParam String email, @RequestParam String password, Model model,
			HttpSession session) {

		Employer employer = employerRepository.findByEmail(email);

		if (employer == null) {
			model.addAttribute("errorMessage", "Email not found! Please Register");

			return "elogin";
		}

		if (!passwordEncoder.matches(password, employer.getPassword())) {

			model.addAttribute("errorMessage", "Incorrect password!");

			return "elogin";
		}

		session.setAttribute("loggedInEmployer", employer);

		return "redirect:/employer/dashboard";
	}

	@GetMapping("/employer/dashboard")
	public String showDashboard(
	        HttpSession session,
	        Model model) {

	    Employer employer =
	            (Employer) session.getAttribute(
	                    "loggedInEmployer");

	    if (employer == null) {
	        return "redirect:/employer/login";
	    }

	    long totalJobs =
	            jobRepository.countByEmployerId(
	                    employer.getId());

	    long totalApplications =
	            applicationRepository
	            .countByJobEmployerId(
	                    employer.getId());

	    long activeJobs =
	            jobRepository.countByEmployerId(
	                    employer.getId());

	    model.addAttribute("employer", employer);
	    model.addAttribute("totalJobs", totalJobs);
	    model.addAttribute("totalApplications", totalApplications);
	    model.addAttribute("activeJobs", activeJobs);

	    return "employer/edashboard";
	}

	@GetMapping("/employer/eprofile")
	public String employerProfile(HttpSession session, Model model) {

		Employer employer = (Employer) session.getAttribute("loggedInEmployer");

		if (employer == null) {
			return "redirect:/employer/login";
		}

		model.addAttribute("employer", employer);

		return "employer/eprofile";
	}

	// ================= LOGO UPLOAD =================

	@PostMapping("/employer/uploadlogo")
	public String uploadLogo(@RequestParam("logo") MultipartFile file, HttpSession session) {

		Employer employer = (Employer) session.getAttribute("loggedInEmployer");

		if (employer == null) {
			return "redirect:/employer/login";
		}

		if (file.isEmpty()) {
			return "redirect:/employer/eprofile";
		}

		try {

			String uploadDir = "uploads/"; 	
			Files.createDirectories(Paths.get(uploadDir));

			String fileName = employer.getId() + "_" + file.getOriginalFilename();

			Path path = Paths.get(uploadDir + fileName);

			Files.write(path, file.getBytes());

			employer.setCompanyLogo(fileName);

			employerRepository.save(employer);

			session.setAttribute("loggedInEmployer", employer);

		} catch (IOException e) {

			e.printStackTrace();
		}

		return "redirect:/employer/eprofile";
	}
	

	@PostMapping("/elogout")
	public String logout(HttpSession session) {

		session.invalidate();

		return "redirect:/employer/login?logout=true";
	}
}