package com.felix.healthcare.api_core;

import com.felix.healthcare.api_core.dto.UsersDto;
import com.felix.healthcare.api_core.entity.Roles;
import com.felix.healthcare.api_core.entity.Users;
import com.felix.healthcare.api_core.repository.RoleRepository;
import com.felix.healthcare.api_core.repository.UserRepository;
import com.felix.healthcare.api_core.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@EnableAsync
@EnableCaching
public class ApiCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiCoreApplication.class, args);
	}

	@Bean
	public CommandLineRunner demoData(RoleRepository roleRepository, UserRepository userRepository) {
		return args -> {
			// Create and save roles
			roleRepository.saveAll(Arrays.asList(
					new Roles("admin"),
					new Roles("user")
			));

			Roles adminRole = roleRepository.findByName("admin");
			Roles userRole = roleRepository.findByName("user");
			System.out.println("role admin : " + adminRole.getId() + ", role user : " + userRole.getId());

			Set<Roles> rolesAdmin = new HashSet<>();
			rolesAdmin.add(adminRole);

			Set<Roles> rolesUser = new HashSet<>();
			rolesAdmin.add(userRole);

			String hashPassword = BCrypt.hashpw("password", BCrypt.gensalt(12));

			Users userAdmin = new Users();
			userAdmin.setUsername("admin");
			userAdmin.setEmail("admin@test.com");
			userAdmin.setPassword(hashPassword);
			userAdmin.setRoles(rolesAdmin);

			Users userUser = new Users();
			userUser.setUsername("user");
			userUser.setEmail("user@test.com");
			userUser.setPassword(hashPassword);
			userUser.setRoles(rolesUser);

			userRepository.saveAll(Arrays.asList(
					userAdmin,
					userUser
			));

//			Users adminUser = userRepository.save(userAdmin);
			System.out.println("admin email : " + userAdmin.getEmail() + ", user email : " + userUser.getEmail());
		};
	}

}
