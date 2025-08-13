package com.connect.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.connect.entities.Reports;
import com.connect.entities.UserPosts;

public interface ReportRepository extends JpaRepository<Reports, Integer> {
	//fetch all reports by postId
	public List<Reports> findAllByPostId(UserPosts userPosts);
}
