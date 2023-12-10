package project.intalk.PostcardProject.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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

    // Az autowired annotációval automatikusan behúzza a szükséges függőségeket.
    @Autowired
    public WebController(UserRepository userRepository, PostcardRepository postcardRepository) {
        this.userRepository = userRepository;
        this.postcardRepository = postcardRepository;
    }

    // Regisztrációs funkciók

    @GetMapping("/register")
    public String showRegistrationForm(HttpSession session, Model model) {
        // Ha a felhasználó már bejelentkezett, akkor átirányítjuk a főoldalra
        if (session.getAttribute("username") != null) {
            return "redirect:/main";
        }
        // Új regisztrációs formot adunk a modellhez
        model.addAttribute("registrationForm", new RegistrationForm());
        return "registration";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegistrationForm registrationForm, BindingResult bindingResult, Model model) {
        // Hibakezelés: ellenőrzi, hogy a regisztrációs űrlap megfelelően van-e kitöltve
        if (bindingResult.hasErrors()) {
            model.addAttribute("registrationAllError", "Please fill all fields to register!");
            return "registration";
        }

        // Ellenőrzi, hogy a felhasználónév és az email cím már létezik-e az adatbázisban
        if (userRepository.findByName(registrationForm.getName()).isPresent()) {
            model.addAttribute("registrationUserError", "Please choose another username!");
            return "registration";
        }
        if (userRepository.findByEmail(registrationForm.getEmail()).isPresent()) {
            model.addAttribute("registrationEmailError", "Please use another email address or sign in!");
            return "registration";
        }

        // Új felhasználó létrehozása és mentése az adatbázisban
        User newUser = new User(registrationForm.getName(), registrationForm.getEmail(), registrationForm.getPassword(), registrationForm.getRole());
        userRepository.save(newUser);
        return "redirect:/login";
    }

    // Bejelentkezési funkciók

    @GetMapping("/login")
    public String showLoginForm(HttpSession session, Model model) {
        // Ha a felhasználó már bejelentkezett, akkor átirányítjuk a főoldalra
        if (session.getAttribute("username") != null) {
            return "redirect:/main";
        }
        // Bejelentkezési űrlap hozzáadása a modellhez
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginForm loginForm, HttpSession session, Model model) {
        // Ellenőrzi, hogy a felhasználó létezik-e és a jelszó megfelelő-e
        Optional<User> userOpt = userRepository.findByName(loginForm.getName());
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(loginForm.getPassword())) {
            // Sikeres bejelentkezés esetén beállítja a felhasználónevet a munkamenetben
            session.setAttribute("username", userOpt.get().getName());
            return "redirect:/main";
        }
        // Sikertelen bejelentkezés esetén hibaüzenetet jelenít meg
        model.addAttribute("loginError", "Invalid username or password!");
        return "login";
    }

    // Főoldal és képeslapok megjelenítése

    @GetMapping("/main")
    public String showMainPage(HttpSession session, Model model) {
        // Ha a felhasználó nincs bejelentkezve, átirányítja a bejelentkezési oldalra
        if (session.getAttribute("username") == null) {
            return "redirect:/login";
        }

        // Lekéri a bejelentkezett felhasználó nevét és hozzáadja a modellhez
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);

        // Lekéri a felhasználó adatait és ellenőrzi a szerepét
        Optional<User> userOpt = userRepository.findByName(username);
        List<Postcard> postcards;
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("userRole", user.getRole().toString());

            // Admin esetén az összes képeslapot lekéri, egyéb esetben csak a jóváhagyottakat
            if ("ADMIN".equals(user.getRole())) {
                postcards = postcardRepository.findAll();
            } else {
                postcards = postcardRepository.findByStatusAndName("approved", username);
            }
            logger.info("Number of postcards fetched: " + postcards.size());
        } else {
            // Ha a felhasználó nem található, üres listát ad vissza
            postcards = Collections.emptyList();
        }

        // A képeslapok listájának hozzáadása a modellhez
        model.addAttribute("postcards", postcards);
        return "main";
    }

    // Képeslap státuszának frissítése

    @PostMapping("/updateStatus/{postcardId}/{newStatus}")
    public String updatePostcardStatus(@PathVariable Long postcardId, @PathVariable String newStatus, HttpSession session) {
        // Lekéri a képeslapot az adatbázisból az azonosító alapján
        Optional<Postcard> postcardOpt = postcardRepository.findById(postcardId);
        if (postcardOpt.isPresent()) {
            // Frissíti a képeslap státuszát és menti az adatbázisban
            Postcard postcard = postcardOpt.get();
            postcard.setStatus(newStatus);
            postcardRepository.save(postcard);
        }
        return "redirect:/main";
    }

    // Képeslap létrehozása

    @GetMapping("/postcard")
    public String showPostcardForm(HttpSession session, Model model) {
        // Ha a felhasználó nincs bejelentkezve, átirányítja a bejelentkezési oldalra
        if (session.getAttribute("username") == null) {
            return "redirect:/login";
        }
        // Képeslap létrehozó űrlap hozzáadása a modellhez
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);
        model.addAttribute("postcardForm", new PostcardForm());
        return "postcard";
    }

    @PostMapping("/createPostcard")
    public String createPostcard(@ModelAttribute PostcardForm postcardForm, HttpSession session) {
        // Létrehoz egy új képeslapot a form adatai alapján
        String username = (String) session.getAttribute("username");
        Postcard postcard = new Postcard();
        postcard.setRecipient(postcardForm.getRecipient());
        postcard.setMessage(postcardForm.getMessage());
        postcard.setName(username);

        // Beállítja a jelenlegi dátumot és időt
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String formattedDate = dateFormat.format(Calendar.getInstance().getTime());
        postcard.setDatetime(formattedDate);
        postcard.setStatus("pending");

        // Mentés az adatbázisban
        postcardRepository.save(postcard);
        return "redirect:/main";
    }

    // Kijelentkezési funkció

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Megszünteti a munkamenetet és átirányít a bejelentkezési oldalra
        session.invalidate();
        return "redirect:/login";
    }

    // Gyökér URL kezelése és átirányítás a főoldalra
    @GetMapping("/")
    public String redirectToMain() {
        return "redirect:/main";
    }
}
