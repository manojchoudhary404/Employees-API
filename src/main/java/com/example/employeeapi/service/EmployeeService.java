package com.example.employeeapi.service;

import com.example.employeeapi.exception.EmployeeNotFoundException;
import com.example.employeeapi.model.Employee;
import com.example.employeeapi.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer — contains all business logic for Employee operations.
 */
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    // Constructor injection (recommended over @Autowired field injection)
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Retrieve all employees.
     */
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * Retrieve a single employee by ID.
     * Throws EmployeeNotFoundException if not found.
     */
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    /**
     * Create a new employee and persist it.
     */
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    /**
     * Update an existing employee's fields by ID.
     * Throws EmployeeNotFoundException if not found.
     */
    public Employee updateEmployee(Long id, Employee updatedData) {
        Employee existing = getEmployeeById(id);
        existing.setName(updatedData.getName());
        existing.setRole(updatedData.getRole());
        existing.setSalary(updatedData.getSalary());
        return employeeRepository.save(existing);
    }

    /**
     * Delete an employee by ID.
     * Throws EmployeeNotFoundException if not found.
     */
    public void deleteEmployee(Long id) {
        Employee existing = getEmployeeById(id);
        employeeRepository.delete(existing);
    }
}
