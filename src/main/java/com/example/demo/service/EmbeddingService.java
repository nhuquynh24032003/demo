package com.example.demo.service;
import com.example.demo.model.Law;
import com.example.demo.repository.LawRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmbeddingService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LawRepository lawRepository;
    @Autowired
    private CosineSimilarity cosineSimilarity;
    public void updateAllLawEmbeddings() {
        List<Law> Law = lawRepository.findAll();  // Lấy tất cả các điều luật từ CSDL
        for (Law law : Law) {
            String embedding = getEmbeddingForLaw(law.getContent());  // Tính embedding cho nội dung của điều luật
            if (embedding != null) {
                law.setEmbedding(embedding);  // Cập nhật embedding vào đối tượng Law
                lawRepository.save(law);  // Lưu lại vào CSDL
            }
        }
    }

    public String getEmbeddingForLaw(String lawContent) {
        // URL của API Ollama
        String url = "http://localhost:11434/api/embeddings";

        // Tạo payload cho yêu cầu
        String jsonPayload = "{" +
                "\"model\": \"mxbai-embed-large\"," +
                "\"prompt\": \"" + lawContent + "\"" +
                "}";

        // Tạo HttpHeaders
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Tạo HttpEntity với dữ liệu và headers
        HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

        // Gửi yêu cầu POST và nhận phản hồi
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // Trả về nội dung của phản hồi (embedding vector)
        return response.getBody();
    }

    public String getEmbeddingForQuery(String query) {
        // URL của API Ollama
        String url = "http://localhost:11434/api/embeddings";  // Sử dụng URL của API Ollama

        // Tạo payload cho yêu cầu
        String jsonPayload = "{" +
                "\"model\": \"mxbai-embed-large\"," +
                "\"prompt\": \"" + query + "\"" +
                "}";

        // Tạo HttpHeaders
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Tạo HttpEntity với dữ liệu và headers
        HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

        // Gửi yêu cầu POST và nhận phản hồi
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // Giải mã chuỗi JSON để lấy embedding
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode embeddingNode = rootNode.path("embedding");  // Lấy mảng embedding từ JSON

            // Trả về mảng embedding dưới dạng chuỗi JSON
            return embeddingNode.toString();  // Trả về embedding dưới dạng chuỗi
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, Object>> searchLawsByQueryEmbedding(String query) {
        // Lấy embedding của query từ API Ollama
        String queryEmbeddingJson = getEmbeddingForQuery(query);
        List<Double> queryEmbedding = convertJsonToList(queryEmbeddingJson);

        // Lấy tất cả các embedding từ CSDL
        List<Law> laws = lawRepository.findAll();

        // Tính cosine similarity với mỗi embedding trong CSDL và lưu kết quả
        List<LawWithSimilarity> results = new ArrayList<>();

        for (Law law : laws) {
            List<Double> lawEmbedding = convertJsonToList2(law.getEmbedding());

            double similarity = cosineSimilarity.computeSimilarity(queryEmbedding, lawEmbedding);

            results.add(new LawWithSimilarity(law, similarity));
            System.out.println(law.getTitle());
            System.out.println(similarity);
        }

        // Sắp xếp theo similarity giảm dần
        results.sort(Comparator.comparingDouble(LawWithSimilarity::getSimilarity).reversed());

        // Trả về danh sách các luật chỉ chứa id, title và content
        return results.stream()
                .map(result -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", result.getLaw().getId());
                    map.put("title", result.getLaw().getTitle());
                    map.put("content", result.getLaw().getContent());
                    return map;
                })
                .collect(Collectors.toList());


    }

    private List<Double> convertJsonToList(String jsonString) {
        try {
            // Sử dụng ObjectMapper để chuyển đổi mảng JSON thành List<Double>
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonString, new TypeReference<List<Double>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to List<Double>: " + e.getMessage(), e);
        }
    }

    private List<Double> convertJsonToList2(String jsonString) {
        try {
            // Sử dụng ObjectMapper để đọc trường "embedding"
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, List<Double>> map = objectMapper.readValue(jsonString, new TypeReference<Map<String, List<Double>>>() {});
            return map.get("embedding");
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to List<Double>: " + e.getMessage(), e);
        }

    }
    public static class LawWithSimilarity {
        private Law law;
        private double similarity;

        public LawWithSimilarity(Law law, double similarity) {
            this.law = law;
            this.similarity = similarity;
        }

        public Law getLaw() {
            return law;
        }

        public double getSimilarity() {
            return similarity;
        }
    }
}
