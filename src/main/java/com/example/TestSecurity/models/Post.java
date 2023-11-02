package com.example.TestSecurity.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

@Entity
@Table(name = "posts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    private String postTitle;
    @Column(columnDefinition = "TEXT")
    private String postText;
    private String postUrl;
    @Column(columnDefinition = "TEXT")
    private String postMainImage;
    private LocalDate postDate;

    public String getPostDate() {
        DateTimeFormatter formatter
                = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("ru"));
        return "Опубликовано: " + postDate.format(formatter);
    }
}