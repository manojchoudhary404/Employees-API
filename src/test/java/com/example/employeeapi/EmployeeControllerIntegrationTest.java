package com.example.employeeapi;

import com.example.employeeapi.model.Employee;
import com.example.employeeapi.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("EmployeeController Integration Tests")
class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee savedEmployee;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        savedEmployee = employeeRepository.save(new Employee("John Doe", "Developer", 50000.0));
    }

    @Test
    @DisplayName("GET /employees - returns list of employees")
    void testGetAllEmployees() throws Exception {
        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name", is("John Doe")));
    }

    @Test
    @DisplayName("GET /employees/{id} - returns employee")
    void testGetById() throws Exception {
        mockMvc.perform(get("/employees/" + savedEmployee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.role", is("Developer")));
    }

    @Test
    @DisplayName("GET /employees/{id} - returns 404 when not found")
    void testGetById_NotFound() throws Exception {
        mockMvc.perform(get("/employees/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @DisplayName("POST /employees - creates and returns 201")
    void testCreateEmployee() throws Exception {
        Employee newEmployee = new Employee("Jane Smith", "Manager", 75000.0);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmployee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id",   notNullValue()))
                .andExpect(jsonPath("$.name", is("Jane Smith")));
    }

    @Test
    @DisplayName("POST /employees - returns 400 for invalid body")
    void testCreateEmployee_Invalid() throws Exception {
        Employee bad = new Employee("", "", -1.0);  // blank name/role, negative salary

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors", notNullValue()));
    }

    @Test
    @DisplayName("PUT /employees/{id} - updates employee")
    void testUpdateEmployee() throws Exception {
        Employee update = new Employee("John Updated", "Senior Dev", 90000.0);

        mockMvc.perform(put("/employees/" + savedEmployee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name",   is("John Updated")))
                .andExpect(jsonPath("$.salary", is(90000.0)));
    }

    @Test
    @DisplayName("DELETE /employees/{id} - deletes and returns 204")
    void testDeleteEmployee() throws Exception {
        mockMvc.perform(delete("/employees/" + savedEmployee.getId()))
                .andExpect(status().isNoContent());

        // Verify it's gone
        mockMvc.perform(get("/employees/" + savedEmployee.getId()))
                .andExpect(status().isNotFound());
    }
}
