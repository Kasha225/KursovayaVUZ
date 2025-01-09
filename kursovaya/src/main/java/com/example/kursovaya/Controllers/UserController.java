package com.example.kursovaya.Controllers;

import com.example.kursovaya.Entity.User;
import com.example.kursovaya.Repository.UserRepository;
import com.example.kursovaya.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    private UserRepository userRepository;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String nickname,
                               @RequestParam String email,
                               @RequestParam String password,
                               Model model) {
        if (userRepository.findByNickname(nickname).isPresent() || userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "User with this nickname or email already exists!");
            return "register";
        }

        User user = new User();
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPasswordHash(password);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        model.addAttribute("message", "Registration successful!");
        return "login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String nickname,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {
        Optional<User> optionalUser = userRepository.findByNickname(nickname);
        if (optionalUser.isPresent() && optionalUser.get().getPasswordHash().equals(password)) {
            session.setAttribute("user", optionalUser.get());
            return "redirect:/reviews";
        }
        model.addAttribute("error", "Invalid nickname or password!");
        return "login";
    }

}