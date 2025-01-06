package com.example.demo.controller;
import com.example.demo.model.Law;
import com.example.demo.service.EmbeddingService;
import com.example.demo.service.LawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/laws")
public class LawSearchController {

    @Autowired
    private LawService lawService;
    @Autowired
    private EmbeddingService embeddingService;

    @GetMapping("/query")
    public Map<String, Object> getEmbeddingForQuery(@RequestParam String query) {
        try {
            // Lấy embedding cho truy vấn người dùng
            String embedding = embeddingService.getEmbeddingForQuery(query);
            if (embedding == null) {
                return Map.of("error", "Failed to retrieve embedding for the query.");
            }
            return Map.of("embedding", embedding);  // Trả về embedding cho người dùng
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "An error occurred while processing the request.");
        }
    }
    @GetMapping("/search")
    public List<Law> searchLaws(@RequestParam String query) {
        return embeddingService.searchLawsByQueryEmbedding(query);
    }
}
