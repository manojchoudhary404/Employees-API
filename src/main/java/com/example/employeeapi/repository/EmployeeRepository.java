package com.example.employeeapi.repository;

import com.example.employeeapi.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for Employee.
 * Provides CRUD operations automatically — no implementation needed.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // JpaRepository already provides:
    // findAll(), findById(), save(), deleteById(), existsById(), count() etc.
}
