package com.kvtsoft.forum.controller;

import com.kvtsoft.forum.models.*;
import com.kvtsoft.forum.repositories.PostLikeRepository;
import com.kvtsoft.forum.repositories.PostRepository;
import com.kvtsoft.forum.repositories.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @GetMapping("/signup")
    public String signUpForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signUpSubmit(User user) {
        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login-form";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("loginForm") LoginForm loginForm, Model model, HttpServletResponse response) {
        // Perform authentication logic here
        if (isValidUser(loginForm.getUsername(), loginForm.getPassword())) {
            // Cache username in a cookie
            Cookie cookie = new Cookie("username", loginForm.getUsername());
            cookie.setMaxAge(24 * 60 * 60); // Set cookie expiration to 1 day (in seconds)
            response.addCookie(cookie);

            return "redirect:/forum";
        } else {
            model.addAttribute("error", true);
            return "login-form";
        }
    }

    // Logout endpoint to clear the cookie
    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        // Expire the cookie by setting its max age to 0
        Cookie cookie = new Cookie("username", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "redirect:/login";
    }


    private boolean isValidUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        return user != null && password.equals(user.getPassword());
    }


    @GetMapping("/forum")
    public String forum(Model model) {
        String username = (String) model.getAttribute("username");
        model.addAttribute("username", username);
        List<Post> latestPosts = postRepository.findTop10ByOrderByCreatedAtDesc();
        System.out.println("latestPosts: " + latestPosts);
        model.addAttribute("posts", latestPosts);

        return "forum";
    }

    @PostMapping("/post")
    public String createPost(@ModelAttribute("newPost") Post newPost, HttpServletRequest request) {
        // Retrieve username from the cookie
        String username = getUsernameFromCookie(request);
        newPost.setUsername(username);

        // Save the post to the database
        postRepository.save(newPost);

        return "redirect:/forum";
    }

    @PostMapping("/like/{postId}")
    public String likePost(@PathVariable Long postId, @ModelAttribute("username") String username) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            // Check if the user has already liked this post
            PostLike existingLike = postLikeRepository.findByPostIdAndUsername(postId, username);
            if (existingLike == null) {
                // Increment like count for the post
                post.setLikes(post.getLikes() + 1);
                postRepository.save(post);

                // Save the like entry
                PostLike newLike = new PostLike();
                newLike.setPost(post);
                newLike.setUsername(username);
                postLikeRepository.save(newLike);
            } else {
                // Handle scenario where user has already liked the post
                // Redirect or show error message indicating duplicate like attempt
            }
        }
        return "redirect:/forum";
    }

    // Helper method to retrieve username from cookie
    private String getUsernameFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("username")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
