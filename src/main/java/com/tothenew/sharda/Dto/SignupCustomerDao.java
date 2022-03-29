package com.tothenew.sharda.Dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class SignupCustomerDao {
    private String firstName;
    private String lastName;

    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\\\.[a-z]{2,3}", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Email should be unique and valid")
    @NotEmpty(message = "Email cannot be empty")
    private String email;


    @NotEmpty
    private String password;
    private String confirmPassword;

    @Size(min = 10, max = 10, message = "Phone number must be of 10 digits")
    @NotEmpty
    private String phoneNumber;
}