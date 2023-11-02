package com.example.TestSecurity.controllers;

import com.example.TestSecurity.models.Comment;
import com.example.TestSecurity.models.Post;
import com.example.TestSecurity.models.User;
import com.example.TestSecurity.repositories.CommentRepo;
import com.example.TestSecurity.repositories.PostRepo;
import com.example.TestSecurity.repositories.UserRepo;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;


@Controller
public class MainController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PostRepo postRepo;
    @Autowired
    private CommentRepo commentRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String main(Authentication a, Model model) {
        if (a != null)
            model.addAttribute("user", userRepo.findByUsername(a.getName()));
        model.addAttribute("posts", postRepo.findAll());
        return "main";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/adduser")
    public String addUser() {
        return "addUser";
    }
    @PostMapping("/adduser")
    public String addUser(@ModelAttribute User user, @RequestParam("file") MultipartFile file,
                          @RequestParam String password, Model model)
            throws IOException {
        User existingUser = userRepo.findByUsername(user.getUsername());
        if (existingUser != null) {
            model.addAttribute("errorMessage", "Логин " + user.getUsername() + " не доступен");
            return "addUser";
        }
        if (file.isEmpty()){
            byte[] defaultImageBytes = IOUtils.toByteArray(
                    new ClassPathResource("static/Anon.jpg").getInputStream()
            );
            user.setImage(Base64.getEncoder().encodeToString(defaultImageBytes));
        } else
            user.setImage(Base64.getEncoder().encodeToString(file.getBytes()));
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        userRepo.save(user);
        return "redirect:/";
    }
    @GetMapping("/addpost")
    public String addPost() {
        return "addPost";
    }
    @PostMapping("/addpost")
    public String addPost(@ModelAttribute Post post,
                          @RequestParam("file") MultipartFile file)
            throws IOException {
        post.setPostMainImage(Base64.getEncoder().encodeToString(file.getBytes()));
        post.setPostDate(LocalDate.now());
        postRepo.save(post);
        return "redirect:/";
    }
    @GetMapping("/posts/{postUrl}")
    public String viewPost(Authentication a,
                           @PathVariable String postUrl, Model model) {
        if (a != null)
            model.addAttribute("user", userRepo.findByUsername(a.getName()));
        Post post = postRepo.findPostByPostUrl(postUrl);
        model.addAttribute("post", post);
        model.addAttribute("comments", commentRepo.findByPost(post));
        return (post == null) ? "error" : "post";
    }
    @PostMapping("/posts/{postUrl}")
    public String addComment(@PathVariable String postUrl, Authentication a,
                             @ModelAttribute Comment comment){
        if (comment.getCommentText() != ""){
            comment.setCommentDate(LocalDate.now());
            comment.setPost(postRepo.findPostByPostUrl(postUrl));
            comment.setUser(userRepo.findByUsername(a.getName()));
            commentRepo.save(comment);
        }
        return "redirect:/posts/{postUrl}";
    }
    @GetMapping("/users/{username}")
    public String viewUser(Authentication a, @PathVariable String username,
                           Model model){
        if (a != null){
            model.addAttribute("authUser",userRepo.findByUsername(a.getName()));
            model.addAttribute("user",userRepo.findByUsername(username));
            return "user";
        } else
            return "error";
    }
    @PostMapping("/users/{username}/update")
    public String updateUser( @RequestParam String newName,
                             @PathVariable String username,
                             @RequestParam("image") MultipartFile file) throws IOException {
        User changeUser = userRepo.findByUsername(username);
        if (!file.isEmpty())
            changeUser.setImage(Base64.getEncoder().encodeToString(file.getBytes()));
        changeUser.setName(newName);
        userRepo.save(changeUser);
        return "redirect:/";
    }
    @GetMapping("/users/{username}/update")
    public String updateUser(@PathVariable String username, Model model){
        model.addAttribute("user", userRepo.findByUsername(username));
        return "updateUser";
    }
}