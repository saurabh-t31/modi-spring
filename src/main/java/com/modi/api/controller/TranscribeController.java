package com.modi.api.controller;

import com.modi.api.dto.HealthResponse;
import com.modi.api.dto.TranscriptionResponse;
import com.modi.api.service.FastApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TranscribeController {

    private final FastApiService fastApiService;

    /**
     * POST /api/transcribe
     * React sends the manuscript image here as multipart form data.
     */
    @PostMapping("/transcribe")
    public ResponseEntity<TranscriptionResponse> transcribe(
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        log.info("Received file: {} ({} bytes)", file.getOriginalFilename(), file.getSize());

        try {
            TranscriptionResponse result = fastApiService.transcribe(file);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            log.error("Failed to read uploaded file", e);
            return ResponseEntity.internalServerError().build();
        } catch (RuntimeException e) {
            log.error("Transcription failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/health
     * Useful to check if FastAPI is reachable and model is loaded.
     */
    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        try {
            HealthResponse response = fastApiService.checkHealth();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("FastAPI health check failed: {}", e.getMessage());
            return ResponseEntity.status(502).build();
        }
    }
}
