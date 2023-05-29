package com.commonlib.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailErrorDto {
    private String code;
    private String objectName;
    private String defaultMessage;
    private String field;
    private Object rejectedValue;
    private List<AdditionalDataDto> additionalData;
}

