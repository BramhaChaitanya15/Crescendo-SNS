package com.connect.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.connect.entities.PostLikes;
import com.connect.entities.User;
import com.connect.entities.UserPosts;

public interface LikeRepository extends JpaRepository<PostLikes, Integer> {

	//fetching like data
	public List<PostLikes> findAllByUserId(User user);
	
	public List<PostLikes> findAllByPostId(UserPosts posts);
	
	public PostLikes findByUserIdAndPostId(User user, UserPosts posts);
}
