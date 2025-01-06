package com.example.demo.controller;


import com.example.demo.service.LawSearchService;
import com.example.demo.service.LawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/laws")
public class LawController {

    @Autowired
    private LawSearchService lawEmbeddingService;
    @Autowired
    private LawService lawService;

    // API để lấy tất cả các embedding từ cơ sở dữ liệu
    @GetMapping("/embeddings")
    public List<String> getAllEmbeddings() {
        return lawEmbeddingService.getAllEmbeddings();
    }

    @PostMapping("/update-all-embeddings")
    public String updateAllLawEmbeddings() {
        lawService.updateAllLawEmbeddings();
        return "Embeddings for all laws updated successfully";
    }
}