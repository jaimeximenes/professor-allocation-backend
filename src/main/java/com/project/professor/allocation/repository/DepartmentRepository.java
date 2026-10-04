package com.project.professor.allocation.repository;

import com.project.professor.allocation.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findByNameContainsIgnoreCase(String name);
}
