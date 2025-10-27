package br.ueg.appgenesis.security.adapter.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProblemDetailAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;

    public ProblemDetailAccessDeniedHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       org.springframework.security.access.AccessDeniedException accessDeniedException) throws IOException {

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        pd.setTitle("Forbidden");
        pd.setDetail("Acesso negado");
        pd.setProperty("code", "access_denied");

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/problem+json");
        mapper.writeValue(response.getOutputStream(), pd);
    }
}
