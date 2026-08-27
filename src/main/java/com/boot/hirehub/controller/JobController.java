package com.boot.hirehub.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.boot.hirehub.entity.Employer;
import com.boot.hirehub.entity.Job;
import com.boot.hirehub.repository.JobRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @GetMapping("/employer/postjob")
    public String showPostJobPage(HttpSession session, Model model) {

        Employer employer =
                (Employer) session.getAttribute("loggedInEmployer");

        if (employer == null) {
            return "redirect:/employer/login";
        }

        model.addAttribute("employer", employer);

        return "employer/postjob";
    }

    // Save Job
    @PostMapping("/employer/savejob")
    public String saveJob(
            @ModelAttribute Job job,
            HttpSession session) {

        Employer employer =
                (Employer) session.getAttribute("loggedInEmployer");

        if (employer == null) {
            return "redirect:/employer/login";
        }

        job.setEmployer(employer);
        job.setCompanyName(employer.getCompanyName());
        job.setPostedDate(LocalDate.now());

        jobRepository.save(job);

        return "redirect:/employer/managejobs";
    }

    // Manage Jobs Page
    @GetMapping("/employer/managejobs")
    public String manageJobs(HttpSession session,
                             Model model) {

        Employer employer =
                (Employer) session.getAttribute("loggedInEmployer");

        if (employer == null) {
            return "redirect:/employer/login";
        }

        List<Job> jobs =
                jobRepository.findByEmployerId(employer.getId());

        model.addAttribute("employer", employer);
        model.addAttribute("jobs", jobs);

        return "employer/managejobs";
    }

    // Delete Job
    @GetMapping("/employer/deletejob/{id}")
    public String deleteJob(@PathVariable int id,
                            HttpSession session) {

        Employer employer =
                (Employer) session.getAttribute("loggedInEmployer");

        if (employer == null) {
            return "redirect:/employer/login";
        }

        Job job = jobRepository.findById(id).orElse(null);

        if (job != null &&
            job.getEmployer().getId() == employer.getId()) {

            jobRepository.delete(job);
        }

        return "redirect:/employer/managejobs";
    }
}