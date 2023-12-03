package project.intalk.PostcardProject.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LoginForm {

    @NotNull(message = "Provide valid data!")
    @Size(min = 4, max = 18)
    private String name;

    @NotNull(message = "Provide valid data!")
    @Size(min = 6, max = 12)
    private String password;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
