package com.boot.hirehub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.boot.hirehub.entity.ContactQuery;
import com.boot.hirehub.repository.ContactRepository;


@Controller
public class ContactController {
	@Autowired
	private ContactRepository contactRepository;

	 @Autowired
	    private JavaMailSender mailSender;
	@PostMapping("/cntct")
	public String submitContact(
	        @RequestParam("name") String name,
	        @RequestParam("email") String email,
	        @RequestParam("subject") String subject,
	        @RequestParam("message") String message,
	        RedirectAttributes redirectAttributes  

	) {
	    ContactQuery query = new ContactQuery();
	    query.setName(name);
	    query.setEmail(email);
	    query.setSubject(subject);
	    query.setMessage(message);
	    contactRepository.save(query);

	    SimpleMailMessage mail = new SimpleMailMessage();
	    mail.setTo(email);
	    mail.setSubject("HireHub: We Received Your Query");

	    mail.setText(
	        "Hi " + name + ",\n\n" +
	        "Thank you for reaching out to HireHub. We have successfully received your query with the following details:\n\n" +
	        "Subject: " + subject + "\n" +
	        "Message: " + message + "\n\n" +
	        "Our support team will review your query and get back to you as soon as possible, usually within 24-48 hours.\n\n" +
	        "In the meantime, you can explore our job portal and find opportunities that match your skills and interests.\n\n" +
	        "If you need urgent assistance, feel free to reply to this email and we will prioritize your request.\n\n" +
	        "Thank you for choosing HireHub!\n\n" +
	        "- HireHub Team"
	    );

	    mailSender.send(mail);
	    redirectAttributes.addFlashAttribute("successMessage", "Your query has been submitted successfully!");

	    return "redirect:/";
	}


}
