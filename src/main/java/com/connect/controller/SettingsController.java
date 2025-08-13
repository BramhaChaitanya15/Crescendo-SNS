package com.connect.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.connect.dao.CommentRepository;
import com.connect.dao.FollowerRepository;
import com.connect.dao.LikeRepository;
import com.connect.dao.NotificationRepository;
import com.connect.dao.PostRepository;
import com.connect.dao.ReplyRepository;
import com.connect.dao.ReportRepository;
import com.connect.dao.SaveRepository;
import com.connect.dao.UserDetailsRepository;
import com.connect.dao.UserRepository;
import com.connect.entities.Followers;
import com.connect.entities.Notifications;
import com.connect.entities.User;
import com.connect.entities.UserDetails;
import com.connect.entities.UserPosts;
import com.connect.helper.Alerts;
import com.connect.helper.DeleteResources;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/setting")
public class SettingsController {

	// autowiring repository classes
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private NotificationRepository notificationRepository;
	@Autowired
	private UserDetailsRepository detailsRepository;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private LikeRepository likeRepository;
	@Autowired
	private SaveRepository saveRepository;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private ReplyRepository replyRepository;
	@Autowired
	private FollowerRepository followerRepository;
	@Autowired
	private ReportRepository reportRepository;
	@Autowired
	private HttpSession session;

	// method to add common user data for all templates
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		// fetching user
		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);

		// fetching notifications data
		List<Notifications> notif = notificationRepository.findAllByReciepientUserId(user);

		List<Notifications> notifications = new ArrayList<Notifications>();
		List<Notifications> messages = new ArrayList<Notifications>();

		int nCount = 0;
		int mCount = 0;

		for (Notifications n : notif) {
			if (n.getType().equalsIgnoreCase("notification")) {
				notifications.add(n);
			}
		}
		for (Notifications n : notif) {
			if (n.getType().equalsIgnoreCase("message")) {
				messages.add(n);
			}
		}
		for (Notifications n : notifications) {
			if (!n.isReadStatus()) {
				nCount++;
			}
		}
		for (Notifications n : messages) {
			if (!n.isReadStatus()) {
				mCount++;
			}
		}

		model.addAttribute(user);
		model.addAttribute("notify", notifications);
		model.addAttribute("ms", messages);
		model.addAttribute("nCount", nCount);
		model.addAttribute("mCount", mCount);
	}

	// go to settings handlers
	@RequestMapping("/settings_general")
	public String general(Model m) {

		m.addAttribute("title", "General");

		return "user/settings/general";
	}

	@RequestMapping("/settings_privacy")
	public String privacy(Model m) {

		m.addAttribute("title", "Privacy");

		return "user/settings/privacy";
	}

	@RequestMapping("/settings_help")
	public String help(Model m) {

		m.addAttribute("title", "Help");

		return "user/settings/help";
	}
	// end go to settings handlers

	// general settings handlers
	// change name
	@PostMapping("/change_name")
	public String changeName(@RequestParam("first_name") String fName, @RequestParam("middle_name") String mName,
			@RequestParam("last_name") String lName, Principal p) {
		// fetching user
		String userName = p.getName();
		User user = userRepository.getUserByUserName(userName);
		user.setFirst_name(fName);
		user.setMiddle_name(mName);
		user.setLast_name(lName);
		this.userRepository.save(user);
		session.setAttribute("alert", new Alerts("Name Changed Successfully !!!", "alert-success"));
		return "redirect:/setting/settings_general";
	}

	// change email
	@PostMapping("/change_email")
	public String changeEmail(@RequestParam("user_email") String email, Principal p) {
		// fetching user
		String userName = p.getName();
		User user = userRepository.getUserByUserName(userName);
		user.setUser_email(email);
		this.userRepository.save(user);
		session.setAttribute("alert", new Alerts("Email Changed Successfully !!!", "alert-success"));
		return "redirect:/setting/settings_general";
	}

	// change phone number
	@PostMapping("/change_number")
	public String changeNumber(@RequestParam("user_phone") String num, Principal p) {
		// fetching user
		String userName = p.getName();
		User user = userRepository.getUserByUserName(userName);
		user.setUser_phone(num);
		this.userRepository.save(user);
		session.setAttribute("alert", new Alerts("Phone Number Changed Successfully !!!", "alert-success"));
		return "redirect:/setting/settings_general";
	}
	// end general settings handlers

	// change password handler
	@PostMapping("/change_password")
	public String changePassword(@RequestParam("oldPassword") String oldPass,
			@RequestParam("newPassword") String newPass, Principal p, HttpSession session) {

		// fetching user
		String userName = p.getName();
		User user = this.userRepository.getUserByUserName(userName);

		// check if old password is correct
		if (this.passwordEncoder.matches(oldPass, user.getPassword())) {
			// change password
			user.setPassword(this.passwordEncoder.encode(newPass));
			this.userRepository.save(user);
			session.setAttribute("alert", new Alerts("Password Changed Successfully !!!", "alert-success"));
		} else {
			// error
			session.setAttribute("alert", new Alerts("Old Password does not match !!!", "alert-danger"));
			return "redirect:/setting/settings_privacy";
		}
		return "redirect:/user/profile";
	}

	// change username
	@PostMapping("/change_username")
	public String changeUsername(@RequestParam("newUsername") String newUsername, Principal p, HttpSession session) {

		// fetching user
		String userName = p.getName();
		User user = this.userRepository.getUserByUserName(userName);

		// changing username
		user.setUsername(newUsername);
		this.userRepository.save(user);
		session.setAttribute("alert", new Alerts("Username Changed Successfully !!!", "alert-success"));

		return "redirect:/user_login";
	}

	// permanently delete account
	@RequestMapping("/delete-account")
	public String deleteAccount(Model m, Principal p) {

		// fetching user
		String userName = p.getName();
		User user = this.userRepository.getUserByUserName(userName);

		// fetching all data related to user
		UserDetails details = this.detailsRepository.findDetailsbyUser(user.getUser_id());
		List<UserPosts> posts = this.postRepository.findAllByUserId(user.getUser_id());
		List<Followers> follows = this.followerRepository.getUsersFollowed(user.getUser_id());
		List<Followers> follows1 = this.followerRepository.getUsersFollowing(user);

		try {
			// deleting all data relating to user
			// delete details
			if (details != null) {
				DeleteResources.deleteDetails(details, this.detailsRepository);
			}
			// delete all posts
			if (posts != null) {
				for (UserPosts post : posts) {
					DeleteResources.deletePost(post, this.postRepository, this.likeRepository,
							this.notificationRepository, this.commentRepository, this.replyRepository,
							this.saveRepository, this.reportRepository);
				}
			}
			// delete all notifications and messages
			DeleteResources.deleteAllNotification(user, this.notificationRepository);
			// delete all follow records
			if (follows != null) {
				for (Followers follow : follows) {
					DeleteResources.deleteFollow(follow, followerRepository);
				}
			}
			if (follows1 != null) {
				for (Followers follow : follows1) {
					DeleteResources.deleteFollow(follow, followerRepository);
				}
			}
			// delete profile image and user
			if (!user.getProfile_image_name().equalsIgnoreCase("default.png")) {
				// delete profile image
				DeleteResources.deleteProfileImg(user, this.userRepository, user.getProfile_image_name());
			}
			// delete user so that the account is removed permanently
			this.userRepository.delete(user);
			// remove user from the session too that is empty the principal
			this.session.invalidate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/";
	}
}
