package com.example.demo.controller;

import com.example.demo.service.EmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmbeddingController {

    @Autowired
    private EmbeddingService embeddingService;

    @GetMapping("/get-embedding")
    public String getEmbedding() {
        return embeddingService.getEmbedding();
    }
}
