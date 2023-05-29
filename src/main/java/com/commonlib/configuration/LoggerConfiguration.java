package com.commonlib.configuration;

import com.commonlib.dto.ServletDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LoggerConfiguration {

    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String RAW_PAYLOAD = "raw";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_JSON_UTF = "application/json; charset=utf-8";
    public static final int FIRST_INDEX_VALUE = 0;
    public static final int SIZE_SINGLE_VALUE = 1;

    @Autowired
    private ObjectMapper mapper;

    @Value("${commonlib-starter.log.request.exclude.required-field:}")
    private List<String> excludedRequiredField;

    @Value("${commonlib-starter.log.request.exclude.field:}")
    private List<String> excludedField;

    @Value("${commonlib-starter.log.request.exclude.param:}")
    private List<String> excludedParams;

    private static final String DELIMITER_CONTENT_TYPE = ";";

    /**
     * logRequest digunakan untuk melakukan ekstrasi HttpServletRequest ke structured logging dalam bentuk JSON
     *
     * @param request
     * @throws IOException
     */
    public ServletDto logRequest(HttpServletRequest request) throws IOException {
        HashMap payload = new HashMap<>();
        Map<Object, Object> requestParams = new HashMap<>();
        String payloadStr = "";
        if (POST.equalsIgnoreCase(request.getMethod())) {
            payloadStr = request.getReader().lines().collect(Collectors.joining(""));
            List<String> contentTypes = Arrays.stream(request.getContentType().split(DELIMITER_CONTENT_TYPE)).collect(Collectors.toList());
            if (contentTypes.contains(APPLICATION_JSON)) {
                payload = mapper.readValue(mapper.readTree(payloadStr).toString(), HashMap.class);
            } else {
                payload.put(RAW_PAYLOAD, payloadStr);
            }

            if (!excludedField.isEmpty()) {
                for (String field : excludedField) {
                    payload.replace(field, "[****]");
                }
            }

            for (String field: excludedRequiredField) {
                payload.replace(field, "[****]");
            }

        } else if (GET.equalsIgnoreCase(request.getMethod())) {
            Enumeration<String> parameterNames = request.getParameterNames();

            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                String[] paramValues = request.getParameterValues(paramName);
                requestParams.put(paramName, paramValues.length == SIZE_SINGLE_VALUE ? paramValues[FIRST_INDEX_VALUE] : Arrays.toString(paramValues));
            }

            if (!excludedParams.isEmpty()) {
                for (String field : excludedParams) {
                    requestParams.replace(field, "[****]");
                }
            }
        }

        Map<String, String> mapHeaders = new HashMap<>();
        Enumeration<String> e = request.getHeaderNames();
        while (e.hasMoreElements()) {
            String headers = e.nextElement();
            if (headers != null) {
                mapHeaders.put(headers, request.getHeader(headers));
            }
        }

        ServletDto servletDto = ServletDto.builder()
                .uri(request.getRequestURI())
                .remoteAddress(request.getRemoteAddr())
                .header(mapHeaders)
                .payloadSize(payloadStr.length())
                .payload(payload)
                .params(requestParams)
                .method(request.getMethod())
                .contentType(request.getContentType())
                .build();

        log.info(new ObjectAppendingMarker("request", servletDto), servletDto.toString());
        return servletDto;
    }
}
