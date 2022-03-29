package com.tothenew.sharda.Controller;

import com.tothenew.sharda.Dto.LoginDao;
import com.tothenew.sharda.Dto.SignupCustomerDao;
import com.tothenew.sharda.Model.Role;
import com.tothenew.sharda.Model.User;
import com.tothenew.sharda.Repository.RoleRepository;
import com.tothenew.sharda.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginDao loginDao) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken
                        (loginDao.getEmail(), loginDao.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ResponseEntity<>("User signed-in successfully!", HttpStatus.OK);
    }

    @PostMapping("/signup/customer")
    public ResponseEntity<?> registerUser(@RequestBody SignupCustomerDao signupCustomerDao) {
        if (userRepository.existsByEmail(signupCustomerDao.getEmail())) {
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setFirstName(signupCustomerDao.getFirstName());
        user.setEmail(signupCustomerDao.getEmail());
        user.setPassword(passwordEncoder.encode(signupCustomerDao.getPassword()));
        user.setLastName(signupCustomerDao.getLastName());

        Role roles = roleRepository.findByAuthority("ROLE_CUSTOMER").get();
        user.setRoles(Collections.singleton(roles));

        userRepository.save(user);

        return new ResponseEntity<>("User Registered Successfully!", HttpStatus.CREATED);
    }
}