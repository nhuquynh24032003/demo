package com.example.demo.repository;

import com.example.demo.model.Law;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LawRepository extends JpaRepository<Law, Integer> {
    List<Law> findAll();
}
