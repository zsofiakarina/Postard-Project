package project.intalk.PostcardProject.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;
import project.intalk.PostcardProject.personas.Role;

public class RegistrationForm {

    @Email
    private String email;

    @NotNull(message = "Provide valid data!")
    @Size(min = 6, max = 12)
    private String password;

    @NotNull(message = "Provide valid data!")
    @Size(min = 4, max = 18)
    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
