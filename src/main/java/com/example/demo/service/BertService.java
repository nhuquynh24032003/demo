package com.example.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class BertService {

    @Value("${huggingface.api.token}")
    private String huggingFaceToken;

    private final RestTemplate restTemplate;

    public BertService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getBertEmbedding(String query) {
        String url = "https://api-inference.huggingface.co/models/bert-base-uncased";

        // Cấu hình HTTP Headers với token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + huggingFaceToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Tạo payload cho yêu cầu
        String payload = "{ \"inputs\": \"" + query + "\" }";

        // Tạo HttpEntity
        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        // Gửi yêu cầu POST tới Hugging Face API
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // Giải mã kết quả trả về từ API
        try {
            JsonNode rootNode = new ObjectMapper().readTree(response.getBody());
            return rootNode.path(0).path("embedding").toString(); // Lấy embedding
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
