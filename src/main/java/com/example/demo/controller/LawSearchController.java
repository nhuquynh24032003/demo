package com.example.demo.controller;
import com.example.demo.service.EmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/laws")
public class LawSearchController {
    @Autowired
    private EmbeddingService embeddingService;
    @PostMapping("/update-all-embeddings")
    public String updateAllLawEmbeddings() {
        embeddingService.updateAllLawEmbeddings();
        return "Embeddings for all laws updated successfully";
    }
    @GetMapping("/search")
    public List<Map<String, Object>> searchLaws(@RequestParam String query) {
        return embeddingService.searchLawsByQueryEmbedding(query);
    }
}
