package project.intalk.PostcardProject.controller;

import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import project.intalk.PostcardProject.model.RegistrationForm;
import project.intalk.PostcardProject.personas.User;
import project.intalk.PostcardProject.repository.UserRepository;

import java.util.logging.Logger;

@Controller
public class WebController {

    private Logger logger = (Logger) LoggerFactory.getLogger(WebController.class);

    private final UserRepository repository;

    @Autowired
    public WebController(UserRepository repository) {
        this.repository = repository;
    }

    public WebController(Logger logger, UserRepository repository) {
        this.logger = logger;
        this.repository = repository;
    }

    @GetMapping("/")
    public String showRegistrationForm(RegistrationForm registrationForm) {

        return "registration";
    }

    @PostMapping("/")
    public String register(@Valid RegistrationForm registrationForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.info("Validation errors occurred!");
            return "registration";
        }

        logger.info("Registering user with email: {}");
        final boolean userIsRegistered = repository.findByEmail(registrationForm.getEmail()).isPresent();
        if (!userIsRegistered) {
            repository.save(new User(registrationForm.getEmail()));
        }

        return "main";
    }
}
