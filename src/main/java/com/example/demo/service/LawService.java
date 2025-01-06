package com.example.demo.service;

import com.example.demo.model.Law;
import com.example.demo.repository.LawRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LawService {

    @Autowired
    private LawRepository lawRepository;

    @Autowired
    private EmbeddingService embeddingService;


    @Autowired
    private CosineSimilarity cosineSimilarity;

    // Phương thức tính embedding cho tất cả các điều luật và cập nhật vào CSDL
    public void updateAllLawEmbeddings() {
        List<Law> Law = lawRepository.findAll();  // Lấy tất cả các điều luật từ CSDL
        for (Law law : Law) {
            String embedding = embeddingService.getEmbeddingForLaw(law.getContent());  // Tính embedding cho nội dung của điều luật
            if (embedding != null) {
                law.setEmbedding(embedding);  // Cập nhật embedding vào đối tượng Law
                lawRepository.save(law);  // Lưu lại vào CSDL
            }
        }
    }
    public List<Law> searchLawsByEmbedding(String queryEmbedding) {
        List<Law> Law = lawRepository.findAll();
        List<Law> similarLaws = new ArrayList<>();

        for (Law law : Law) {
            // Tính cosine similarity giữa query và embedding của điều luật
            double similarity = cosineSimilarity.computeSimilarity(queryEmbedding, law.getEmbedding());

            // Thêm điều luật vào danh sách nếu độ tương đồng lớn hơn một ngưỡng nào đó
            if (similarity > 0.8) { // Ví dụ, độ tương đồng > 0.8
                similarLaws.add(law);
            }
        }
        return similarLaws;
    }
}
