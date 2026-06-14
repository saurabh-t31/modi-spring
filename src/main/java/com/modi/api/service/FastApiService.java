package com.modi.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modi.api.dto.HealthResponse;
import com.modi.api.dto.TranscriptionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class FastApiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${fastapi.base-url}")
    private String fastApiBaseUrl;

    /**
     * Forwards the uploaded image to FastAPI /transcribe
     * and returns the Devanagari + English result.
     */
    public TranscriptionResponse transcribe(MultipartFile file) throws IOException {

        // Build multipart request body — FastAPI expects field name "file"
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        String url = fastApiBaseUrl + "/transcribe";
        log.info("Forwarding image to FastAPI: {}", url);

        try {
            // Read as raw bytes first — avoids RestTemplate guessing the wrong
            // charset (e.g. ISO-8859-1) when decoding the JSON response body,
            // which corrupts Devanagari conjuncts.
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    byte[].class
            );

            log.info("FastAPI responded with status: {}", response.getStatusCode());

            byte[] rawBody = response.getBody();
            if (rawBody == null) {
                throw new RuntimeException("Empty response from FastAPI");
            }

            String json = new String(rawBody, StandardCharsets.UTF_8);
            log.debug("Raw FastAPI JSON (UTF-8): {}", json);

            return objectMapper.readValue(json, TranscriptionResponse.class);

        } catch (HttpClientErrorException e) {
            log.error("FastAPI client error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("FastAPI rejected the request: " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            log.error("FastAPI server error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("FastAPI internal error: " + e.getResponseBodyAsString());
        }
    }

    /**
     * Checks if FastAPI is up and model is loaded.
     */
    public HealthResponse checkHealth() {
        String url = fastApiBaseUrl + "/health";
        log.info("Checking FastAPI health at: {}", url);

        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
        byte[] rawBody = response.getBody();
        if (rawBody == null) {
            throw new RuntimeException("Empty response from FastAPI health check");
        }

        try {
            String json = new String(rawBody, StandardCharsets.UTF_8);
            return objectMapper.readValue(json, HealthResponse.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse health response", e);
        }
    }
}