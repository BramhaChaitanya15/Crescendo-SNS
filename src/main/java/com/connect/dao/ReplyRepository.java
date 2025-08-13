package com.connect.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.connect.entities.CommentReplies;
import com.connect.entities.PostComments;
import com.connect.entities.User;
import com.connect.entities.UserPosts;

public interface ReplyRepository extends JpaRepository<CommentReplies, Integer> {
	
	//fetch replies by postId
	@Query("from CommentReplies as r where r.postComments.userPost = :pid")
	public List<CommentReplies> findAllByPostId(UserPosts pid);
	
	//fetch replies by comments
	@Query("from CommentReplies as r where r.postComments = :cid")
	public List<CommentReplies> findAllByPostComments(PostComments cid);
	
	//fetch replies by user
	public List<CommentReplies> findAllByUser(User User);
	//fetch reply by id
	public CommentReplies findCommentRepliesById(int rid);
	
}
