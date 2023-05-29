package com.commonlib.configuration;

import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import javax.validation.constraints.NotNull;
import java.io.IOException;

public class RestTemplateInterceptorConfiguration implements ClientHttpRequestInterceptor {
    private String cookie;

    @Override
    public ClientHttpResponse intercept(@NotNull HttpRequest httpRequest, byte[] body, @NotNull ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        if (cookie != null) {
            httpRequest.getHeaders().add(HttpHeaders.COOKIE, cookie);
        }

        httpRequest.getHeaders().add(CorrelationConfiguration.CORRELATION_ID_HEADER_NAME, MDC.get(CorrelationConfiguration.CORRELATION_ID_LOG_VAR_NAME));

        var response = clientHttpRequestExecution.execute(httpRequest, body);

        if (cookie == null) {
            cookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        }
        return response;
    }
}
