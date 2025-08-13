package com.connect.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
import com.connect.entities.CommentReplies;
import com.connect.entities.Followers;
import com.connect.entities.Notifications;
import com.connect.entities.PostComments;
import com.connect.entities.PostLikes;
import com.connect.entities.PostSaves;
import com.connect.entities.User;
import com.connect.entities.UserDetails;
import com.connect.entities.UserPosts;
import com.connect.helper.Alerts;
import com.connect.helper.Base64Util;
import com.connect.helper.DeleteResources;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	// autowiring repository(dao) classes
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private UserDetailsRepository detailsRepository;
	@Autowired
	private LikeRepository likeRepository;
	@Autowired
	private SaveRepository saveRepository;
	@Autowired
	private NotificationRepository notificationRepository;
	@Autowired
	private FollowerRepository followerRepository;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private ReplyRepository replyRepository;
	@Autowired
	private ReportRepository reportRepository;

	// method to add common user data for all templates
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();

		User user = userRepository.getUserByUserName(userName);

		// fetching notifications data
		List<Notifications> notif = notificationRepository.findAllByReciepientUserId(user);

		List<Notifications> notifications = new ArrayList<Notifications>();
		List<Notifications> messages = new ArrayList<Notifications>();

		// fetching follower data
		List<Followers> follow = followerRepository.getUsersFollowed(user.getUser_id());

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
		model.addAttribute("follow", follow);
	}

	// handler for main profile page
	@RequestMapping("/profile")
	public String account(Model model, Principal principal) {

		model.addAttribute("title", "profile Home");

		// fetching user
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		// sending religion option data
		List<String> religions = new ArrayList<String>();
		religions.add("Hindu");
		religions.add("Muslim");
		religions.add("Christian");
		religions.add("Sikh");
		religions.add("Jew");

		model.addAttribute("religions", religions);

		// fetch all posts
		List<UserPosts> posts = this.postRepository.findAllByUserId(user.getUser_id());

		// fetch followers
		List<Followers> followers = this.followerRepository.getUsersFollowing(user);

		// fetch following
		List<Followers> following = this.followerRepository.getUsersFollowed(user.getUser_id());

		// sending UserDetails data
		UserDetails userDetails2 = this.detailsRepository.findDetailsbyUser(user.getUser_id());

		model.addAttribute("posts", posts);
		model.addAttribute("following", following);
		model.addAttribute("followers", followers);
		model.addAttribute("details", userDetails2);

		return "user/profile";

	}

	// add_post_form page handler
	@GetMapping("/add_post")
	public String addPostForm(Model model) {
		model.addAttribute("title", "Add Post");
		model.addAttribute("posts", new UserPosts());
		return "user/addpostform";
	}

	// add a post handler
	@PostMapping("/process_post")
	public String processPost(@ModelAttribute UserPosts userPosts, @RequestParam("postImg") MultipartFile imgFile,
			@RequestParam("postVid") MultipartFile vidFile, Principal principal, HttpSession session, Model model) {

		model.addAttribute("title", "Add Post");
		try {
			// fetching user
			String userName = principal.getName();
			User user = this.userRepository.getUserByUserName(userName);

			// formating date
			SimpleDateFormat DateFormat = new SimpleDateFormat("E,dd_MMM_yyyy--hh_mm_ss_SSS");

			// Initializing the Date Object
			Date date = new Date();

			// Using format() method for conversion
			String curr_date = DateFormat.format(date);

			// image upload
			if (imgFile.isEmpty()) {
				// no code
			} else {
				// System.out.println(file.getOriginalFilename());
				// creating file name with current date and time
				String fileNameWithoutExtension = FilenameUtils.getBaseName(imgFile.getOriginalFilename());
				String fileExtension = "." + FilenameUtils.getExtension(imgFile.getOriginalFilename());
				String fileName = fileNameWithoutExtension + "_" + curr_date + fileExtension;

				userPosts.setPost_image_name(fileName);

				// saving image
				File saveFile = new ClassPathResource("static/img/postMedia").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
				Files.copy(imgFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			}

			// video upload
			if (vidFile.isEmpty()) {
				// no code
			} else {
				// creating file name with current date and time
				String fileNameWithoutExtension = FilenameUtils.getBaseName(vidFile.getOriginalFilename());
				String fileExtension = "." + FilenameUtils.getExtension(vidFile.getOriginalFilename());
				String fileName = fileNameWithoutExtension + "_" + curr_date + fileExtension;

				userPosts.setPost_video_name(fileName);

				// saving video
				File saveFile = new ClassPathResource("static/img/postMedia").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
				Files.copy(vidFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}

			// setting date and time of post
			java.sql.Date d = new java.sql.Date(date.getTime());
			userPosts.setPost_date(d);

			// saving date string to show on posts
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a	E, dd MMM");
			userPosts.setDate_str(simpleDateFormat.format(date));

			// setting user to check to who the post belong
			userPosts.setUser(user);
			// saving post using dao class
			UserPosts pid = this.postRepository.save(userPosts);
			session.setAttribute("alert", new Alerts("Posted Successfully !!!", "alert-success"));
			System.out.println("data" + userPosts);

			// create a notification and sending it to frontend for js to send it to
			// websocket controller

			// fetching followers for the user
			List<Followers> follower = this.followerRepository.getUsersFollowing(user);

			String message = user.getUsername() + " uploaded a post...";

			List<Notifications> notifications = new ArrayList<Notifications>();

			for (Followers f : follower) {
				// fetching user per following
				User receiver = this.userRepository.getUserByUserId(f.getFollower_id());
				// creating notification object and saving it
				Notifications notification = new Notifications(message, d, simpleDateFormat.format(date),
						"notification", false, receiver, user, pid);
				this.notificationRepository.save(notification);
				notifications.add(notification);
			}

			model.addAttribute("notificationsForPost", notifications);

			return "user/addpostform";

		} catch (Exception e) {
			System.out.println("ERROR " + e.getMessage());
			e.printStackTrace();
			// send apropriate error message
			session.setAttribute("alert", new Alerts("Something went wrong !!! try again", "alert-danger"));
			return "user/addpostform";
		}
	}

	// My posts page handler
	@RequestMapping("/posts/{page}/{username}")
	public String myPosts(@PathVariable("page") Integer page, @PathVariable("username") String userName, Model model) {

		model.addAttribute("title", "My Posts");

		// fetching user
		User user = this.userRepository.getUserByUserName(userName);

		// creating page of posts
		Pageable pageable = PageRequest.of(page, 5);

		// saving posts on the page
		Page<UserPosts> userPost = this.postRepository.findPostsbyUser(user.getUser_id(), pageable);

		// converting page to list
		List<UserPosts> posts = userPost.getContent();
		// getting all likes in database according to user
		List<PostLikes> likes = this.likeRepository.findAllByUserId(user);
		// getting all saves in database according to user
		List<PostSaves> saves = this.saveRepository.findAllByUserId(user);
		
		// fetching all comments on posts
		List<List<PostComments>> commentsOnPosts = new ArrayList<List<PostComments>>();

		// getting all likes in database according to posts
		List<List<PostLikes>> likesOnPosts = new ArrayList<List<PostLikes>>();
		for (int i = 0; i < posts.size(); i++) {
			likesOnPosts.add(this.likeRepository.findAllByPostId(posts.get(i)));
			commentsOnPosts.add(this.commentRepository.findAllByUserPost(posts.get(i)));
		}
		// fetching id in list of integers
		List<Integer> likedPostIds = new ArrayList<Integer>();
		List<Integer> savedPostIds = new ArrayList<Integer>();
		List<Integer> PostIds = new ArrayList<Integer>();

		// saving post id to list
		for (int i = 0; i < posts.size(); i++) {
			PostIds.add(posts.get(i).getPost_id());
		}

		// saving liked post id to the list
		for (int i = 0; i < likes.size(); i++) {
			likedPostIds.add(likes.get(i).getPostId().getPost_id());
		}

		// saving saved post id to list
		for (int i = 0; i < saves.size(); i++) {
			savedPostIds.add(saves.get(i).getPostId().getPost_id());
		}

		model.addAttribute("uname", userName);
		model.addAttribute("likeOnPost", likesOnPosts);
		model.addAttribute("commentsOnPosts",commentsOnPosts);
		model.addAttribute("postId", PostIds);
		model.addAttribute("likedPostId", likedPostIds);
		model.addAttribute("savedPostIds", savedPostIds);
		model.addAttribute("likes", likes);
		model.addAttribute("posts", userPost);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", userPost.getTotalPages());

		return "user/userposts";
	}

	// delete post handler
	@GetMapping("/deletePost/{post_id}")
	public String deletePost(@PathVariable("post_id") Integer post_id, Model model, Principal principal,
			HttpSession session) throws IOException {

		// fetching user
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		// fetching the post from database
		UserPosts userPosts = this.postRepository.findById(post_id).get();

		// checking of post belongs to logged in user or not
		if (user.getUser_id() == userPosts.getUser().getUser_id()) {
			// calling delete post from helper package
			DeleteResources.deletePost(userPosts, this.postRepository, this.likeRepository, this.notificationRepository,
					this.commentRepository, this.replyRepository, this.saveRepository, this.reportRepository);
			session.setAttribute("alert", new Alerts("Post deleted !!!", "alert-success"));
		} else {
			session.setAttribute("alert", new Alerts("You don't have access to this post !!!", "alert-danger"));
		}
		return "redirect:/user/posts/0/" + userName;
	}

	// update post handler
	@PostMapping("/updatePost/{post_id}")
	public String updatePost(@PathVariable("post_id") Integer post_id, Model m) {

		m.addAttribute("title", "Update post");

		// fetching user
		UserPosts userPost = this.postRepository.findById(post_id).get();
		// sending post data to update form
		m.addAttribute("post", userPost);

		return "user/postupdateform";
	}

	// edit posts process handler
	@PostMapping("/edit_post_process")
	public String editPostProcess(@ModelAttribute UserPosts userPosts, @RequestParam("postImg") MultipartFile imgFile,
			@RequestParam("postVid") MultipartFile vidFile, @RequestParam("old") Integer old, Model m, Principal p,
			HttpSession session) {

		// fetching user
		String userName = p.getName();
		User user = this.userRepository.getUserByUserName(userName);

		try {

			// format date and time
			SimpleDateFormat DateFormat = new SimpleDateFormat("E,dd_MMM_yyyy--hh_mm_ss_SSS");

			// Initializing the Date Object
			Date date = new Date();

			// Using format() method for conversion
			String curr_date = DateFormat.format(date);

			// fetching saved post data
			UserPosts savedPost = this.postRepository.findById(userPosts.getPost_id()).get();

			String savedImage = savedPost.getPost_image_name();
			String savedVideo = savedPost.getPost_video_name();

			userPosts.setUser(null);

			// if user wants to remove media from the post
			if (old == 1) {
				// checking if post has any media and deleting it
				if (savedImage == null && savedVideo == null) {
					// do nothing
				} else if (savedImage != null) {
					// delete existing file
					File deleteFile = new ClassPathResource("static/img/postMedia").getFile();
					Path imgPath = Paths.get(deleteFile.getAbsolutePath() + File.separator + savedImage);
					Files.delete(imgPath);
					userPosts.setPost_image_name(null);
				} else if (savedVideo != null) {
					// delete existing file
					File deleteFile = new ClassPathResource("static/img/postMedia").getFile();
					Path imgPath = Paths.get(deleteFile.getAbsolutePath() + File.separator + savedVideo);
					Files.delete(imgPath);
					userPosts.setPost_video_name(null);
				}
			}

			// if user don't want to remove media
			if (imgFile.isEmpty() && vidFile.isEmpty() && old == 0) {
				// setting previous present data
				userPosts.setPost_image_name(savedImage);
				userPosts.setPost_video_name(savedVideo);
			}

			// image upload
			if (imgFile.isEmpty()) {
				// do nothing
			} else {
				// checking if post has any media and deleting it
				if (savedImage == null && savedVideo == null) {
					// do nothing
				} else if (savedImage != null) {
					// delete existing file
					File deleteFile = new ClassPathResource("static/img/postMedia").getFile();
					Path imgPath = Paths.get(deleteFile.getAbsolutePath() + File.separator + savedImage);
					Files.delete(imgPath);
					userPosts.setPost_image_name(null);
				} else if (savedVideo != null) {
					// delete existing file
					File deleteFile = new ClassPathResource("static/img/postMedia").getFile();
					Path imgPath = Paths.get(deleteFile.getAbsolutePath() + File.separator + savedVideo);
					Files.delete(imgPath);
					userPosts.setPost_video_name(null);
				}
				// creating file name with current date and time
				String fileNameWithoutExtension = FilenameUtils.getBaseName(imgFile.getOriginalFilename());
				String fileExtension = "." + FilenameUtils.getExtension(imgFile.getOriginalFilename());
				String fileName = fileNameWithoutExtension + "_" + curr_date + fileExtension;

				userPosts.setPost_image_name(fileName);

				// saving image
				File saveFile = new ClassPathResource("static/img/postMedia").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
				Files.copy(imgFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			}

			// video upload
			if (vidFile.isEmpty()) {
				// do nothing
			} else {
				// checking if post has any media and deleting it
				if (savedImage == null && savedVideo == null) {
					// do nothing
				} else if (savedImage != null) {
					// delete existing file
					File deleteFile = new ClassPathResource("static/img/postMedia").getFile();
					Path imgPath = Paths.get(deleteFile.getAbsolutePath() + File.separator + savedImage);
					Files.delete(imgPath);
					userPosts.setPost_image_name(null);
				} else if (savedVideo != null) {
					// delete existing file
					File deleteFile = new ClassPathResource("static/img/postMedia").getFile();
					Path imgPath = Paths.get(deleteFile.getAbsolutePath() + File.separator + savedVideo);
					Files.delete(imgPath);
					userPosts.setPost_video_name(null);
				}
				// creating file name with current date and time
				String fileNameWithoutExtension = FilenameUtils.getBaseName(vidFile.getOriginalFilename());
				String fileExtension = "." + FilenameUtils.getExtension(vidFile.getOriginalFilename());
				String fileName = fileNameWithoutExtension + "_" + curr_date + fileExtension;

				userPosts.setPost_video_name(fileName);

				// saving video
				File saveFile = new ClassPathResource("static/img/postMedia").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
				Files.copy(vidFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}

			// setting edited date and time of post
			java.sql.Date d = new java.sql.Date(date.getTime());
			userPosts.setPost_date(d);

			// saving edited date string to show on posts
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a	E, dd MMM");
			userPosts.setDate_str(simpleDateFormat.format(date) + " (edited) ");

			// setting previous user data
			userPosts.setUser(user);

			this.postRepository.save(userPosts);

			session.setAttribute("alert", new Alerts("Post Edited !!!", "alert-success"));
			System.out.println("data" + userPosts);

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("alert", new Alerts("Something went wrong !!! try again", "alert-danger"));
			return "redirect:/user/posts/0/" + userName;
		}

		return "redirect:/user/posts/0/" + userName;
	}

	// add user details handler
	@PostMapping("/add_details")
	public String addDetails(@ModelAttribute UserDetails userDetails, Model m, Principal p, HttpSession session) {

		m.addAttribute("tilte", "profile page");

		try {
			// fetching user
			String userName = p.getName();
			User user = this.userRepository.getUserByUserName(userName);

			userDetails.setUser(user);

			// saving details
			this.detailsRepository.save(userDetails);
			session.setAttribute("alert", new Alerts("Details added Successfully !!!", "alert-success"));

			return "redirect:/user/profile";
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("alert", new Alerts("Something went wrong !!! try again", "alert-danger"));
			return "redirect:/user/profile";
		}
	}

	// update details handler
	@PostMapping("/edit_details")
	public String updateDetails(@ModelAttribute UserDetails userDetails, Model m, Principal p, HttpSession session) {

		try {
			// fetching user
			String userName = p.getName();
			User user = this.userRepository.getUserByUserName(userName);

			// changing and saving details to database
			userDetails.setUser(user);
			this.detailsRepository.save(userDetails);

			session.setAttribute("alert", new Alerts("Details editted Successfully !!!", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("alert", new Alerts("Something went wrong !!! try again", "alert-danger"));
			return "redirect:/user/profile";
		}

		return "redirect:/user/profile";
	}

	// remove details
	@RequestMapping("/remove_details")
	public String removeDetails(Model m, Principal p, HttpSession session) {

		try {
			// fetching user
			String userName = p.getName();
			User user = this.userRepository.getUserByUserName(userName);

			UserDetails userDetails = this.detailsRepository.findDetailsbyUser(user.getUser_id());

			// deleting details to database
			DeleteResources.deleteDetails(userDetails, this.detailsRepository);

			session.setAttribute("alert", new Alerts("Details removed Successfully !!!", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("alert", new Alerts("Something went wrong !!! try again", "alert-danger"));
			return "redirect:/user/profile";
		}

		return "redirect:/user/profile";
	}

	// change profile image
	@PostMapping("/change_profile_image")
	public String addProfilePic(Model m, Principal p, @RequestParam("browse_image") MultipartFile imgFile,
			HttpSession session) {
		try {
			// fetching user
			String userName = p.getName();
			User user = this.userRepository.getUserByUserName(userName);

			// formatting date
			SimpleDateFormat DateFormat = new SimpleDateFormat("E,dd_MMM_yyyy--hh_mm_ss_SSS");

			// Initializing the Date Object
			Date date = new Date();

			// Using format() method for conversion
			String curr_date = DateFormat.format(date);
			if (imgFile.isEmpty()) {
				//
			} else {
				// creating file name with date and time
				String fileNameWithoutExtension = FilenameUtils.getBaseName(imgFile.getOriginalFilename());
				String fileExtension = "." + FilenameUtils.getExtension(imgFile.getOriginalFilename());
				String fileName = fileNameWithoutExtension + "_" + curr_date + fileExtension;

				String profileImgName = user.getProfile_image_name();

				if (!(profileImgName.equals("default.png"))) {

					// delete existing file
					File deleteFile = new ClassPathResource("static/img/profileImg").getFile();
					Path imgPath = Paths.get(deleteFile.getAbsolutePath() + File.separator + profileImgName);
					Files.delete(imgPath);

				}
				// saving new profile image
				user.setProfile_image_name(fileName);
				File saveFile = new ClassPathResource("static/img/profileImg").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
				Files.copy(imgFile.getInputStream(), path);

				this.userRepository.save(user);

				session.setAttribute("alert", new Alerts("profile image changed Successfully !!!", "alert-success"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("alert", new Alerts("Something went wrong !!! try again", "alert-danger"));
			return "redirect:/user/profile";
		}

		return "redirect:/user/profile";
	}

	// delete profile image handler
	@RequestMapping("/delete_profile_image")
	public String deleteProfileImage(Principal p, HttpSession session) {

		try {
			// fetching user
			String userName = p.getName();
			User user = this.userRepository.getUserByUserName(userName);

			// fetching profile pic name
			String profileImgName = user.getProfile_image_name();

			if (!(profileImgName.equals("default.png"))) {

				// delete existing file using helper package
				DeleteResources.deleteProfileImg(user, this.userRepository, profileImgName);
				session.setAttribute("alert", new Alerts("profile image deleted !!!", "alert-success"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("alert", new Alerts("Something went wrong !!! try again", "alert-danger"));
			return "redirect:/user/profile";
		}

		return "redirect:/user/profile";
	}

	// feed page handler
	@RequestMapping("/feed/{page}")
	public String feedPage(@PathVariable("page") Integer page, Model m, Principal p) {

		// adding title to page
		m.addAttribute("title", "feed");

		// fetching user
		String userName = p.getName();
		User user = this.userRepository.getUserByUserName(userName);

		// creating page of posts
		List<UserPosts> allPosts = this.postRepository.findAll();
		Collections.reverse(allPosts);
		Pageable pageable = PageRequest.of(page, 5);
		
		int start = (int) pageable.getOffset();
	    int end = Math.min((start + pageable.getPageSize()), allPosts.size());
	    List<UserPosts> pageContent = allPosts.subList(start, end);

		// saving posts on the page
		Page<UserPosts> userPost = new PageImpl<>(pageContent, pageable, allPosts.size());

		// converting page to list
		List<UserPosts> posts = userPost.getContent();
		// getting all likes in database
		List<PostLikes> likes = this.likeRepository.findAllByUserId(user);
		// getting all saves in database according to user
		List<PostSaves> saves = this.saveRepository.findAllByUserId(user);
		// fetching all comments on posts
		List<List<PostComments>> commentsOnPosts = new ArrayList<List<PostComments>>();

		// getting all likes in database according to posts
		List<List<PostLikes>> likesOnPosts = new ArrayList<List<PostLikes>>();
		for (int i = 0; i < posts.size(); i++) {
			likesOnPosts.add(this.likeRepository.findAllByPostId(posts.get(i)));
			commentsOnPosts.add(this.commentRepository.findAllByUserPost(posts.get(i)));
		}

		// fetching id in list of integers
		List<Integer> likedPostIds = new ArrayList<Integer>();
		List<Integer> savedPostIds = new ArrayList<Integer>();
		List<Integer> PostIds = new ArrayList<Integer>();

		// saving post id to list
		for (int i = 0; i < posts.size(); i++) {
			PostIds.add(posts.get(i).getPost_id());
		}

		// saving liked post id to the list
		for (int i = 0; i < likes.size(); i++) {
			likedPostIds.add(likes.get(i).getPostId().getPost_id());
		}

		// saving saved post id to list
		for (int i = 0; i < saves.size(); i++) {
			savedPostIds.add(saves.get(i).getPostId().getPost_id());
		}

		m.addAttribute("likeOnPost", likesOnPosts);
		m.addAttribute("commentsOnPosts", commentsOnPosts);
		m.addAttribute("postId", PostIds);
		m.addAttribute("likedPostId", likedPostIds);
		m.addAttribute("savedPostIds", savedPostIds);
		m.addAttribute("likes", likes);
		m.addAttribute("allPosts", userPost);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", userPost.getTotalPages());

		return "user/feed";
	}

	// search user page handler
	@RequestMapping("/search_user")
	public String searchUser(Model m) {
		m.addAttribute("title", "Search User page");
		return "user/searchuser";
	}

	// search user page handler
	@RequestMapping("/other_profile/{user_id}")
	public String searchedUserPage(@PathVariable("user_id") Integer user_id, Model m, Principal p) {

		// fetching logged in user
		String userName = p.getName();
		User user = this.userRepository.getUserByUserName(userName);

		// fetching user by user id
		User searchedUser = this.userRepository.findById(user_id).get();

		// fetch all posts
		List<UserPosts> posts = this.postRepository.findAllByUserId(user_id);

		// fetch followers
		List<Followers> followers = this.followerRepository.getUsersFollowing(searchedUser);

		// fetch following
		List<Followers> following = this.followerRepository.getUsersFollowed(searchedUser.getUser_id());

		// sending UserDetails data
		UserDetails userDetails = this.detailsRepository.findDetailsbyUser(searchedUser.getUser_id());

		m.addAttribute("followers", followers);
		m.addAttribute("following", following);
		m.addAttribute("details", userDetails);
		m.addAttribute("title", searchedUser.getFirst_name());
		m.addAttribute("searchedUser", searchedUser);
		m.addAttribute("serachedUserPosts", posts);

		return "user/otheruserprofile";
	}

	// show saved post handler
	@RequestMapping("/saved_posts/{page}")
	public String savedPosts(@PathVariable("page") Integer page, Model m, Principal p) {

		// adding title to page
		m.addAttribute("title", "feed");

		// fetching user
		String userName = p.getName();
		User user = this.userRepository.getUserByUserName(userName);

		// creating page of posts
		Pageable pageable = PageRequest.of(page, 5);

		// getting all likes in database
		List<PostLikes> likes = this.likeRepository.findAllByUserId(user);
		// getting all saves in database according to user
		List<PostSaves> saves = this.saveRepository.findAllByUserId(user);

		// creating page for saved posts
		List<UserPosts> postList = new ArrayList<UserPosts>();
		for (int i = 0; i < saves.size(); i++) {
			postList.add(saves.get(i).getPostId());
		}
		int start = (int) pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), postList.size());
		List<UserPosts> pageContent = postList.subList(start, end);
		Page<UserPosts> userPost = new PageImpl<UserPosts>(pageContent, pageable, postList.size());

		// fetching all comments on posts
		List<List<PostComments>> commentsOnPosts = new ArrayList<List<PostComments>>();

		// converting page to list
		List<UserPosts> posts = userPost.getContent();

		// getting all likes in database according to posts
		List<List<PostLikes>> likesOnPosts = new ArrayList<List<PostLikes>>();
		for (int i = 0; i < posts.size(); i++) {
			likesOnPosts.add(this.likeRepository.findAllByPostId(posts.get(i)));
			commentsOnPosts.add(this.commentRepository.findAllByUserPost(posts.get(i)));
		}

		// fetching id in list of integers
		List<Integer> likedPostIds = new ArrayList<Integer>();
		List<Integer> savedPostIds = new ArrayList<Integer>();
		List<Integer> PostIds = new ArrayList<Integer>();

		// saving post id to list
		for (int i = 0; i < posts.size(); i++) {
			PostIds.add(posts.get(i).getPost_id());
		}

		// saving liked post id to the list
		for (int i = 0; i < likes.size(); i++) {
			likedPostIds.add(likes.get(i).getPostId().getPost_id());
		}

		// saving saved post id to list
		for (int i = 0; i < saves.size(); i++) {
			savedPostIds.add(saves.get(i).getPostId().getPost_id());
		}

		m.addAttribute("likeOnPost", likesOnPosts);
		m.addAttribute("commentsOnPosts", commentsOnPosts);
		m.addAttribute("postId", PostIds);
		m.addAttribute("likedPostId", likedPostIds);
		m.addAttribute("savedPostIds", savedPostIds);
		m.addAttribute("likes", likes);
		m.addAttribute("allPosts", userPost);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", userPost.getTotalPages());

		return "user/savedposts";
	}

	// clear all notifications for a recipient user
	@RequestMapping("/clear-all-notif/{userId}")
	public String clearAllNotifications(@PathVariable("userId") Integer uid) {

		// fetch user whose notification is to be removed
		User user = this.userRepository.getUserByUserId(uid);
		// delete notifications
		DeleteResources.deleteNotification(user, this.notificationRepository);
		return "redirect:/user/profile";
	}

	// post page handler
	@RequestMapping("/postpage/{post_id}")
	public String postPage(@PathVariable("post_id") Integer pid, Model m) {
		UserPosts post = this.postRepository.getPostByPostId(pid);

		// fetch all data related to a post
		List<PostLikes> likes = likeRepository.findAllByPostId(post);
		List<PostSaves> saves = saveRepository.findAllByPostId(post);
		List<PostComments> comments = commentRepository.findAllByUserPost(post);
		List<CommentReplies> replies = replyRepository.findAllByPostId(post);

		m.addAttribute("title", post.getPost_title());
		m.addAttribute("likes", likes);
		m.addAttribute("saves", saves);
		m.addAttribute("comments", comments);
		m.addAttribute("reply", replies);
		m.addAttribute("p", post);

		return "user/postpage";
	}

	// delete comment
	@RequestMapping("/deleteComment/{comment_id}")
	public String deleteComment(@PathVariable("comment_id") Integer cid) {

		// fetch and delete comment using helper package's delete resources class
		PostComments pc = this.commentRepository.getCommentByCommentId(cid);
		int pid = pc.getUserPost().getPost_id();
		DeleteResources.deleteComment(pc, this.commentRepository, this.replyRepository);

		return "redirect:/user/postpage/" + pid;
	}

	// delete reply
	@RequestMapping("/deleteReply/{reply_id}")
	public String deleteReply(@PathVariable("reply_id") Integer rid) {

		CommentReplies cr = this.replyRepository.findCommentRepliesById(rid);
		int pid = cr.getPostComments().getUserPost().getPost_id();

		cr.setPostComments(null);
		cr.setUser(null);
		this.replyRepository.delete(cr);

		return "redirect:/user/postpage/" + pid;
	}

	// chat page controller
	@RequestMapping("/chat")
	public String chatPage(Model m, Principal p) {

		// adding title to page
		m.addAttribute("title", "chat page");

		// fetching user
		String userName = p.getName();
		User user = this.userRepository.getUserByUserName(userName);

		// fetch all notifications
		List<Notifications> snotif = this.notificationRepository.findAllBySenderUserId(user);
		List<Notifications> rnotif = this.notificationRepository.findAllByReciepientUserId(user);

		// separating messages from notifications
		List<Notifications> messages = new ArrayList<Notifications>();
		List<User> u = new ArrayList<User>();

		for (Notifications n : snotif) {
			if (n.getType().equalsIgnoreCase("message")) {
				messages.add(n);
			}
		}
		for (Notifications n : rnotif) {
			if (n.getType().equalsIgnoreCase("message")) {
				messages.add(n);
			}
		}
		for (Notifications n : messages) {
			if (n.getReciepientUserId() == user) {
				User temp = n.getSenderUserId();
				if (!u.contains(temp)) {
					u.add(temp);
				}
			}
			if (n.getReciepientUserId() != user) {
				User temp = n.getReciepientUserId();
				if (!u.contains(temp)) {
					u.add(temp);
				}
			}
		}

		// fetch list of followed users
		List<Followers> followed = this.followerRepository.getUsersFollowed(user.getUser_id());

		m.addAttribute("followed", followed);
		m.addAttribute("messages", u);

		return "user/chat";
	}

	// chatbox controller
	@RequestMapping("/chat-box/{id}")
	public String chatBox(@PathVariable("id") Integer id, Model m, Principal p) {
		// fetching user
		User receiver = this.userRepository.getUserByUserId(id);

		// fetch all notifications messages
		List<Notifications> messagesFromDB = this.notificationRepository.getAllMessages("message");

		List<Notifications> messages = new ArrayList<Notifications>();

		for (Notifications msg : messagesFromDB) {
			String message = msg.getMessage();
			// decrypt messages
			String decodedMessage = Base64Util.decode(message);
			msg.setMessage(decodedMessage);
			messages.add(msg);
		}

		m.addAttribute("receiver", receiver);
		m.addAttribute("message", messages);
		return "user/chatbox";
	}

	// chatbox controller
	@RequestMapping("/chat-box-personal/{id}")
	public String chatBoxPersonal(@PathVariable("id") Integer id, Model m, Principal p) {

		// fetching user
		User receiver = this.userRepository.getUserByUserId(id);

		m.addAttribute("title", receiver.getUsername());

		// fetch all notifications messages
		List<Notifications> messagesFromDB = this.notificationRepository.getAllMessages("message");

		List<Notifications> messages = new ArrayList<Notifications>();

		for (Notifications msg : messagesFromDB) {
			String message = msg.getMessage();
			// decrypt messages
			String decodedMessage = Base64Util.decode(message);
			msg.setMessage(decodedMessage);
			messages.add(msg);
		}

		m.addAttribute("receiver", receiver);
		m.addAttribute("message", messages);
		return "user/chatboxpersonal";
	}

	// delete chat completely
	@GetMapping("/delete-chat/{id}")
	public String searchFollower(@PathVariable("id") Integer id, Principal p) {

		// fetching users
		String userName = p.getName();
		User user1 = this.userRepository.getUserByUserName(userName);
		User user2 = this.userRepository.getUserByUserId(id);

		// fetch all notifications
		List<Notifications> messages = this.notificationRepository.getAllMessages("message");

		for (Notifications msg : messages) {
			if (msg.getReciepientUserId() == user1 && msg.getSenderUserId() == user2
					|| msg.getReciepientUserId() == user2 && msg.getSenderUserId() == user1) {
				msg.setPostId(null);
				msg.setReciepientUserId(null);
				msg.setSenderUserId(null);
				this.notificationRepository.delete(msg);
			}
		}
		return "redirect:/user/chat";
	}

	// followers page
	@GetMapping("/followers_page/{username}")
	public String followers(@PathVariable("username") String userName, Model m) {

		m.addAttribute("title", "followers");

		// fetching users
		User user = this.userRepository.getUserByUserName(userName);

		// fetching following
		List<Followers> follow = this.followerRepository.getUsersFollowing(user);
		List<User> followers = new ArrayList<User>();

		for (Followers f : follow) {
			User u = this.userRepository.getUserByUserId(f.getFollower_id());
			followers.add(u);
		}

		m.addAttribute("followers", followers);

		return "user/followers";
	}

	// following page
	@GetMapping("/following_page/{username}")
	public String following(@PathVariable("username") String userName, Model m) {

		m.addAttribute("title", "following");

		// fetching users
		User user = this.userRepository.getUserByUserName(userName);
		// fetching followers
		List<Followers> following = this.followerRepository.getUsersFollowed(user.getUser_id());

		m.addAttribute("following", following);

		return "user/following";
	}

}
