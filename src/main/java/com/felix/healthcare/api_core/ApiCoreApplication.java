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
	public CommandLineRunner demoData(RoleRepository roleRepository) {
		return args -> {
			// Create and save roles
			roleRepository.saveAll(Arrays.asList(
					new Roles("admin"),
					new Roles("user")
			));
		};
	}

}
