package com.commonlib.configuration;

import com.commonlib.dto.ServletDto;
import com.commonlib.service.CustomServletService;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;


@Order(3)
@Component
public class CorrelationConfiguration extends OncePerRequestFilter {

    public static final String CORRELATION_ID_LOG_VAR_NAME = "correlation_id";
    public static final String CORRELATION_ID_HEADER_NAME = "x-correlation-id";

    @Value("${astrapay-starter.log.request.enabled:true}")
    private boolean logRequest;

    @Autowired
    private LoggerConfiguration loggerConfiguration;

    @Autowired
    private CustomServletService customServletService;

    /**
     * FilterInternal ini digunakan untuk
     * <ul>
     *     <li>Melakukan <i>caching</i>  HttpServletRequest sehingga dapat digunakan pada interceptor berikutnya</li>
     *     <li>Melakukan generate CorrelationID sebagai tracking ID pada setiap log yang dilakukan</li>
     *     <li>Melakukan <i>automatic</i> logging request. Log berupa structured logging dalam bentuk JSON</li>
     * </ul>
     *
     * Log Request dapat dimatikan melalui properties <u>astrapay-starter.log.request.enabled</u>
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )
            throws ServletException, IOException {
        CachedBodyHttpServletRequestConfiguration cachedBodyHttpServletRequest = new CachedBodyHttpServletRequestConfiguration(request);
        // Get correlation ID (or generate if not present)
        final String requestCorrelationId = extractOrGenerateCorrelationId(cachedBodyHttpServletRequest);

        // Set logging MDC
        MDC.put(CORRELATION_ID_LOG_VAR_NAME, requestCorrelationId);

        if (this.logRequest) {
            ServletDto servletDto = loggerConfiguration.logRequest(cachedBodyHttpServletRequest);
             customServletService.setServletDto(servletDto);
            cachedBodyHttpServletRequest.setServletDto(servletDto);
        }

        // Carry on using existing spring filter chain
        filterChain.doFilter(cachedBodyHttpServletRequest, response);
    }

    /**
     * Get correction ID from current request or generate new correlation ID
     *
     * @param {@link HttpServletRequest} to extract correlation ID from
     *
     * @return {@link String} of the correction ID
     */
    private String extractOrGenerateCorrelationId(final HttpServletRequest request) {
        String headerCorrelationId = request.getHeader(
                CORRELATION_ID_HEADER_NAME
        );

        // Check if header correlation ID is null or empty string
        return ObjectUtils.isEmpty(headerCorrelationId)
                ? UUID.randomUUID().toString()
                : headerCorrelationId;
    }
}
