package com.connect.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.connect.entities.PostComments;
import com.connect.entities.User;
import com.connect.entities.UserPosts;

public interface CommentRepository extends JpaRepository<PostComments, Integer> {

	// fetching comments by post
	public List<PostComments> findAllByUserPost(UserPosts posts);
	// fetching comments by user
	public List<PostComments> findAllByUser(User user);

	// fetching comments by id
	@Query("from PostComments as c where c.comment_id = :cmtid")
	public PostComments getCommentByCommentId(@Param("cmtid") int cmtid);

}
