package com.lde.usermicroservice;

import com.lde.usermicroservice.models.RoleName;
import com.lde.usermicroservice.models.User;
import com.lde.usermicroservice.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;



@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class UsermicroserviceApplication implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(UsermicroserviceApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(UsermicroserviceApplication.class, args);
	}

	@Bean
	public CommandLineRunner initUsers(UserService userService, PasswordEncoder passwordEncoder) {
		return args -> {
			log.info("--- Initialisation des utilisateurs de base ---");

			// Créer un utilisateur admin si non existant
			if (userService.getByEmail("admin@example.com").isEmpty()) {
				User adminUser = User.builder()
						.username("admin")
						.email("admin@example.com")
						.password(passwordEncoder.encode("adminpassword"))
						.roles(RoleName.Admin) // Attribuez directement le RoleName
						.build();
				userService.saveUser(adminUser);
				log.info("Utilisateur admin créé: admin@example.com");
			} else {
				log.info("Utilisateur admin (admin@example.com) existe déjà.");
			}

			// Créer un utilisateur standard si non existant
			if (userService.getByEmail("user@example.com").isEmpty()) {
				User normalUser = User.builder()
						.username("user")
						.email("user@example.com")
						.password(passwordEncoder.encode("userpassword"))
						.roles(RoleName.User) // Attribuez directement le RoleName
						.build();
				userService.saveUser(normalUser);
				log.info("Utilisateur normal créé: user@example.com");
			} else {
				log.info("Utilisateur normal (user@example.com) existe déjà.");
			}
			// Créer un collaborateur si non existant
			if (userService.getByEmail("collaborateur@example.com").isEmpty()) {
				userService.saveUser(  User.builder()
						.username("collaborateur")
						.email("collaborateur@example.com")
						.password(passwordEncoder.encode("collaborateurpassword"))
						.roles(RoleName.Collaborateur) // Attribuez directement le RoleName
						.build());
				log.info("Collaborateur créé:collaborateur@example.com");
			} else {
				log.info("Collaborateur (collaborateur@example.com) existe déjà.");
			}
			log.info("--- Fin d'initialisation ---");
		};
}

	@Override
	public void run(String... args) throws Exception {

	}
}
