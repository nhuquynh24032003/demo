package com.example.demo.service;


import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.springframework.stereotype.Service;

@Service
public class CosineSimilarity {

    public double computeSimilarity(String embedding1, String embedding2) {
        // Chuyển đổi embedding thành các vector số
        RealVector vector1 = convertToVector(embedding1);
        RealVector vector2 = convertToVector(embedding2);

        // Kiểm tra nếu một trong hai vector có kích thước 0
        if (vector1.getDimension() == 0 || vector2.getDimension() == 0) {
            System.err.println("One of the vectors is empty. Cannot compute similarity.");
            return 0.0;
        }

        // Kiểm tra nếu kích thước của cả hai vector không đồng nhất
        if (vector1.getDimension() != vector2.getDimension()) {
            throw new IllegalArgumentException("Vectors must have the same dimension.");
        }

        // Tính cosine similarity
        double dotProduct = vector1.dotProduct(vector2);
        double magnitude1 = vector1.getNorm();
        double magnitude2 = vector2.getNorm();

        // Tránh chia cho 0 nếu độ dài vector bằng 0
        if (magnitude1 == 0 || magnitude2 == 0) {
            return 0.0;
        }

        // Tính cosine similarity
        return dotProduct / (magnitude1 * magnitude2);
    }


    private RealVector convertToVector(String embedding) {
        if (embedding == null || embedding.trim().isEmpty()) {
            throw new IllegalArgumentException("Embedding string is null or empty.");
        }
        try {
            // Loại bỏ dấu ngoặc vuông và chia chuỗi theo dấu phẩy
            embedding = embedding.replace("[", "").replace("]", "");

            // Chia chuỗi thành mảng các chuỗi số
            String[] embeddingValues = embedding.split(",");

            // Kiểm tra nếu không có giá trị nào trong mảng
            if (embeddingValues.length == 0) {
                throw new IllegalArgumentException("Embedding contains no values.");
            }

            // Chuyển đổi các chuỗi thành mảng các giá trị double
            double[] vectorValues = new double[embeddingValues.length];
            for (int i = 0; i < embeddingValues.length; i++) {
                vectorValues[i] = Double.parseDouble(embeddingValues[i].trim());  // Chuyển chuỗi thành double
            }

            return new ArrayRealVector(vectorValues);
        } catch (Exception e) {
            // In ra lỗi chi tiết hơn
            System.err.println("Error converting embedding to vector: " + e.getMessage());
            return new ArrayRealVector(new double[0]);  // Trả về vector rỗng nếu có lỗi
        }
    }


}