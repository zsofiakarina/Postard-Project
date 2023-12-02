package project.intalk.PostcardProject.model;

import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;

public class RegistrationForm {

    @Email
    @Length(min = 3)
    private String email;

    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
