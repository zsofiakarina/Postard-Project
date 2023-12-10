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
import project.intalk.PostcardProject.personas.Postcard;
import project.intalk.PostcardProject.personas.User;
import project.intalk.PostcardProject.repository.PostcardRepository;
import project.intalk.PostcardProject.repository.UserRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

@Controller
public class WebController {

    private static final Logger logger = Logger.getLogger(WebController.class.getName());

    private final UserRepository userRepository;
    private final PostcardRepository postcardRepository;

    @Autowired
    public WebController(UserRepository userRepository, PostcardRepository postcardRepository) {
        this.userRepository = userRepository;
        this.postcardRepository = postcardRepository;
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
            model.addAttribute("registrationAllError", "Please fill all fields to register!");
            return "registration";
        }

        Optional<User> existingName = userRepository.findByName(registrationForm.getName());
        if (existingName.isPresent()) {
            model.addAttribute("registrationUserError", "Please choose another username!");
            return "registration";
        }

        Optional<User> existingEmail = userRepository.findByEmail(registrationForm.getEmail());
        if (existingEmail.isPresent()) {
            model.addAttribute("registrationEmailError", "Please use another email address or sign in!");
            return "registration";
        }

        User newUser = new User(registrationForm.getName(), registrationForm.getEmail(), registrationForm.getPassword(), registrationForm.getRole());
        userRepository.save(newUser);
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
        Optional<User> userOpt = userRepository.findByName(loginForm.getName());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(loginForm.getPassword())) {
                // Sikeres bejelentkezés, átirányítás a főoldalra
                session.setAttribute("username", user.getName());
                return "redirect:/main";
            }
        }
        // Sikertelen bejelentkezés, visszatérés a bejelentkezési oldalra hibaüzenettel
        model.addAttribute("loginError", "Invalid username or password!");
        return "login";
    }

    @GetMapping("/main")
    public String showMainPage(HttpSession session, Model model) {
        if (session.getAttribute("username") == null) {
            return "redirect:/login";
        }
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);

        // Fetch the user details
        Optional<User> userOpt = userRepository.findByName(username);
        List<Postcard> postcards;
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("userRole", user.getRole().toString());
            // Check if user is an admin
            if ("ADMIN".equals(user.getRole())) {
                // Admin: fetch all postcards
                postcards = postcardRepository.findAll();
                logger.info("Number of postcards fetched: " + postcards.size());
            } else {
                // Regular user: fetch only their postcards
                postcards = postcardRepository.findByName(username);
                logger.info("Number of postcards fetched: " + postcards.size());
            }
        } else {
            // Handle error case if user not found
            postcards = Collections.emptyList();
        }

        model.addAttribute("postcards", postcards);

        User user = userOpt.get();
        logger.info("User role: " + user.getRole());


        return "main";
    }


    @GetMapping("/postcard")
    public String showPostcardForm(HttpSession session, Model model) {
        if (session.getAttribute("username") == null) {
            return "redirect:/login";
        }
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);
        model.addAttribute("postcardForm", new PostcardForm());
        return "postcard";
    }

    @PostMapping("/createPostcard")
    public String createPostcard(@ModelAttribute PostcardForm postcardForm, HttpSession session) {
        String username = (String) session.getAttribute("username");
        Postcard postcard = new Postcard();
        postcard.setRecipient(postcardForm.getRecipient());
        postcard.setMessage(postcardForm.getMessage());
        postcard.setName(username);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String formattedDate = dateFormat.format(Calendar.getInstance().getTime());
        postcard.setDatetime(formattedDate);
        postcard.setStatus("pending");
        postcardRepository.save(postcard);
        return "redirect:/main";
    }

/*    @GetMapping("/gallery")
    public String showGalleryPage(HttpSession session, Model model) {
        if (session.getAttribute("username") == null) {
            return "redirect:/login";
        }
        String username = (String) session.getAttribute("username");
        List<Postcard> postcards = postcardRepository.findByName(username);
        model.addAttribute("postcards", postcards);
        return "gallery";
    }*/

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
