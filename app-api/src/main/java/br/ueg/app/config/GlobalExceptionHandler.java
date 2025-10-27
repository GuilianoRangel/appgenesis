package br.ueg.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ueg.appgenesis.core.domain.error.BusinessException;
import br.ueg.appgenesis.core.domain.error.DomainException;
import br.ueg.appgenesis.core.domain.error.ErrorCode;
import br.ueg.appgenesis.core.domain.error.NotFoundException;
import br.ueg.appgenesis.core.domain.security.DomainAccessDeniedException;
import br.ueg.appgenesis.core.domain.security.DomainAuthenticationException;
import br.ueg.appgenesis.core.domain.validation.DomainValidationException;
import br.ueg.appgenesis.core.domain.validation.DomainViolation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String BASE_TYPE = "https://triprequest.dev/errors/";

    private ProblemDetail base(HttpStatus status, String typeSuffix, String title, String detail,
                               String code, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(URI.create(BASE_TYPE + typeSuffix));
        pd.setInstance(URI.create(req.getRequestURI()));
        pd.setProperty("code", code);
        pd.setProperty("traceId", UUID.randomUUID().toString().replace("-", ""));
        return pd;
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex, HttpServletRequest req) {
        log.warn("NotFoundException: {} - Code: {} - Instance: {}", ex.getMessage(), ex.getCode().value(), req.getRequestURI());
        return base(HttpStatus.NOT_FOUND, "not-found", "Resource not found",
                ex.getMessage(), ex.getCode().value(), req);
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusiness(BusinessException ex, HttpServletRequest req) {
        log.warn("BusinessException: {} - Code: {} - Instance: {}", ex.getMessage(), ex.getCode().value(), req.getRequestURI());
        return base(HttpStatus.UNPROCESSABLE_ENTITY, "business-rule", "Business rule violated",
                ex.getMessage(), ex.getCode().value(), req);
    }

    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomain(DomainException ex, HttpServletRequest req) {
        log.warn("DomainException: {} - Code: {} - Instance: {}", ex.getMessage(), ex.getCode().value(), req.getRequestURI());
        return base(HttpStatus.BAD_REQUEST, "domain", "Domain error",
                ex.getMessage(), ex.getCode().value(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest req) {
        log.warn("MethodArgumentNotValidException: {} - Instance: {} - Errors: {}", ex.getMessage(), req.getRequestURI(), ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).toList());
        ProblemDetail pd = base(HttpStatus.BAD_REQUEST, "validation", "Request validation failed",
                "Há campos inválidos.", ErrorCode.VALIDATION.value(), req);
        List<Map<String, Object>> errs = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("field", fe.getField());
            m.put("message", fe.getDefaultMessage());
            m.put("rejectedValue", fe.getRejectedValue());
            errs.add(m);
        }
        pd.setProperty("errors", errs);
        return pd;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        log.warn("ConstraintViolationException: {} - Instance: {} - Violations: {}", ex.getMessage(), req.getRequestURI(), ex.getConstraintViolations().stream().map(v -> v.getPropertyPath().toString() + ": " + v.getMessage()).toList());
        ProblemDetail pd = base(HttpStatus.BAD_REQUEST, "validation", "Request validation failed",
                "Violação de constraint.", ErrorCode.VALIDATION.value(), req);
        List<Map<String, Object>> errs = new ArrayList<>();
        ex.getConstraintViolations().forEach(v -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("field", v.getPropertyPath().toString());
            m.put("message", v.getMessage());
            m.put("rejectedValue", v.getInvalidValue());
            errs.add(m);
        });
        pd.setProperty("errors", errs);
        return pd;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        log.warn("MethodArgumentTypeMismatchException: {} - Parameter: {} - Value: {} - Instance: {}", ex.getMessage(), ex.getName(), ex.getValue(), req.getRequestURI());
        String detail = "Parâmetro inválido: " + ex.getName();
        return base(HttpStatus.BAD_REQUEST, "type-mismatch", "Type mismatch",
                detail, ErrorCode.VALIDATION.value(), req);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ProblemDetail handleNotReadable(Exception ex, HttpServletRequest req) {
        log.warn("HttpMessageNotReadableException: {} - Instance: {}", ex.getMessage(), req.getRequestURI());
        return base(HttpStatus.BAD_REQUEST, "malformed-json", "Malformed JSON",
                "Corpo da requisição inválido.", ErrorCode.VALIDATION.value(), req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("DataIntegrityViolationException: {} - Instance: {}", ex.getMessage(), req.getRequestURI());
        return base(HttpStatus.CONFLICT, "data-integrity", "Data integrity violation",
                "Operação não permitida por integridade de dados.", ErrorCode.CONFLICT.value(), req);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Internal Server Error: {} - Instance: {}", ex.getMessage(), req.getRequestURI(), ex);
        return base(HttpStatus.INTERNAL_SERVER_ERROR, "internal", "Internal error",
                "Ocorreu um erro inesperado.", ErrorCode.INTERNAL.value(), req);
    }

    @ExceptionHandler(DomainValidationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) // 422
    public ProblemDetail handleDomainValidation(DomainValidationException ex, WebRequest request) {
        log.warn("DomainValidationException: {} - Violations: {}", ex.getMessage(), ex.getViolations().stream().map(v -> v.getField() + ": " + v.getMessage()).toList());
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        pd.setTitle("Violação de regras de negócio");
        pd.setDetail("Um ou mais campos violam as regras do domínio.");
        pd.setType(URI.create("about:blank"));

        List<Map<String, String>> errors = new ArrayList<>();
        for (DomainViolation v : ex.getViolations()) {
            Map<String, String> e = new LinkedHashMap<>();
            e.put("field", v.getField());
            e.put("message", v.getMessage());
            e.put("code", v.getCode());
            errors.add(e);
        }
        pd.setProperty("errors", errors);
        return pd;
    }

    // 401 do domínio
    @ExceptionHandler(DomainAuthenticationException.class)
    public ProblemDetail handleDomainAuth(DomainAuthenticationException ex, HttpServletRequest req) {
        Object violations = ex.getViolations() != null ? ex.getViolations().stream().map(v -> v.getField() + ": " + v.getMessage()).toList() : "N/A";
        log.warn("DomainAuthenticationException: {} - Violations: {} - URL: {} - IP: {}", ex.getMessage(), violations, req.getRequestURI(), req.getRemoteAddr());
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        pd.setTitle("Unauthorized");

        String code = normalize(ex.getMessage());
        switch (code) {
            case "token_expired" -> {
                pd.setDetail("Token expirado");
                pd.setProperty("code", "token_expired");
            }
            case "token_invalid" -> {
                pd.setDetail("Token inválido");
                pd.setProperty("code", "token_invalid");
            }
            case "refresh_invalid" -> {
                pd.setDetail("Refresh token inválido");
                pd.setProperty("code", "refresh_invalid");
            }
            case "refresh_expired_or_revoked" -> {
                pd.setDetail("Refresh token expirado ou revogado");
                pd.setProperty("code", "refresh_expired_or_revoked");
            }
            case "user_inactive" -> {
                pd.setDetail("Usuário inativo");
                pd.setProperty("code", "user_inactive");
            }
            default -> pd.setDetail("Falha de autenticação");
        }

        List<DomainViolation> v = ex.getViolations();
        if (v != null && !v.isEmpty()) {
            pd.setProperty("violations", v.stream().map(this::toViolation).toList());
        }
        return pd;
    }

    // 403 do domínio
    @ExceptionHandler(DomainAccessDeniedException.class)
    public ProblemDetail handleDomainAccess(DomainAccessDeniedException ex) {
        log.warn("DomainAccessDeniedException: {} - Required Permission: {}", ex.getMessage(), ex.getPermission());
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        pd.setTitle("Forbidden");
        pd.setDetail("Acesso negado");
        pd.setProperty("requiredPermission", ex.getPermission());
        pd.setProperty("code", "access_denied");
        return pd;
    }



    // Harmonização com Spring Security (se escaparem do filtro)
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ProblemDetail handleSpring403(org.springframework.security.access.AccessDeniedException ex, HttpServletRequest req) {
        log.warn("Spring Security AccessDeniedException: {} - URL: {} - IP: {}", ex.getMessage(), req.getRequestURI(), req.getRemoteAddr());
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        pd.setTitle("Forbidden");
        pd.setDetail("Acesso negado");
        pd.setProperty("code", "access_denied");
        return pd;
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ProblemDetail handleSpring401(org.springframework.security.core.AuthenticationException ex, HttpServletRequest req) {
        log.warn("Spring Security AuthenticationException: {} - URL: {} - IP: {}", ex.getMessage(), req.getRequestURI(), req.getRemoteAddr());
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        pd.setTitle("Unauthorized");
        pd.setDetail("Falha de autenticação");
        pd.setProperty("code", "unauthorized");
        return pd;
    }

    /* ---------- helpers ---------- */

    private List<Object> toViolationPayload(List<DomainViolation> violations) {
        // Gera uma lista simples para o JSON: [{field, message, rule}]
        return Collections.singletonList(violations.stream()
                .map(v -> Map.of(
                        "field", v.getField(),    // ex.: "username"
                        "message", v.getMessage(),  // ex.: "usuário inválido"
                        "rule", v.getCode()      // ex.: "Auth" ou nome da anotação
                ))
                .toList());
    }

    /* helpers */
    private Map<String,Object> toViolation(DomainViolation v) {
        return Map.of("field", v.getField(), "message", v.getMessage(), "rule", v.getCode());
    }
    private String normalize(String s) { return s == null ? "" : s.trim().toLowerCase(); }

}
