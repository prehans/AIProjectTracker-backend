package com.SpringProject.Repository;

import com.SpringProject.Entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>{
    @Override
    List<Project> findAll();
}
