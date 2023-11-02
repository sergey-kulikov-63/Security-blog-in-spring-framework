package com.example.TestSecurity.repositories;

import com.example.TestSecurity.models.Comment;
import com.example.TestSecurity.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepo extends JpaRepository<Comment, Long> { // наследование методов от JpaRepository
    List<Comment> findByPost(Post post);
}