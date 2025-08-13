package com.connect.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.ClassPathResource;

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
import com.connect.entities.Reports;
import com.connect.entities.User;
import com.connect.entities.UserDetails;
import com.connect.entities.UserPosts;

public class DeleteResources {

	// function to delete posts
	public static void deletePost(UserPosts userPosts, PostRepository postRepository, LikeRepository likeRepository,
			NotificationRepository notificationRepository, CommentRepository commentRepository,
			ReplyRepository replyRepository, SaveRepository saveRepository, ReportRepository reportRepository)
			throws IOException {

		// code for deleting post
		userPosts.setUser(null);
		// deleting post like and notifications related to the post
		List<PostLikes> pl = likeRepository.findAllByPostId(userPosts);
		List<PostSaves> ps = saveRepository.findAllByPostId(userPosts);
		List<Notifications> pn = notificationRepository.findAllByPostId(userPosts);
		List<PostComments> pc = commentRepository.findAllByUserPost(userPosts);
		List<Reports> pr = reportRepository.findAllByPostId(userPosts);

		for (PostComments c : pc) {
			List<CommentReplies> cr = replyRepository.findAllByPostComments(c);
			for (CommentReplies r : cr) {
				r.setPostComments(null);
				r.setUser(null);
				replyRepository.delete(r);
			}
			c.setUserPost(null);
			c.setUser(null);
			commentRepository.delete(c);
		}
		for (PostLikes l : pl) {
			l.setPostId(null);
			l.setUserId(null);
			likeRepository.delete(l);
		}
		for (PostSaves s : ps) {
			s.setPostId(null);
			s.setUserId(null);
			saveRepository.delete(s);
		}
		for (Notifications n : pn) {
			n.setPostId(null);
			n.setReciepientUserId(null);
			n.setSenderUserId(null);
			notificationRepository.delete(n);
		}
		for (Reports r : pr) {
			r.setPostId(null);
			reportRepository.delete(r);
		}

		String postImg = userPosts.getPost_image_name();
		String postVid = userPosts.getPost_video_name();
		// checking if post has any media if it has then delete it
		if (postImg == null && postVid == null) {
			// do nothing
		} else if (postVid == null) {
			File deleteFile = new ClassPathResource("static/img/postMedia").getFile();
			Path imgPath = Paths.get(deleteFile.getAbsolutePath() + File.separator + postImg);
			Files.delete(imgPath);
		} else if (postImg == null) {
			File deleteFile = new ClassPathResource("static/img/postMedia").getFile();
			Path vidPath = Paths.get(deleteFile.getAbsolutePath() + File.separator + postVid);
			Files.delete(vidPath);
		}

		// deleting post from database
		postRepository.delete(userPosts);

	}

	// function to delete profile image name
	public static void deleteProfileImg(User user, UserRepository userRepository, String profileImgName)
			throws IOException {
		// delete existing file
		File deleteFile = new ClassPathResource("static/img/profileImg").getFile();
		Path imgPath = Paths.get(deleteFile.getAbsolutePath() + File.separator + profileImgName);
		Files.delete(imgPath);

		// setting the profile image to default
		user.setProfile_image_name("default.png");
		userRepository.save(user);
	}

	// function to delete notifications
	public static void deleteNotification(User user, NotificationRepository notificationRepository) {

		List<Notifications> nList = notificationRepository.findAllByReciepientUserId(user);

		for (Notifications n : nList) {
			if (n.getType().equalsIgnoreCase("notification")) {
				n.setPostId(null);
				n.setReciepientUserId(null);
				n.setSenderUserId(null);
				// delete from database
				notificationRepository.delete(n);
			}
		}
	}

	// function to delete all notifications
	public static void deleteAllNotification(User user, NotificationRepository notificationRepository) {

		List<Notifications> nList = notificationRepository.findAllByReciepientUserId(user);
		List<Notifications> nList1 = notificationRepository.findAllBySenderUserId(user);

		if (nList != null && nList1 != null) {
			for (Notifications n : nList) {
				n.setPostId(null);
				n.setReciepientUserId(null);
				n.setSenderUserId(null);
				// delete from database
				notificationRepository.delete(n);
			}
			for (Notifications n : nList1) {
				n.setPostId(null);
				n.setReciepientUserId(null);
				n.setSenderUserId(null);
				// delete from database
				notificationRepository.delete(n);
			}
		}
	}

	// function to delete comments
	public static void deleteComment(PostComments pc, CommentRepository commentRepository,
			ReplyRepository replyRepository) {
		List<CommentReplies> cr = replyRepository.findAllByPostComments(pc);
		pc.setUser(null);
		pc.setUserPost(null);

		for (CommentReplies r : cr) {
			r.setPostComments(null);
			r.setUser(null);
			// delete from database
			replyRepository.delete(r);
		}
		// delete from database
		commentRepository.delete(pc);
	}

	// function to delete likes
	public static void deleteLikes(PostLikes postLike, LikeRepository likeRepository) {
		// removing post and user from like
		postLike.setPostId(null);
		postLike.setUserId(null);
		// delete from database
		likeRepository.delete(postLike);
	}

	// function to delete saves
	public static void deleteSaves(PostSaves postSaves, SaveRepository saveRepository) {
		// removing post and user from like
		postSaves.setPostId(null);
		postSaves.setUserId(null);
		// delete from database
		saveRepository.delete(postSaves);
	}

	// function to delete follows
	public static void deleteFollow(Followers followers, FollowerRepository followerRepository) {
		followers.setUser_id(null);
		followerRepository.delete(followers);
	}

	// function to delete user details
	public static void deleteDetails(UserDetails details, UserDetailsRepository detailsRepository) {
		details.setUser(null);
		detailsRepository.delete(details);
	}

}
