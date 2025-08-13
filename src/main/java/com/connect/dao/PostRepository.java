package com.connect.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.connect.entities.UserPosts;

public interface PostRepository extends JpaRepository<UserPosts, Integer> {

	//fetching posts according to user 
	@Query("from UserPosts as p where p.user.user_id = :userId")
	public Page<UserPosts> findPostsbyUser(@Param("userId") int userId, Pageable pageable); 
	
	//fetching all posts of users
	@Query("from UserPosts as p where p.user.user_id = :userId")
	public List<UserPosts> findAllByUserId(@Param("userId") int userId);
	
	//fetching post by id
	@Query("from UserPosts as p where p.post_id = :pid")
	public UserPosts getPostByPostId(@Param("pid") int pid);
}
