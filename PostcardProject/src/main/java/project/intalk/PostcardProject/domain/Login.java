package project.intalk.PostcardProject.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Login {

    @NotNull(message = "Provide valid data!")
    @Size(min = 4, max = 18)
    private String username;

    @NotNull(message = "Provide valid data!")
    @Size(min = 6, max = 12)
    private String password;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
