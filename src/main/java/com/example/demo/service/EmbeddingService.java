package com.example.demo.service;
import com.example.demo.model.Law;
import com.example.demo.repository.LawRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmbeddingService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LawRepository lawRepository;
    @Autowired
    private CosineSimilarity cosineSimilarity;

    public String getEmbedding() {
        // URL API của Ollama
        String url = "http://localhost:11434/api/embeddings";

        // Tạo payload
        String jsonPayload = "{" +
                "\"model\": \"mxbai-embed-large\"," +
                "\"prompt\": \"Represent this sentence for searching relevant passages: The sky is blue because of Rayleigh scattering\"" +
                "}";

        // Tạo đối tượng HttpHeaders
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Tạo đối tượng HttpEntity với dữ liệu và headers
        HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

        // Gửi yêu cầu POST
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // Trả về kết quả trả về từ API
        return response.getBody();
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

    public List<Law> searchLawsByQueryEmbedding(String query) {
        // Lấy embedding của query từ API Ollama
        String queryEmbedding = getEmbeddingForQuery(query);

        // Lấy tất cả các embedding từ CSDL
        List<Law> Law = lawRepository.findAll();

        // Tính cosine similarity với mỗi embedding trong CSDL và lưu kết quả
        List<LawWithSimilarity> results = new ArrayList<>();
        System.out.println(queryEmbedding);
        for (Law law : Law) {

            System.out.println(law.getEmbedding());

            //double similarity = cosineSimilarity.computeSimilarity(queryEmbedding, law.getEmbedding());
           // results.add(new LawWithSimilarity(law, similarity));
        }

        // Sắp xếp theo similarity giảm dần
    //    results.sort(Comparator.comparingDouble(LawWithSimilarity::getSimilarity).reversed());

        // Trả về danh sách các luật đã sắp xếp
       // return results.stream().map(LawWithSimilarity::getLaw).collect(Collectors.toList());
        return Law;
    }

    public class LawWithSimilarity {
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
