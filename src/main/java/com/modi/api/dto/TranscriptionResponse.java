package com.modi.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TranscriptionResponse {

    @JsonProperty("matched_index")
    private int matchedIndex;

    @JsonProperty("similarity")
    private double similarity;

    @JsonProperty("devanagari")
    private String devanagari;

    @JsonProperty("english")
    private String english;
}
