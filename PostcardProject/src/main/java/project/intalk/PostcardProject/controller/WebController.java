package project.intalk.PostcardProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import project.intalk.PostcardProject.domain.LoginForm;
import project.intalk.PostcardProject.model.RegistrationForm;
import project.intalk.PostcardProject.personas.User;
import project.intalk.PostcardProject.repository.UserRepository;

import java.util.Optional;
import java.util.logging.Logger;

@Controller
public class WebController {

    private static final Logger logger = Logger.getLogger(WebController.class.getName());

    private final UserRepository repository;

    @Autowired
    public WebController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "registration";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegistrationForm registrationForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }

        Optional<User> existingUser = repository.findByEmail(registrationForm.getEmail());
        if (existingUser != null) {
            model.addAttribute("message", "Ez az e-mail cím már regisztrálva van.");
            return "registration";
        }

        User newUser = new User(registrationForm.getName(), registrationForm.getEmail(), registrationForm.getPassword(), registrationForm.getRole());
        repository.save(newUser);
        return "login";
    }

    // Itt a login
    @GetMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }
    @PostMapping("/login")
    public String login(@ModelAttribute LoginForm loginForm, Model model) {
        Optional<User> userOpt = repository.findByName(loginForm.getUsername());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(loginForm.getPassword())) {
                // Sikeres bejelentkezés, átirányítás a főoldalra
                return "main";
            }
        }
        // Sikertelen bejelentkezés, visszatérés a bejelentkezési oldalra hibaüzenettel
        model.addAttribute("loginError", "Érvénytelen felhasználónév cím vagy jelszó");
        return "login";
    }

    @GetMapping("/main")
    public String showMainPage() {
        return "main";
    }

    @GetMapping("/postcard")
    public String showPostcardPage() {
        return "postcard";
    }
}
