package com.jarmdev.employeesjpa.service;

import com.jarmdev.employeesjpa.entity.Employee;
import com.jarmdev.employeesjpa.request.EmployeeRequest;

import java.util.List;

public interface EmployeeService {
    List<Employee> findAll();
    Employee findById(Long id);
    Employee save(EmployeeRequest request);
    Employee update(Long id, EmployeeRequest request);
    Employee convertToEmployee(Long id, EmployeeRequest request);
    void deleteById(Long id);
}
