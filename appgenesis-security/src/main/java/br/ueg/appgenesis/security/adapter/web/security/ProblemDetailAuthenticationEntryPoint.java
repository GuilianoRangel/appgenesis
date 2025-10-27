package br.ueg.appgenesis.security.adapter.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProblemDetailAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;

    public ProblemDetailAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String code = (authException.getMessage() == null) ? "" : authException.getMessage().toLowerCase();
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        pd.setTitle("Unauthorized");

        switch (code) {
            case "token_expired" -> { pd.setDetail("Token expirado"); pd.setProperty("code","token_expired"); }
            case "token_invalid" -> { pd.setDetail("Token inválido"); pd.setProperty("code","token_invalid"); }
            default -> { pd.setDetail("Falha de autenticação"); pd.setProperty("code","unauthorized"); }
        }

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/problem+json");
        mapper.writeValue(response.getOutputStream(), pd);
    }
}
