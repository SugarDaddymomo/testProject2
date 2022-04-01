package com.tothenew.sharda.Controller;

import com.tothenew.sharda.RegistrationConfig.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    RegistrationService registrationService;

    @GetMapping("/all")
    public String allAccess() {
        return "Public Content";
    }

    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public String userAccess() {
        return "Customer Content.";
    }

    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public String moderatorAccess() {
        return "Seller Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }

    @GetMapping(path = "/seller/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    public String confirmSeller(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }
}