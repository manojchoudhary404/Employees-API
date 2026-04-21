package com.example.employeeapi;

import com.example.employeeapi.model.Employee;
import com.example.employeeapi.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Seeds the H2 database with sample data when the application starts.
 * Useful for testing all endpoints immediately via Postman.
 */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(EmployeeRepository repository) {
        return args -> {
            repository.save(new Employee("John Doe",    "Developer",      50000.0));
            repository.save(new Employee("Jane Smith",  "Manager",        75000.0));
            repository.save(new Employee("Bob Johnson", "QA Engineer",    45000.0));
            repository.save(new Employee("Alice Brown", "DevOps",         60000.0));
            repository.save(new Employee("Charlie Lee", "Data Analyst",   55000.0));
            System.out.println("✅  Sample employees loaded into H2 database.");
        };
    }
}
