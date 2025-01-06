package com.example.demo.service;
import com.example.demo.model.Law;
import com.example.demo.repository.LawRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LawSearchService {

    @Autowired
    private LawRepository lawRepository;

    @Autowired
    private EmbeddingService embeddingService;

    public List<Law> searchLaws(String query) {
        // Lấy embedding cho văn bản tìm kiếm
        String queryEmbedding = embeddingService.getEmbeddingForLaw(query);

        // Lấy tất cả các điều luật từ cơ sở dữ liệu
        List<Law> Law = lawRepository.findAll();

        // Tính toán độ tương đồng cosine (có thể sử dụng một phương thức hỗ trợ tính cosine similarity)
        // Sắp xếp các điều luật theo độ tương đồng với truy vấn
   //     laws.sort((law1, law2) -> {
     //       double similarity1 = calculateCosineSimilarity(queryEmbedding, law1.getEmbedding());
    //        double similarity2 = calculateCosineSimilarity(queryEmbedding, law2.getEmbedding());
     //       return Double.compare(similarity2, similarity1); // Sắp xếp giảm dần
    //    });

        return Law;
    }


    public List<String> getAllEmbeddings() {
        List<Law> allLaws = lawRepository.findAll();
        // Trả về danh sách các embedding dưới dạng List<String>
        return allLaws.stream()
                .map(Law::getEmbedding)  // Lấy embedding của từng điều luật
                .collect(Collectors.toList());
    }



}
