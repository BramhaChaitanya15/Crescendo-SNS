package com.connect.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.connect.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	//fetching user from database if exists with username and password
	@Query("select u from User u where u.username = :username")
	public User getUserByUserName(@Param("username") String username); 

	//fetch user by user id
	@Query("select u from User u where u.user_id = :uid")
	public User getUserByUserId(@Param("uid") int uid);
	
	//getting users for search results
	public List<User> findByUsernameContaining(String keywords);
	
	//geting all users online
	 @Query("select u from User u where u.isOnline = true")
	 public List<User> findAllOnlineUsers();

	 //geting all users offline
	 @Query("select u from User u where u.isOnline = false")
	 public List<User> findAllOfflineUsers();
}
