package com.jarmdev.employeesjpa.controller;

import com.jarmdev.employeesjpa.entity.Employee;
import com.jarmdev.employeesjpa.request.EmployeeRequest;
import com.jarmdev.employeesjpa.service.EmployeeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee Rest API endpoints", description = "Operations related to employee")
public class EmployeeController {

    private final EmployeeServiceImpl employeeService;

    public EmployeeController(EmployeeServiceImpl employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all employees", description = "Retrieve a list of all employees")
    public List<Employee> findAll() {
        return employeeService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Fetch single employee", description = "Retrieve a employee by it's Id")
    public Employee findById(@PathVariable @Min(value = 1) Long id) {
        return employeeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Create a new employee", description = "Add a new employee to DB")
    public Employee add(@Valid @RequestBody EmployeeRequest employee) {
        return employeeService.save(employee);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a single employee", description = "Update the details of a employee in DB")
    public Employee update(@PathVariable @Min(value = 1) Long id, @Valid @RequestBody EmployeeRequest employee) {
        return employeeService.update(id, employee);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a employee", description = "Delete a employee by it's Id")
    public void delete(@PathVariable @Min(value = 1) Long id) {
        employeeService.deleteById(id);
    }
}
