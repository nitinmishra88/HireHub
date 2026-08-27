package com.boot.hirehub.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.boot.hirehub.entity.User;
import com.boot.hirehub.repository.JobRepository;
import com.boot.hirehub.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class ULoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login/user")
    public String loginUser(
            @RequestParam String email,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            model.addAttribute("errorMessage", "Email not registered!");
            return "ulogin";
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("errorMessage", "Incorrect password!");
            return "ulogin";
        }

        session.setAttribute("user", user);

        Integer jobId = (Integer) session.getAttribute("redirectJobId");

        if (jobId != null) {

            session.removeAttribute("redirectJobId");

            return "redirect:/user/apply/" + jobId;
        }
        String redirectUrl =
                (String) session.getAttribute(
                        "redirectAfterLogin");

        if (redirectUrl != null) {

            session.removeAttribute(
                    "redirectAfterLogin");

            return "redirect:" + redirectUrl;
        }

        return "redirect:/user/dashboard";
    }

    @GetMapping("/user/dashboard")
    public String showDashboard(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/user/login";
        }

        int profileCompletion = 0;

        if (user.getName() != null && !user.getName().isBlank())
            profileCompletion += 10;

        if (user.getEmail() != null && !user.getEmail().isBlank())
            profileCompletion += 10;

        if (user.getPhone() != null && !user.getPhone().isBlank())
            profileCompletion += 10;

        if (user.getProfilePhoto() != null && !user.getProfilePhoto().isBlank())
            profileCompletion += 10;

        if (user.getQualification() != null && !user.getQualification().isBlank())
            profileCompletion += 15;

        if (user.getExperience() != null && !user.getExperience().isBlank())
            profileCompletion += 15;

        if (user.getSkills() != null && !user.getSkills().isBlank())
            profileCompletion += 15;

        if (user.getAddress() != null && !user.getAddress().isBlank())
            profileCompletion += 10;

        if (user.getLinkedin() != null && !user.getLinkedin().isBlank())
            profileCompletion += 5;

        model.addAttribute("user", user);
        model.addAttribute("profileCompletion", profileCompletion);

        return "user/uDashboard";
    }

    @GetMapping("/user/complete-profile")
    public String completeProfilePage(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/user/login";
        }

        model.addAttribute("user", user);

        return "user/completeprofile";
    }

    @PostMapping("/user/save-profile")
    public String saveProfile(
            @ModelAttribute User formUser,
            @RequestParam("photoFile") MultipartFile photoFile,
            @RequestParam("resumeFile") MultipartFile resumeFile,
            HttpSession session) {

        try {

            User sessionUser = (User) session.getAttribute("user");

            if (sessionUser == null) {
                return "redirect:/user/login";
            }

            User user = userRepository.findById(sessionUser.getId()).orElse(null);

            if (user == null) {
                return "redirect:/user/login";
            }

            String uploadDir = "uploads/";

            Files.createDirectories(Paths.get(uploadDir));

            // Profile Photo Upload
            if (!photoFile.isEmpty()) {

                String photoName =
                        System.currentTimeMillis() + "_"
                                + photoFile.getOriginalFilename();

                Path photoPath = Paths.get(uploadDir, photoName);

                Files.copy(
                        photoFile.getInputStream(),
                        photoPath,
                        StandardCopyOption.REPLACE_EXISTING);

                user.setProfilePhoto(photoName);
            }

            // Resume Upload
            if (!resumeFile.isEmpty()) {

                String resumeName =
                        System.currentTimeMillis() + "_"
                                + resumeFile.getOriginalFilename();

                Path resumePath = Paths.get(uploadDir, resumeName);

                Files.copy(
                        resumeFile.getInputStream(),
                        resumePath,
                        StandardCopyOption.REPLACE_EXISTING);

                user.setResume(resumeName);
            }

            user.setQualification(formUser.getQualification());
            user.setExperience(formUser.getExperience());
            user.setSkills(formUser.getSkills());
            user.setAddress(formUser.getAddress());
            user.setLinkedin(formUser.getLinkedin());

            userRepository.save(user);

            session.setAttribute("user", user);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/user/dashboard";
    }
    @GetMapping("/user/profile")
    public String viewProfile(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/user/login";
        }

        model.addAttribute("user", user);

        return "user/viewprofile";
    }

    @PostMapping("/logout/user")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/user/login";
    }
    @GetMapping("/jobs")
    public String allJobs(
            HttpSession session,
            Model model) {

        User user =
                (User) session.getAttribute("user");

        if (user == null) {

            session.setAttribute(
                    "redirectAfterLogin",
                    "/jobs");

            return "redirect:/user/login";
        }

        model.addAttribute(
                "jobs",
                jobRepository.findAll());

        model.addAttribute("user", user);

        return "user/jobs";
    }
}