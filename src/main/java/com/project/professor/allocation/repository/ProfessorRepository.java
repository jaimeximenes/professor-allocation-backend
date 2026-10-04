package com.project.professor.allocation.repository;

import com.project.professor.allocation.entity.Department;
import com.project.professor.allocation.entity.Professor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    @EntityGraph(attributePaths = { "department" })
    List<Professor> findAll();

    List<Professor> findByNameContainingIgnoreCase(String name);

    List<Professor> findByDepartment(Department department);
}
