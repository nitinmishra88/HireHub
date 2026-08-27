package com.boot.hirehub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.boot.hirehub.entity.Admin;
import com.boot.hirehub.repository.AdminRepository;
import com.boot.hirehub.repository.EmployerRepository;
import com.boot.hirehub.repository.JobRepository;
import com.boot.hirehub.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private JobRepository jobRepository;

    private BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    // =========================
    // ADMIN LOGIN
    // =========================
    @PostMapping("/login/admin")
    public String loginAdmin(
            @RequestParam String email,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        Admin admin = adminRepository.findByEmail(email);

        if (admin == null) {
            model.addAttribute(
                    "errorMessage",
                    "Email not found!");
            return "alogin";
        }

        if (!passwordEncoder.matches(
                password,
                admin.getPassword())) {

            model.addAttribute(
                    "errorMessage",
                    "Wrong password!");
            return "alogin";
        }

        session.setAttribute("admin", admin);

        return "redirect:/admin/dashboard";
    }

    // =========================
    // ADMIN DASHBOARD
    // =========================
    @GetMapping("/admin/dashboard")
    public String dashboard(
            HttpSession session,
            Model model) {

        Admin admin =
                (Admin) session.getAttribute("admin");

        if (admin == null) {
            return "redirect:/admin/login";
        }

        long totalUsers =
                userRepository.count();

        long totalEmployers =
                employerRepository.count();

        long totalJobs =
                jobRepository.count();

        model.addAttribute("admin", admin);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalEmployers", totalEmployers);
        model.addAttribute("totalJobs", totalJobs);

        return "admin/adashboard";
    }

    // =========================
    // VIEW USERS
    // =========================
    @GetMapping("/admin/users")
    public String viewUsers(
            HttpSession session,
            Model model) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute(
                "users",
                userRepository.findAll());

        return "admin/manageusers";
    }

    // =========================
    // VIEW EMPLOYERS
    // =========================
    @GetMapping("/admin/employers")
    public String viewEmployers(
            HttpSession session,
            Model model) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute(
                "employers",
                employerRepository.findAll());

        return "admin/manageemployers";
    }

   
    @GetMapping("/admin/deleteuser/{id}")
    public String deleteUser(
            @PathVariable int id,
            HttpSession session) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        userRepository.deleteById((long) id);

        return "redirect:/admin/users";
    }

  
    @GetMapping("/admin/deleteemployer/{id}")
    public String deleteEmployer(
            @PathVariable int id,
            HttpSession session) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        employerRepository.deleteById(id);

        return "redirect:/admin/employers";
    }

    // =========================
    // ADMIN LOGOUT
    // =========================
    @PostMapping("/logout/admin")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/admin/login";
    }
}