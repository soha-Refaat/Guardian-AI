package com.toxicbert.toxic_bert_gateway.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BatchAnalyzeResult {

    private List<AnalyzeResult> results;

    @JsonProperty("total_latency_ms")
    private double totalLatencyMs;
}