package com.connect.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.connect.entities.UserDetails;

public interface UserDetailsRepository extends JpaRepository<UserDetails, Integer> {

	//fetching user details according to user_id
	@Query("from UserDetails as d where d.user.user_id = :userId")
	public UserDetails findDetailsbyUser(@Param("userId") int userId);
}
