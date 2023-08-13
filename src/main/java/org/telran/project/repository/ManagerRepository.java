package org.telran.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.telran.project.entity.Manager;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {
}