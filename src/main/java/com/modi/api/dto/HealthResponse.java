package com.modi.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HealthResponse {

    @JsonProperty("status")
    private String status;

    @JsonProperty("model_loaded")
    private boolean modelLoaded;

    @JsonProperty("database_size")
    private int databaseSize;
}
