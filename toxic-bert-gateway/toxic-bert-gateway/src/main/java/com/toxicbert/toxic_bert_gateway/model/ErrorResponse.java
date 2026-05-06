package com.toxicbert.toxic_bert_gateway.model;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private long timestamp;
}