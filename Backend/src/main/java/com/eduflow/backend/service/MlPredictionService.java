package com.eduflow.backend.service;

import com.eduflow.backend.dto.PredictionRequestDto;
import com.eduflow.backend.dto.PredictionResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MlPredictionService {

    @Value("${ml.service.url:http://localhost:8000}")
    private String mlServiceUrl;

    private final RestTemplate restTemplate;

    public MlPredictionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double getRecoveryProbability(PredictionRequestDto requestDto) {
        try {
            String url = mlServiceUrl + "/predict";
            PredictionResponseDto response = restTemplate.postForObject(url, requestDto, PredictionResponseDto.class);
            if (response != null) {
                return response.getRecovery_probability();
            }
        } catch (Exception e) {
            System.err.println("Failed to get prediction from ML service: " + e.getMessage());
        }
        return 0.0;
    }
}
