package project.intalk.PostcardProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.intalk.PostcardProject.personas.User;
import project.intalk.PostcardProject.repository.UserRepository;

@RestController
@RequestMapping("/auth")
public class WebController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {

        return userRepository.save(user);
    }
}
