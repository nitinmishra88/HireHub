package com.boot.hirehub.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.boot.hirehub.entity.Application;
import com.boot.hirehub.entity.Employer;
import com.boot.hirehub.entity.User;
import com.boot.hirehub.repository.ApplicationRepository;
import com.boot.hirehub.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ApplicationController {

	@Autowired
	private ApplicationRepository applicationRepository;
	@Autowired
	private EmailService emailService;

	// ================= USER APPLICATIONS =================

	@GetMapping("/user/applications")
	public String viewUserApplications(HttpSession session, Model model) {

		User user = (User) session.getAttribute("user");

		if (user == null) {
			return "redirect:/user/login";
		}

		List<Application> applications = applicationRepository.findByUserId(user.getId());

		model.addAttribute("applications", applications);

		return "user/viewapplications";
	}

	// ================= EMPLOYER APPLICATIONS =================

	@GetMapping("/employer/applications")
	public String viewApplications(
	        HttpSession session,
	        Model model) {

	    Employer employer =
	            (Employer) session.getAttribute("loggedInEmployer");

	    if (employer == null) {
	        return "redirect:/employer/login";
	    }

	    List<Application> applications =
	            applicationRepository.findByJobEmployerId(
	                    employer.getId());

	    Map<String, List<Application>> jobMap =
	            applications.stream()
	                    .collect(Collectors.groupingBy(
	                            app -> app.getJob().getJobTitle()));

	    model.addAttribute("jobMap", jobMap);

	    return "employer/viewapplication";
	}
	@PostMapping("/employer/application/approve/{id}")
	public String approveApplication(@PathVariable int id, HttpSession session) {

		Employer employer = (Employer) session.getAttribute("loggedInEmployer");

		if (employer == null) {
			return "redirect:/employer/login";
		}

		Application application = applicationRepository.findById(id).orElse(null);

		if (application != null) {

			application.setStatus("Approved");

			applicationRepository.save(application);
			emailService.sendEmail(application.getApplicantEmail(), "Application Shortlisted - HireHub",

					"Dear " + application.getApplicantName() + ",\n\n"

							+ "Congratulations!\n\n"

							+ "We are pleased to inform you that your application has been reviewed and shortlisted by the employer.\n\n"

							+ "Application Details:\n" + "-----------------------------------\n" + "Job Title : "
							+ application.getJob().getJobTitle() + "\n" + "Company   : "
							+ application.getJob().getCompanyName() + "\n" + "Status    : Shortlisted\n"
							+ "-----------------------------------\n\n"

							+ "Your profile has successfully passed the initial screening process. "
							+ "The employer may contact you soon regarding the next stages of recruitment, "
							+ "which could include interviews, assessments, or further discussions.\n\n"

							+ "For further communication regarding this opportunity, you may contact the employer at:\n\n"

							+ "Employer: " + application.getJob().getEmployer().getCompanyName() + "\n" + "Email: "
							+ application.getJob().getEmployer().getEmail() + "\n\n"

							+ "Please keep an eye on your email and HireHub dashboard for future updates.\n\n"
							+ "We wish you the very best for the next stage of the hiring process.\n\n"

							+ "Regards,\n" + "HireHub Team\n" + "HireHub - Find Jobs, Build Careers");
		}

		return "redirect:/employer/applications";
	}

	// ================= REJECT APPLICATION =================

	@PostMapping("/employer/application/reject/{id}")
	public String rejectApplication(@PathVariable int id, HttpSession session) {

		Employer employer = (Employer) session.getAttribute("loggedInEmployer");

		if (employer == null) {
			return "redirect:/employer/login";
		}

		Application application = applicationRepository.findById(id).orElse(null);

		if (application != null) {

			application.setStatus("Rejected");

			applicationRepository.save(application);
			emailService.sendEmail(application.getApplicantEmail(), "Application Update - HireHub",

					"Dear " + application.getApplicantName() + ",\n\n"

							+ "Thank you for your interest in the position and for taking the time to apply through HireHub.\n\n"

							+ "After careful consideration, we regret to inform you that you have not been selected for the next stage of the recruitment process for the following position:\n\n"

							+ "Application Details:\n" + "-----------------------------------\n" + "Job Title : "
							+ application.getJob().getJobTitle() + "\n" + "Company   : "
							+ application.getJob().getCompanyName() + "\n" + "Status    : Not Selected\n"
							+ "-----------------------------------\n\n"

							+ "Please do not be discouraged. Recruitment decisions are often based on many factors, "
							+ "and this outcome does not diminish your skills, qualifications, or future potential.\n\n"

							+ "We encourage you to continue exploring new opportunities on HireHub and apply for roles that match your profile and career goals.\n\n"

							+ "For further communication regarding this opportunity, you may contact the employer at:\n\n"

							+ "Employer: " + application.getJob().getEmployer().getCompanyName() + "\n" + "Email: "
							+ application.getJob().getEmployer().getEmail() + "\n\n"

							+ "Please keep an eye on your email and HireHub dashboard for future updates.\n\n"
							+ "Thank you again for choosing HireHub. We wish you success in your future job search.\n\n"

							+ "Regards,\n" + "HireHub Team\n" + "HireHub - Find Jobs, Build Careers");
		}

		return "redirect:/employer/applications";
	}
}
