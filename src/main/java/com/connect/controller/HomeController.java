package com.connect.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.connect.dao.UserRepository;
import com.connect.entities.User;
import com.connect.helper.Alerts;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

	// autowire user repository for dao applications
	@Autowired
	private UserRepository userRepository;

	// autowire encryption encoder
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	// home page handler
	@RequestMapping("/")
	public String home(Model m, Principal p) {

		// redirecting to profile page if user is logged in
		if (p != null) {
			return "redirect:/user/profile";
		}
		// sending data to template
		m.addAttribute("title", "Home page");

		return "index";
	}

	// create account page handler
	@RequestMapping("/new_user")
	public String Register(Model m, Principal p) {

		// redirecting to profile page if user is logged in
		if (p != null) {
			return "redirect:/user/profile";
		}
		// sending data to template
		m.addAttribute("title", "Create new account");
		m.addAttribute("user", new User());

		return "register";
	}

	// terms and conditions handler
	@RequestMapping("/TermsandConditions")
	public String TandC(Model m, Principal p) {

		// redirecting to profile page if user is logged in
		if (p != null) {
			return "redirect:/user/profile";
		}
		// sending data to template
		m.addAttribute("title", "Terms and conditions page");

		return "TandC";
	}

	// registration handler
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model m,
			HttpSession session) {

		try {

			// checking if the user has agreed to terms and conditions or not
			if (!agreement) {

				throw new Exception("You have not agreed the terms and conditions");
			}

			// checking validations and sending error to register form
			if (result.hasErrors()) {
				m.addAttribute(user);
				return "register";
			}

			// saving encrypted password
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user.setProfile_image_name("default.png");
			user.setIsOnline(false);
			//saving user data in the database
			this.userRepository.save(user);
			m.addAttribute("user", new User());
			session.setAttribute("alert", new Alerts(
					"Account successfully created, go to login page to access your account", "alert-success"));
			return "register";

		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			m.addAttribute(user);
			session.setAttribute("alert",
					new Alerts("username or email already exists!!! please try with another one", "alert-danger"));
			return "register";
		} catch (Exception ex) {
			ex.printStackTrace();
			m.addAttribute(user);
			session.setAttribute("alert", new Alerts("somthing went wrong!!! " + ex.getMessage(), "alert-danger"));
			return "register";
		}
	}

	@GetMapping("/user_login")
	public String customLogin(Model m, Principal p) {

		// redirecting to profile page if user is logged in
		if (p != null) {
			return "redirect:/user/profile";
		}
		// sending data to template
		m.addAttribute("title", "Login page");
		return "login";
	}

}
