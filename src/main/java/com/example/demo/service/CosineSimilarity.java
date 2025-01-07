package com.example.demo.service;


import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CosineSimilarity {


    public double computeSimilarity(List<Double> vectorA, List<Double> vectorB) {
        if (vectorA == null || vectorB == null || vectorA.size() != vectorB.size()) {
            throw new IllegalArgumentException("Vectors must not be null and must have the same size");
        }

        RealVector vecA = new ArrayRealVector(vectorA.toArray(new Double[0]));
        RealVector vecB = new ArrayRealVector(vectorB.toArray(new Double[0]));

        return vecA.cosine(vecB); // Sử dụng phương thức cosine từ Apache Commons Math
    }

}