package com.toxicbert.toxic_bert_gateway.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnalyzeResult {

    private String text;

    private Map<String, Double> scores;

    @JsonProperty("is_toxic")
    private boolean isToxic;

    @JsonProperty("max_label")
    private String maxLabel;

    @JsonProperty("max_score")
    private double maxScore;

    @JsonProperty("latency_ms")
    private double latencyMs;

    @JsonProperty("original_text")
    private String originalText;

    @JsonProperty("translated_text")
    private String translatedText;

    @JsonProperty("was_translated")
    private boolean wasTranslated;
}