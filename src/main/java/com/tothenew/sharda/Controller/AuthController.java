package com.tothenew.sharda.Controller;

import com.tothenew.sharda.Dto.LoginDao;
import com.tothenew.sharda.Dto.SignupCustomerDao;
import com.tothenew.sharda.Dto.SignupSellerDao;
import com.tothenew.sharda.Email.EmailSender;
import com.tothenew.sharda.Model.Customer;
import com.tothenew.sharda.Model.Role;
import com.tothenew.sharda.Model.Seller;
import com.tothenew.sharda.Model.User;
import com.tothenew.sharda.RegistrationConfig.RegistrationService;
import com.tothenew.sharda.RegistrationConfig.Token.ConfirmationTokenService;
import com.tothenew.sharda.Repository.CustomerRepository;
import com.tothenew.sharda.Repository.RoleRepository;
import com.tothenew.sharda.Repository.SellerRepository;
import com.tothenew.sharda.Repository.UserRepository;
import com.tothenew.sharda.Service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    CustomUserDetailsService userService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    SellerRepository sellerRepository;
    @Autowired
    RegistrationService registrationService;
    @Autowired
    EmailSender emailSender;

    @PostMapping("/signin")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginDao loginDao) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken
                        (loginDao.getEmail(), loginDao.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ResponseEntity<>("User signed-in successfully!", HttpStatus.OK);
    }

    @PostMapping("/customer/signup")
    public ResponseEntity<?> registerAsCustomer(@Valid @RequestBody SignupCustomerDao signupCustomerDao) {
        if (userRepository.existsByEmail(signupCustomerDao.getEmail())) {
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setFirstName(signupCustomerDao.getFirstName());
        user.setEmail(signupCustomerDao.getEmail());
        user.setPassword(passwordEncoder.encode(signupCustomerDao.getPassword()));
        user.setLastName(signupCustomerDao.getLastName());

        Customer customer = new Customer(user, signupCustomerDao.getContact());

        Role roles = roleRepository.findByAuthority("ROLE_CUSTOMER").get();
        user.setRoles(Collections.singleton(roles));

        userRepository.save(user);
        customerRepository.save(customer);

        String token = registrationService.generateToken(user);

        String link = "http://localhost:4545/api/auth/customer/confirm?token="+token;
        emailSender.send(signupCustomerDao.getEmail(), registrationService.buildEmail(signupCustomerDao.getFirstName(), link));
        return new ResponseEntity<>(
                "Customer Registered Successfully!\nHere is your activation token use it with in 15 minutes\n"+token,
                HttpStatus.CREATED
        );
    }

    @PostMapping("/seller/signup")
    public ResponseEntity<?> registerAsSeller(@Valid @RequestBody SignupSellerDao signupSellerDao) {
        if (userRepository.existsByEmail(signupSellerDao.getEmail())) {
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setFirstName(signupSellerDao.getFirstName());
        user.setEmail(signupSellerDao.getEmail());
        user.setPassword(passwordEncoder.encode(signupSellerDao.getPassword()));
        user.setLastName(signupSellerDao.getLastName());

        Seller seller = new Seller(user, signupSellerDao.getGstNumber(), signupSellerDao.getCompanyContact(), signupSellerDao.getCompanyName());

        Role roles = roleRepository.findByAuthority("ROLE_SELLER").get();
        user.setRoles(Collections.singleton(roles));

        userRepository.save(user);
        sellerRepository.save(seller);

        String token = registrationService.generateToken(user);

        String link = "http://localhost:4545/api/auth/seller/confirm?token="+token;
        emailSender.send(signupSellerDao.getEmail(), registrationService.buildEmail(signupSellerDao.getFirstName(), link));

        return new ResponseEntity<>(
                "Seller Registered Successfully!\nAsk you Admin to activate your account!",
                HttpStatus.CREATED);
    }

    @PutMapping(path = "/customer/confirm")
    public String confirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }

    @GetMapping(path = "/seller/confirm")
    public String confirmSeller(@RequestParam("token") String token) {
        return "Oops! You cannot activate this account, Contact Admin to get approval!!";
    }
}