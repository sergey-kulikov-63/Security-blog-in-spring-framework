package com.example.TestSecurity.repositories;

import com.example.TestSecurity.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepo extends JpaRepository<Post, Long> {
    Post findPostByPostUrl(String postUrl);
}
