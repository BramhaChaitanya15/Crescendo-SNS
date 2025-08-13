package com.connect.controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.connect.dao.CommentRepository;
import com.connect.dao.FollowerRepository;
import com.connect.dao.LikeRepository;
import com.connect.dao.NotificationRepository;
import com.connect.dao.PostRepository;
import com.connect.dao.ReplyRepository;
import com.connect.dao.ReportRepository;
import com.connect.dao.SaveRepository;
import com.connect.dao.UserRepository;
import com.connect.entities.CommentReplies;
import com.connect.entities.Followers;
import com.connect.entities.Notifications;
import com.connect.entities.PostComments;
import com.connect.entities.PostLikes;
import com.connect.entities.PostSaves;
import com.connect.entities.Reports;
import com.connect.entities.User;
import com.connect.entities.UserPosts;
import com.connect.helper.DeleteResources;

@RestController
public class JsController {

	// autowiring repositories
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private LikeRepository likeRepository;
	@Autowired
	private PostRepository postRepository;
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

	// search handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal p) {

		// fetching user
		String userName = p.getName();
		User user = this.userRepository.getUserByUserName(userName);

		// calling search function
		// System.out.println(query);
		List<User> usersFromDB = this.userRepository.findByUsernameContaining(query);

		// creating new user list
		List<User> users = new ArrayList<User>();

		// removing the logged in user
		for (int i = 0; i < usersFromDB.size(); i++) {
			if (usersFromDB.get(i) != user) {
				users.add(usersFromDB.get(i));
			}
		}

		return ResponseEntity.ok(users);
	}

	// like handler
	@GetMapping("/like/{post_id}")
	public ResponseEntity<?> like(@PathVariable("post_id") Integer pid, Principal p) {

		// fetching user
		String userName = p.getName();
		User user = this.userRepository.getUserByUserName(userName);

		UserPosts post = this.postRepository.findById(pid).get();

		PostLikes like = new PostLikes();

		like.setUserId(user);
		like.setPostId(post);

		this.likeRepository.save(like);

		// formating date
		SimpleDateFormat DateFormat = new SimpleDateFormat("hh:mm a	E, dd MMM");

		// Initializing the Date Object
		Date date = new Date();

		// Using format() method for conversion
		String curr_date = DateFormat.format(date);
		// setting date and time of post
		java.sql.Date d = new java.sql.Date(date.getTime());

		String message = userName + " Liked your post...";

		Notifications notification = new Notifications();

		if (user.getUser_id() != post.getUser().getUser_id()) {
			notification = new Notifications(message, d, curr_date, "notification", false, post.getUser(), user, post);
			this.notificationRepository.save(notification);
		}

		// Creating a map to hold your data
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("notification", notification);
		responseData.put("data", "liked");

		return ResponseEntity.ok(responseData);
	}

	// unlike handler
	@GetMapping("/unlike/{post_id}")
	public ResponseEntity<?> unlike(@PathVariable("post_id") Integer pid, Principal p) {

		// fetching user
		String userName = p.getName();
		User user = this.userRepository.getUserByUserName(userName);

		// fetching post
		UserPosts userPost = this.postRepository.findById(pid).get();

		// fetching like on a particular post
		PostLikes postLike = this.likeRepository.findByUserIdAndPostId(user, userPost);

		DeleteResources.deleteLikes(postLike, this.likeRepository);

		return ResponseEntity.ok("unliked");
	}

	// save handler
	@GetMapping("/save/{post_id}")
	public ResponseEntity<?> save(@PathVariable("post_id") Integer pid, Principal p) {

		// fetching user
		String userName = p.getName();
		User user = this.userRepository.getUserByUserName(userName);

		//create new save record
		PostSaves savePost = new PostSaves();
		savePost.setUserId(user);
		savePost.setPostId(this.postRepository.findById(pid).get());

		this.saveRepository.save(savePost);

		return ResponseEntity.ok("saved");
	}

	// unsave handler
	@GetMapping("/unsave/{post_id}")
	public ResponseEntity<?> unsave(@PathVariable("post_id") Integer pid, Principal p) {

		// fetching user
		String userName = p.getName();
		User user = this.userRepository.getUserByUserName(userName);

		// fetching post
		UserPosts userPost = this.postRepository.findById(pid).get();

		// fetching like on a particular post
		PostSaves postSave = this.saveRepository.findByUserIdAndPostId(user, userPost);

		DeleteResources.deleteSaves(postSave, this.saveRepository);

		return ResponseEntity.ok("unsaved");
	}

	// follow user handler
	@GetMapping("/follow/{userId}")
	public ResponseEntity<?> follow(@PathVariable("userId") Integer uid, Principal p) {

		// fetching user logged in
		String userName = p.getName();
		User userFollowing = this.userRepository.getUserByUserName(userName);
		int userFollowingId = userFollowing.getUser_id();

		// fetch user to be followed
		User userFollowed = this.userRepository.getUserByUserId(uid);

		// saving follow data in database
		Followers f = new Followers(userFollowingId, userFollowed);
		this.followerRepository.save(f);

		// formating date
		SimpleDateFormat DateFormat = new SimpleDateFormat("hh:mm a	E, dd MMM");

		// Initializing the Date Object
		Date date = new Date();

		// Using format() method for conversion
		String curr_date = DateFormat.format(date);
		// setting date and time of post
		java.sql.Date d = new java.sql.Date(date.getTime());

		String message = userName + " started following you...";

		Notifications notification = new Notifications(message, d, curr_date, "notification", false, userFollowed,
				userFollowing, null);
		this.notificationRepository.save(notification);

		// Creating a map to hold your data
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("notification", notification);
		responseData.put("userFollowed", userFollowed.getUsername());
		responseData.put("data", "follow");

		return ResponseEntity.ok(responseData);
	}

	// unfollow handler
	@GetMapping("/unfollow/{userId}")
	public ResponseEntity<?> unfollow(@PathVariable("userId") Integer uid, Principal p) {

		// fetching user
		String userName = p.getName();
		User userFollowing = this.userRepository.getUserByUserName(userName);
		int userFollowingId = userFollowing.getUser_id();

		// fetch user to be followed
		User userFollowed = this.userRepository.getUserByUserId(uid);

		Followers f = followerRepository.getFollow(userFollowingId, userFollowed.getUser_id());

		DeleteResources.deleteFollow(f, this.followerRepository);

		// Creating a map to hold your data
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("userFollowed", userFollowed.getUsername());
		responseData.put("data", "unfollow");

		return ResponseEntity.ok(responseData);
	}

	// mark notification read handler
	@GetMapping("/read/{userId}/{senderUserId}")
	public ResponseEntity<?> notifRead(@PathVariable("userId") Integer uid,
			@PathVariable("senderUserId") Integer suid) {

		int c = 0;
		User user = this.userRepository.getUserByUserId(uid);
		// Creating a map to hold your data
		Map<String, Object> responseData = new HashMap<>();
		String read = null;

		// reading notifications and messages separately
		List<Notifications> notifications = notificationRepository.findAllByReciepientUserId(user);

		for (Notifications n : notifications) {
			if (!n.isReadStatus()) {
				if (n.getType().equalsIgnoreCase("notification")) {
					n.setReadStatus(true);
					notificationRepository.save(n);
					read = "nRead";
				} else if (n.getType().equalsIgnoreCase("message")) {
					c++;
					if (n.getSenderUserId().getUser_id() == suid) {
						n.setReadStatus(true);
						notificationRepository.save(n);
						read = "mRead";
						c--;
					}
				}
			}
		}
		responseData.put("count", c);
		responseData.put("read", read);
		return ResponseEntity.ok(responseData);
	}

	// ccomment save
	@GetMapping("/comment/{post_id}/{comment}")
	public ResponseEntity<?> comment(@PathVariable("post_id") Integer pid, @PathVariable("comment") String cmt,
			Principal p) {

		// fetching user
		String userName = p.getName();
		User user = this.userRepository.getUserByUserName(userName);

		UserPosts post = this.postRepository.getPostByPostId(pid);

		// Initializing the Date Object
		Date date = new Date();

		// saving date string to show on posts
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a	E, dd MMM");

		PostComments comment = new PostComments();

		comment.setDate_str(simpleDateFormat.format(date));
		comment.setComment(cmt);
		comment.setUser(user);
		comment.setUserPost(post);

		this.commentRepository.save(comment);

		// generating notification and saving it
		// setting date and time of notification
		java.sql.Date d = new java.sql.Date(date.getTime());
		Notifications notif = new Notifications();
		// message for notification
		String message = userName + " commented on your post";
		if (user.getUser_id() != post.getUser().getUser_id()) {
			notif = new Notifications(message, d, simpleDateFormat.format(date), "notification", false, post.getUser(),
					user, post);
			this.notificationRepository.save(notif);
		}

		// Creating a map to hold your data
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("notification", notif);
		responseData.put("PostComment", comment);
		responseData.put("data", "cmt");

		return ResponseEntity.ok(responseData);
	}

	// reply save
	@GetMapping("/comment-reply/{post_id}/{comment_id}/{reply}")
	public ResponseEntity<?> reply(@PathVariable("post_id") Integer pid, @PathVariable("comment_id") Integer cmtid,
			@PathVariable("reply") String reply, Principal p) {

		// fetching user
		String userName = p.getName();
		User user = this.userRepository.getUserByUserName(userName);

		UserPosts post = this.postRepository.getPostByPostId(pid);
		PostComments cmt = this.commentRepository.getCommentByCommentId(cmtid);

		// Initializing the Date Object
		Date date = new Date();

		// saving date string to show on posts
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a	E, dd MMM");

		CommentReplies r = new CommentReplies();

		r.setReply(reply);
		r.setUser(user);
		r.setPostComments(cmt);
		r.setDate_str(simpleDateFormat.format(date));

		this.replyRepository.save(r);

		// generating notification and saving it
		// setting date and time of notification
		java.sql.Date d = new java.sql.Date(date.getTime());
		Notifications notif = new Notifications();
		String message = userName + " replied on your comment";
		if (user.getUser_id() != cmt.getUser().getUser_id()) {
			notif = new Notifications(message, d, simpleDateFormat.format(date), "notification", false, cmt.getUser(),
					user, post);
			this.notificationRepository.save(notif);
		}

		// Creating a map to hold your data
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("notification", notif);
		responseData.put("reply", r);
		responseData.put("data", "reply");

		return ResponseEntity.ok(responseData);
	}

	// fetch all online users
	@GetMapping("/users/all-online")
	public ResponseEntity<?> getAllOnlineUsers() {

		// fetching all online users
		List<User> onlineUsers = this.userRepository.findAllOnlineUsers();

		// making list for offline users
		List<User> offlineUser = this.userRepository.findAllOfflineUsers();

		// Creating a map to hold your data
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("onlineUsers", onlineUsers);
		responseData.put("offlineUser", offlineUser);

		return ResponseEntity.ok(responseData);
	}

	// save the report generated
	@GetMapping("/submit-report/{pid}/{reportType}/{reportSubType}/{reportOptional}")
	public ResponseEntity<?> submitReport(@PathVariable("pid") Integer pid,
			@PathVariable("reportType") String reportType, @PathVariable("reportSubType") String reportSubType,
			@PathVariable("reportOptional") String reportOptional) {

		// generating a report and saving it to the database
		// list of reports
		List<String> reportTypes = new ArrayList<String>();
		reportTypes.add(reportType);
		reportTypes.add(reportSubType);
		if (reportOptional.equalsIgnoreCase("empty")) {
			reportTypes.add(null);
		} else {
			reportTypes.add(reportOptional);
		}
		Reports report = new Reports();
		report.setPostId(this.postRepository.getPostByPostId(pid));
		report.setReport(reportTypes);

		this.reportRepository.save(report);

		return ResponseEntity.ok("reported");
	}
}
