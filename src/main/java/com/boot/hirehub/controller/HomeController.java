package com.boot.hirehub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.boot.hirehub.entity.Employer;
import com.boot.hirehub.entity.Job;
import com.boot.hirehub.entity.User;
import com.boot.hirehub.repository.EmployerRepository;
import com.boot.hirehub.repository.JobRepository;
import com.boot.hirehub.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private EmployerRepository employerRepository;

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/")
	public String home(Model model) {

		List<Job> jobs = jobRepository.findTop6ByOrderByPostedDateDesc();
		long totalJobs = jobRepository.count();
		long totalEmployers = employerRepository.count();
		long totalUsers = userRepository.count();

		model.addAttribute("jobs", jobs);
		model.addAttribute("totalJobs", totalJobs);
		model.addAttribute("totalEmployers", totalEmployers);
		model.addAttribute("totalUsers", totalUsers);

		return "index";
	}

	@GetMapping("/about")
	public String about() {
		return "about";
	}

	@GetMapping("/contact")
	public String contact() {
		return "contact";
	}

	@GetMapping("/employer/register")
	public String showForm(Model model) {
		model.addAttribute("employer", new Employer());
		return "eregister";
	}

	@GetMapping("/user/register")
	public String showUserRegisterForm(Model model) {
		model.addAttribute("user", new User());
		return "uregister";
	}

	@GetMapping("/employer/login")
	public String elogin() {
		return "elogin";
	}

	@GetMapping("/user/login")
	public String ulogin() {
		return "ulogin";
	}

	@GetMapping("/admin/login")
	public String alogin() {
		return "alogin";
	}
	@GetMapping("/search-jobs")
	public String searchJobs(
	        @RequestParam(required = false) String keyword,
	        @RequestParam(required = false) String location,
	        Model model) {

	    if(keyword == null)
	        keyword = "";

	    if(location == null)
	        location = "";

	    List<Job> jobs =
	            jobRepository
	            .findByJobTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(
	                    keyword,
	                    location);

	    model.addAttribute("jobs", jobs);

	    return "search-jobs";
	}

	// ==========================
	// APPLY JOB LOGIN CHECK
	// ==========================

	@GetMapping("/apply-job/{jobId}")
	public String applyJob(@PathVariable int jobId, HttpSession session) {

		User user = (User) session.getAttribute("user");

		if (user == null) {

			// Save Job ID for redirect after login
			session.setAttribute("redirectJobId", jobId);

			return "redirect:/user/login";
		}

		// User already logged in
		return "redirect:/user/apply/" + jobId;
	}
}