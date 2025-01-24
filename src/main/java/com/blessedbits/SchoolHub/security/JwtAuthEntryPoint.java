package com.blessedbits.SchoolHub.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String message = (String) request.getAttribute("exception");
        if (message == null) {
            message = authException.getMessage();
        }
        String jsonResponse = String.format("{\"message\": \"%s\"}", message);

        response.resetBuffer();
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getOutputStream().print(jsonResponse);
        response.flushBuffer();
    }
}
