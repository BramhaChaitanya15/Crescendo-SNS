package com.connect.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.connect.entities.Followers;
import com.connect.entities.User;

public interface FollowerRepository extends JpaRepository<Followers, Integer>{
	
	//fetch followed users by user id
	@Query("select f from Followers f where f.follower_id = :uid")
	public List<Followers> getUsersFollowed(@Param("uid") int uid);
	
	//fetch following users by user id
	@Query("select f from Followers f where f.user_id = :uid")
	public List<Followers> getUsersFollowing(@Param("uid") User uid);

	//fetch particular follow record based on follower and followed
	@Query("select f from Followers f where f.follower_id = :uid1 and f.user_id.user_id = :uid2")
	public Followers getFollow(@Param("uid1") int followerId, @Param("uid2") int followedId);
	
	//fetch follow by follow id
	public Followers findFollowersById(int fid);

}
