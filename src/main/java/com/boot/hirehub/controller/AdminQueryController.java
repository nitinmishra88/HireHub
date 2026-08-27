package com.boot.hirehub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.boot.hirehub.entity.ContactQuery;
import com.boot.hirehub.repository.ContactRepository;
import com.boot.hirehub.service.EmailService;

@Controller
@RequestMapping("/admin")
public class AdminQueryController {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private EmailService emailService;

    private String buildEmail(String userMessage) {
        return "Dear User,\n\n"
             + "Thank you for contacting HireHub.\n\n"
             + userMessage + "\n\n"
             + "If you have any further questions, feel free to reply.\n\n"
             + "Best Regards,\nHireHub Team";
    }
    @GetMapping("/queries")
    public String showQueries(Model model) {
        model.addAttribute("queries", contactRepository.findAll());
        return "admin/queries";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        contactRepository.deleteById(id);
        return "redirect:/admin/queries";
    }

    @GetMapping("/reply/{id}")
    public String replyPage(@PathVariable int id, Model model) {
        ContactQuery contact = contactRepository.findById(id).orElse(null);
        model.addAttribute("contact", contact);
        return "admin/replyq";
    }

    @PostMapping("/send")
    public String sendReply(
            @RequestParam String email,
            @RequestParam String subject,
            @RequestParam String message,
            @RequestParam int id
    ) {

        String finalMessage = buildEmail(message); 

        emailService.sendEmail(email, subject, finalMessage);

        contactRepository.deleteById(id);

        return "redirect:/admin/queries";
    }
    }