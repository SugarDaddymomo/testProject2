package com.tothenew.sharda;

import com.tothenew.sharda.Model.Role;
import com.tothenew.sharda.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootEcommerceApiApplication implements CommandLineRunner {

	@Autowired
	RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootEcommerceApiApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		Role adminRole = new Role();
		adminRole.setAuthority("ROLE_ADMIN");

		Role userRole = new Role();
		userRole.setAuthority("ROLE_CUSTOMER");

		Role sellerRole = new Role();
		sellerRole.setAuthority("ROLE_SELLER");

		roleRepository.save(adminRole);
		roleRepository.save(userRole);
		roleRepository.save(sellerRole);
	}
}
