package com.connect.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.connect.entities.PostSaves;
import com.connect.entities.User;
import com.connect.entities.UserPosts;

public interface SaveRepository extends JpaRepository<PostSaves, Integer> {

	//fetch the post save data
	public List<PostSaves> findAllByUserId(User user);

	public List<PostSaves> findAllByPostId(UserPosts posts);

	public PostSaves findByUserIdAndPostId(User user, UserPosts posts);

}
