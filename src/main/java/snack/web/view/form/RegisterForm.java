package snack.web.view.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import snack.config.Constants;

public class RegisterForm {

    @NotBlank
    @Length(min = Constants.USERNAME_MIN_LENGTH, max = Constants.USERNAME_MAX_LENGTH)
    private String username;

    @NotBlank
    @Length(min = Constants.PASSWORD_MIN_LENGTH, max = Constants.PASSWORD_MAX_LENGTH)
    private String password;

    @NotBlank
    @Email
    @Length(max = Constants.EMAIL_MAX_LENGTH)
    private String email;

    @NotBlank
    @Length(max = Constants.NAME_MAX_LENGTH)
    private String firstName;

    @NotBlank
    @Length(max = Constants.NAME_MAX_LENGTH)
    private String lastName;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUsername(String username) {
        this.username = username.strip();
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
