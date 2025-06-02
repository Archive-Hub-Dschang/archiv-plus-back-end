package com.lde.academicservice.bootstrap;

import com.lde.academicservice.models.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.UUID;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(MongoTemplate mongoTemplate) {
        return args -> {
            // Nettoyage uniquement des collections qu'on initialise
            mongoTemplate.dropCollection(Department.class);
            mongoTemplate.dropCollection(Level.class);
            mongoTemplate.dropCollection(Program.class);
            mongoTemplate.dropCollection(Semester.class);
            mongoTemplate.dropCollection(Subject.class); // Ajout pour les matières

            // Department
            Department csDept = new Department(UUID.randomUUID().toString(), "Computer Science");
            mongoTemplate.save(csDept);

            // Levels
            Level l1 = new Level(UUID.randomUUID().toString(), "Licence 1");
            Level l2 = new Level(UUID.randomUUID().toString(), "Licence 2");
            mongoTemplate.save(l1);
            mongoTemplate.save(l2);

            // Program
            Program prog = new Program(UUID.randomUUID().toString(), "Informatique Générale", csDept.getId());
            mongoTemplate.save(prog);

            // Semesters
            Semester s1 = new Semester(UUID.randomUUID().toString(), "Semestre 1", prog.getId(), l1.getId());
            Semester s2 = new Semester(UUID.randomUUID().toString(), "Semestre 2", prog.getId(), l1.getId());
            mongoTemplate.save(s1);
            mongoTemplate.save(s2);

            // Subjects (matières)
            Subject subj1 = new Subject(UUID.randomUUID().toString(), "Programmation Java", s1.getId());
            Subject subj2 = new Subject(UUID.randomUUID().toString(), "Algèbre Linéaire", s1.getId());
            Subject subj3 = new Subject(UUID.randomUUID().toString(), "Structures de Données", s2.getId());
            Subject subj4 = new Subject(UUID.randomUUID().toString(), "Bases de Données", s2.getId());
            mongoTemplate.save(subj1);
            mongoTemplate.save(subj2);
            mongoTemplate.save(subj3);
            mongoTemplate.save(subj4);

            System.out.println("✅ Données initiales (y compris les matières) insérées avec succès !");
        };
    }
}
