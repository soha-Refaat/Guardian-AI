package com.toxicbert.toxic_bert_gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.List;
import java.util.Map;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class VideoAnalyzeResult {

    // Whisper
    private String transcript;
    private List<TranscriptChunk> chunks;

    // toxic-bert
    private Map<String, Double> scores;

    @JsonProperty("is_toxic")
    private boolean isToxic;

    @JsonProperty("max_label")
    private String maxLabel;

    @JsonProperty("max_score")
    private double maxScore;

    // ترجمة
    @JsonProperty("original_text")
    private String originalText;

    @JsonProperty("translated_text")
    private String translatedText;

    @JsonProperty("was_translated")
    private boolean wasTranslated;

    // أوقات التنفيذ
    @JsonProperty("whisper_ms")
    private double whisperMs;

    @JsonProperty("toxicity_ms")
    private double toxicityMs;

    @JsonProperty("total_ms")
    private double totalMs;
}