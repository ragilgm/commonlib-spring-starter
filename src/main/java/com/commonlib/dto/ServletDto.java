package com.commonlib.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Builder
@ToString
@Data
@AllArgsConstructor
public class ServletDto {
    private String uri;
    private String remoteAddress;
    private Map header;
    private Map params;
    private Map payload;
    private int payloadSize;
    private String method;
    private String contentType;
}
