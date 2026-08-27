package com.boot.hirehub.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.boot.hirehub.entity.Application;
import com.boot.hirehub.entity.Job;
import com.boot.hirehub.entity.User;
import com.boot.hirehub.repository.ApplicationRepository;
import com.boot.hirehub.repository.JobRepository;
import com.boot.hirehub.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ApplyController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;
 
    @Autowired
    private EmailService emailService;
    
    @GetMapping("/user/apply/{jobId}")
    public String applyJob(
            @PathVariable Integer jobId,
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {

            session.setAttribute("redirectJobId", jobId);

            return "redirect:/user/login";
        }

        Job job = jobRepository.findById(jobId).orElse(null);

        if (job == null) {
            return "redirect:/";
        }

        model.addAttribute("job", job);
        model.addAttribute("user", user);

        return "user/applyjob";
    }

    @PostMapping("/user/apply/{jobId}")
    public String submitApplication(
            @PathVariable Integer jobId,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/user/login";
        }

        Job job = jobRepository.findById(jobId).orElse(null);

        if (job == null) {
            return "redirect:/";
        }

        // Duplicate Apply Check
        boolean alreadyApplied =
                applicationRepository.existsByUserIdAndJobId(
                        user.getId(),
                        jobId);

        if (alreadyApplied) {

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "You have already applied for this job.");

            return "redirect:/user/dashboard";
        }

        Application application = new Application();

        application.setUser(user);
        application.setJob(job);

        application.setApplicantName(name);
        application.setApplicantEmail(email);
        application.setApplicantPhone(phone);
        application.setAppliedDate(LocalDate.now());

        application.setStatus("Pending");
        applicationRepository.save(application);

        emailService.sendEmail(
                application.getApplicantEmail(),
                "Application Submitted Successfully - HireHub",

                "Dear " + application.getApplicantName() + ",\n\n"

                + "Thank you for applying through HireHub.\n\n"

                + "We are pleased to inform you that your application has been submitted successfully and has been forwarded to the employer for review.\n\n"

                + "Application Details:\n"
                + "-----------------------------------\n"
                + "Job Title   : " + job.getJobTitle() + "\n"
                + "Company     : " + job.getCompanyName() + "\n"
                + "Location    : " + job.getLocation() + "\n"
                + "Job Type    : " + job.getJobType() + "\n"
                + "Applied On  : " + application.getAppliedDate() + "\n"
                + "Status      : Pending\n"
                + "-----------------------------------\n\n"

                + "What happens next?\n"
                + "• The employer will review your application.\n"
                + "• If shortlisted, you may be contacted for further rounds.\n"
                + "• You can track the latest status of your application from the 'My Applications' section of your HireHub account.\n\n"

                + "We wish you the very best in your job search and career journey.\n\n"

                + "Regards,\n"
                + "HireHub Team\n"
                + "HireHub - Find Jobs, Build Careers"
        );

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Application submitted successfully!");
        return "redirect:/user/dashboard";
    }
}