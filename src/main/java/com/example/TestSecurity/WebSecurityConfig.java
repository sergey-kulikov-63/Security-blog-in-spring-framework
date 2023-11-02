package com.example.TestSecurity;

import com.example.TestSecurity.repositories.UserRepo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final UserRepo userRepo;

    public WebSecurityConfig(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/addpost").hasRole("ADMIN")
                        .requestMatchers("/adduser").permitAll()
                        .requestMatchers("/posts/{postUrl}").permitAll()
                        .requestMatchers("/users/{username}/update").permitAll()
                        .requestMatchers("/").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll());
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            com.example.TestSecurity.models.User user = userRepo.findByUsername(username);
            if (user != null) {
                return User.withUsername(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRole())
                        .build();
            }
            throw new UsernameNotFoundException("User not found");
        };
    }
}