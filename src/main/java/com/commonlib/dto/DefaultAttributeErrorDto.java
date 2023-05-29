package com.commonlib.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * {
 *     "timestamp": "2021-06-03T10:49:10.498+00:00",
 *     "status": 400,
 *     "error": "Bad Request",
 *     "message": "Validation failed for object='transactionRequestDto'. Error count: 1",
 *     "path": "/payment/transactions"
 * }
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultAttributeErrorDto {
    private int status;
    private String message;
    private String error;
    private String path;
    private String timestamp;
    private List<DetailErrorDto> details;
}
