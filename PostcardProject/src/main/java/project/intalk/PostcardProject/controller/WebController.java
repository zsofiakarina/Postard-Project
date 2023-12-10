package project.intalk.PostcardProject.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import project.intalk.PostcardProject.domain.LoginForm;
import project.intalk.PostcardProject.domain.PostcardForm;
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
    public String showRegistrationForm(HttpSession session, Model model) {
        if (session.getAttribute("username") != null) {
            // A felhasználó már bejelentkezett, átirányítás a főoldalra
            return "redirect:/main";
        }
        model.addAttribute("registrationForm", new RegistrationForm());
        return "registration";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegistrationForm registrationForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registrationError", "Kérjük, töltse ki az összes mezőt.");
            return "registration";
        }

        Optional<User> existingName = repository.findByName(registrationForm.getName());
        if (existingName.isPresent()) {
            model.addAttribute("registrationError", "Ez a felhasználónév már foglalt..");
            return "registration";
        }

        Optional<User> existingEmail = repository.findByEmail(registrationForm.getEmail());
        if (existingEmail.isPresent()) {
            model.addAttribute("registrationError", "Ez az e-mail cím már regisztrálva van.");
            return "registration";
        }

        User newUser = new User(registrationForm.getName(), registrationForm.getEmail(), registrationForm.getPassword(), registrationForm.getRole());
        repository.save(newUser);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm(HttpSession session, Model model) {
        if (session.getAttribute("username") != null) {
            return "redirect:/main";
        }
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginForm loginForm, HttpSession session, Model model) {
        Optional<User> userOpt = repository.findByName(loginForm.getName());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(loginForm.getPassword())) {
                // Sikeres bejelentkezés, átirányítás a főoldalra
                session.setAttribute("username", user.getName());
                return "redirect:/main";
            }
        }
        // Sikertelen bejelentkezés, visszatérés a bejelentkezési oldalra hibaüzenettel
        model.addAttribute("loginError", "Érvénytelen felhasználónév vagy jelszó");
        return "login";
    }

    @GetMapping("/main")
    public String showMainPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);
        return "main";
    }

    @GetMapping("/postcard")
    public String showPostcardForm(Model model) {
        model.addAttribute("postcard", new PostcardForm());
        return "postcard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
