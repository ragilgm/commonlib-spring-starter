package com.commonlib.dto;

import lombok.*;

import java.util.Map;

@Builder
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
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
