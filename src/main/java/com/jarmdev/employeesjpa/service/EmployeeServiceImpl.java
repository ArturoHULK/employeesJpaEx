package com.jarmdev.employeesjpa.service;

import com.jarmdev.employeesjpa.dao.EmployeeRepository;
import com.jarmdev.employeesjpa.entity.Employee;
import com.jarmdev.employeesjpa.request.EmployeeRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee findById(Long id) {
        return employeeRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Did not find employee id - " + id));
    }

    @Transactional
    @Override
    public Employee save(EmployeeRequest request) {
        return employeeRepository.save(convertToEmployee(0L, request));
    }

    @Transactional
    @Override
    public Employee update(Long id, EmployeeRequest request) {
        return employeeRepository.save(convertToEmployee(id, request));
    }

    @Override
    public Employee convertToEmployee(Long id, EmployeeRequest request) {
        return new Employee(id,
                request.getFirstName(),
                request.getLastName(),
                request.getEmail());
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        employeeRepository.deleteById(id);
    }


}
