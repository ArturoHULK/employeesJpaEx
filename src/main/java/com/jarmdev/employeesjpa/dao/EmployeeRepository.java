package com.jarmdev.employeesjpa.dao;

import com.jarmdev.employeesjpa.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
