package com.example.CeylonEE.repo;

import com.example.CeylonEE.entity.Blog;
import com.example.CeylonEE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogRepo extends JpaRepository<Blog, Long> {
    List<Blog> findByUser(User user);
}

