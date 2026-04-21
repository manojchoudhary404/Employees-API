package com.example.employeeapi.exception;

/**
 * Thrown when an Employee with the given ID is not found in the database.
 * Maps to HTTP 404 Not Found via GlobalExceptionHandler.
 */
public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(Long id) {
        super("Employee not found with id: " + id);
    }
}
