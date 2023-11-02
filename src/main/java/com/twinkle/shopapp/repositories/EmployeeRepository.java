package com.twinkle.shopapp.repositories;

import com.twinkle.shopapp.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findEmployeeByUserId(long id);
}
