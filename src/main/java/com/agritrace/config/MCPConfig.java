package com.agritrace.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class MCPConfig {

    @Bean
    public FilterRegistrationBean<MCPTraceFilter> mcpTraceFilter() {
        FilterRegistrationBean<MCPTraceFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new MCPTraceFilter());
        registration.addUrlPatterns("/mcp/skill/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return registration;
    }

    public static class MCPTraceFilter implements Filter {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            HttpServletRequest req = (HttpServletRequest) request;
            long start = System.currentTimeMillis();
            try {
                chain.doFilter(request, response);
            } finally {
                long duration = System.currentTimeMillis() - start;
                if (duration > 300) {
                    System.out.printf("[MCP] %s executed in %d ms (exceeded 300ms threshold)%n",
                            req.getRequestURI(), duration);
                }
            }
        }
    }
}
