package com.toxicbert.toxic_bert_gateway.model;

import lombok.*;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class TranscriptChunk {
    private double start;
    private double end;
    private String text;
}