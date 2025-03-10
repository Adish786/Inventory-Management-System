package com.security;

import com.security.entity.Role;
import com.security.entity.User;
import com.security.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SecurityServiceApplication implements CommandLineRunner {
	private UserRepository userRepository;

	public SecurityServiceApplication(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(SecurityServiceApplication.class, args);
	}
	@Override
	public void run(String... args) throws Exception {
		User adminAcc = userRepository.findByRole(Role.ADMIN);

		if(adminAcc == null){
			User user = new User();
			user.setFirstname("admin");
			user.setLastName("admin");
			user.setEmail("admin@gmail.com");
			user.setRole(Role.ADMIN);
			user.setPassword(new BCryptPasswordEncoder().encode("admin"));
		}
	}
}
