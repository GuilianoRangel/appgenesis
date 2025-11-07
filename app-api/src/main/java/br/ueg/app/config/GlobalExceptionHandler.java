package br.ueg.app.config;

import br.ueg.appgenesis.core.domain.error.*;
import br.ueg.appgenesis.core.domain.security.DomainUnauthenticationException;
import br.ueg.appgenesis.security.adapter.web.security.TokenExpiredAuthException;
import br.ueg.appgenesis.security.adapter.web.security.TokenInvalidAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ueg.appgenesis.core.domain.security.DomainAccessDeniedException;
import br.ueg.appgenesis.core.domain.security.DomainAuthenticationException;
import br.ueg.appgenesis.core.domain.validation.DomainValidationException;
import br.ueg.appgenesis.core.domain.validation.DomainViolation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.text.MessageFormat;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String BASE_TYPE = "https://triprequest.dev/errors/";

    private ResponseEntity<ProblemDetail> base(HttpStatus status, String typeSuffix, String title, String detail,
                               String code, HttpServletRequest req) {
        ProblemDetail pd = new ProblemDetail();
        pd.setStatus(status.value());
        pd.setTitle(title);
        pd.setType(String.valueOf(URI.create(BASE_TYPE + typeSuffix)));
        pd.setInstance(String.valueOf(URI.create(req.getRequestURI())));
        pd.setCode(code);
        pd.setTraceId(UUID.randomUUID().toString().replace("-", ""));
        List<ProblemProperties> errs = new ArrayList<>();

        ProblemProperties prob = new ProblemProperties(
                "detail_error",
                detail,
                null);
        errs.add(prob);
        pd.setProperties(errs);

        return  ResponseEntity.status(status).body(pd);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        log.warn("NotFoundException: {} - Code: {} - Instance: {}", ex.getMessage(), ex.getCode().value(), req.getRequestURI());
        return base(HttpStatus.NOT_FOUND, "not-found", "Resource not found",
                ex.getMessage(), ex.getCode().value(), req);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusiness(BusinessException ex, HttpServletRequest req) {
        log.warn("BusinessException: {} - Code: {} - Instance: {}", ex.getMessage(), ex.getCode().value(), req.getRequestURI());
        return base(HttpStatus.UNPROCESSABLE_ENTITY, "business-rule", "Business rule violated",
                ex.getMessage(), ex.getCode().value(), req);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ProblemDetail> handleDomain(DomainException ex, HttpServletRequest req) {
        log.warn("DomainException: {} - Code: {} - Instance: {}", ex.getMessage(), ex.getCode().value(), req.getRequestURI());
        return base(HttpStatus.BAD_REQUEST, "domain", "Domain error",
                ex.getMessage(), ex.getCode().value(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest req) {
        log.warn("MethodArgumentNotValidException: {} - Instance: {} - Errors: {}", ex.getMessage(), req.getRequestURI(), ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).toList());
        var pd = base(HttpStatus.BAD_REQUEST, "validation", "Request validation failed",
                "Há campos inválidos.", ErrorCode.VALIDATION.value(), req);
        List<ProblemProperties> errs = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            ProblemProperties prob = new ProblemProperties(
                    fe.getField(),
                    fe.getDefaultMessage(),
                    MessageFormat.format("rejectedValue: {0}", fe.getRejectedValue()));
            errs.add(prob);
        }

        pd.getBody().setProperties(errs);
        return pd;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        log.warn("ConstraintViolationException: {} - Instance: {} - Violations: {}", ex.getMessage(), req.getRequestURI(), ex.getConstraintViolations().stream().map(v -> v.getPropertyPath().toString() + ": " + v.getMessage()).toList());
        var pd = base(HttpStatus.BAD_REQUEST, "validation", "Request validation failed",
                "Violação de constraint.", ErrorCode.VALIDATION.value(), req);
        List<ProblemProperties> errs = new ArrayList<>();
        ex.getConstraintViolations().forEach(v -> {
            ProblemProperties m = new ProblemProperties(
                    v.getPropertyPath().toString(),
                    v.getMessage(),
                    "rejectedValue:"+v.getInvalidValue().toString()
            );

            errs.add(m);
        });
        pd.getBody().setProperties(errs);
        return pd;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        log.warn("MethodArgumentTypeMismatchException: {} - Parameter: {} - Value: {} - Instance: {}", ex.getMessage(), ex.getName(), ex.getValue(), req.getRequestURI());
        String detail = "Parâmetro inválido: " + ex.getName();
        return base(HttpStatus.BAD_REQUEST, "type-mismatch", "Type mismatch",
                detail, ErrorCode.VALIDATION.value(), req);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleNotReadable(Exception ex, HttpServletRequest req) {
        log.warn("HttpMessageNotReadableException: {} - Instance: {}", ex.getMessage(), req.getRequestURI());
        return base(HttpStatus.BAD_REQUEST, "malformed-json", "Malformed JSON",
                "Corpo da requisição inválido.", ErrorCode.VALIDATION.value(), req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("DataIntegrityViolationException: {} - Instance: {}", ex.getMessage(), req.getRequestURI());
        return base(HttpStatus.CONFLICT, "data-integrity", "Data integrity violation",
                "Operação não permitida por integridade de dados.", ErrorCode.CONFLICT.value(), req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Internal Server Error: {} - Instance: {}", ex.getMessage(), req.getRequestURI(), ex);
        return base(HttpStatus.INTERNAL_SERVER_ERROR, "internal", "Internal error",
                "Ocorreu um erro inesperado.", ErrorCode.INTERNAL.value(), req);
    }

    @ExceptionHandler(DomainValidationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) // 422
    public ResponseEntity<ProblemDetail> handleDomainValidation(DomainValidationException ex, WebRequest request) {
        log.warn("DomainValidationException: {} - Violations: {}", ex.getMessage(), ex.getViolations().stream().map(v -> v.getField() + ": " + v.getMessage()).toList());
        var pd = new ProblemDetail();
        HttpStatus unprocessableEntity = HttpStatus.UNPROCESSABLE_ENTITY;
        pd.setCode(ErrorCode.BUSINESS_RULE.value());
        pd.setStatus(unprocessableEntity.value());
        pd.setTitle("Violação de regras de negócio");
        pd.setDetail("Um ou mais campos violam as regras do domínio.");
        pd.setInstance(request.getContextPath());
        pd.setType(String.valueOf(URI.create("about:blank")));

        List<ProblemProperties> errors = new ArrayList<>();
        for (DomainViolation v : ex.getViolations()) {
            ProblemProperties probl = new ProblemProperties(
                    v.getField(),
                    v.getMessage(),
                    "code: "+v.getCode()
            );
            errors.add(probl);
        }
        pd.setProperties( errors);
        return  ResponseEntity.status(unprocessableEntity).body(pd);
    }

    // 401 do domínio
    @ExceptionHandler(DomainAuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleDomainAuth(DomainAuthenticationException ex, HttpServletRequest req) {
        Object violations = ex.getViolations() != null ? ex.getViolations().stream().map(v -> v.getField() + ": " + v.getMessage()).toList() : "N/A";
        log.warn("DomainAuthenticationException: {} - Violations: {} - URL: {} - IP: {}", ex.getMessage(), violations, req.getRequestURI(), req.getRemoteAddr());
        ProblemDetail pd = new ProblemDetail();
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        pd.setStatus(badRequest.value());
        pd.setTitle("Unauthorized");
        pd.setDetail("Falha de autenticação");
        pd.setCode("auth_fail");


        List<DomainViolation> v = ex.getViolations();
        if (v != null && !v.isEmpty()) {
            pd.setProperties(
                    v.stream().map(
                            value -> toProblemProperties(value,"rule")
                    ).toList());
        }

        return ResponseEntity.status(badRequest).body(pd);
    }


    // 401 do domínio
    @ExceptionHandler(DomainUnauthenticationException.class)
    public ResponseEntity<ProblemDetail> handleDomainUnauth(DomainUnauthenticationException ex, HttpServletRequest req) {
        String code = normalize(ex.getMessage());
        log.warn("DomainUnauthenticationException: {} - URL: {} - IP: {} - Code: {} ", ex.getMessage(), req.getRequestURI(), req.getRemoteAddr(), code);
        ProblemDetail pd = new ProblemDetail();
        HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
        pd.setStatus(unauthorized.value());
        pd.setTitle("Unauthorized");

        switch (code) {
            case "token_expired" -> {
                pd.setDetail("Token expirado");
                pd.setCode("token_expired");
            }
            case "token_invalid" -> {
                pd.setDetail("Token inválido");
                pd.setCode("token_invalid");
            }
            case "refresh_invalid" -> {
                pd.setDetail("Refresh token inválido");
                pd.setCode("refresh_invalid");
            }
            case "refresh_expired_or_revoked" -> {
                pd.setDetail("Refresh token expirado ou revogado");
                pd.setCode("refresh_expired_or_revoked");
            }
            case "user_inactive" -> {
                pd.setDetail("Usuário inativo");
                pd.setCode("user_inactive");
            }
            default -> pd.setDetail("Falha de autenticação");
        }

        List<DomainViolation> v = ex.getViolations();
        if (v != null && !v.isEmpty()) {
            pd.setProperties( v.stream().map(
                    value -> toProblemProperties(value,"rule")
            ).toList());
        }
        return ResponseEntity.status(unauthorized).body(pd);
    }

    // 403 do domínio
    @ExceptionHandler(DomainAccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleDomainAccess(DomainAccessDeniedException ex) {
        log.warn("DomainAccessDeniedException: {} - Required Permission: {}", ex.getMessage(), ex.getPermission());
        ProblemDetail pd = new ProblemDetail();
        HttpStatus forbidden = HttpStatus.FORBIDDEN;
        pd.setStatus(forbidden.value());
        pd.setTitle("Forbidden");
        pd.setDetail("Acesso negado");
        pd.setProperties(List.of(new ProblemProperties(
                "requiredPermission",
                "Permissão requerida",
                ex.getPermission()
        )));
        pd.setCode("access_denied");
        return ResponseEntity.status(forbidden).body(pd);
    }



    // Harmonização com Spring Security (se escaparem do filtro)
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleSpring403(org.springframework.security.access.AccessDeniedException ex, HttpServletRequest req) {
        log.warn("Spring Security AccessDeniedException: {} - URL: {} - IP: {}", ex.getMessage(), req.getRequestURI(), req.getRemoteAddr());
        ProblemDetail pd = new ProblemDetail();
        HttpStatus forbidden = HttpStatus.FORBIDDEN;
        pd.setStatus(forbidden.value());
        pd.setTitle("Forbidden");
        pd.setDetail("Acesso negado");
        pd.setCode("access_denied");
        return ResponseEntity.status(forbidden).body(pd);
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleSpring401(org.springframework.security.core.AuthenticationException ex, HttpServletRequest req) {
        log.warn("Spring Security AuthenticationException: {} - URL: {} - IP: {}", ex.getMessage(), req.getRequestURI(), req.getRemoteAddr());
        ProblemDetail pd = new ProblemDetail();
        HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
        pd.setStatus(unauthorized.value());
        pd.setTitle("Unauthorized");
        pd.setDetail("Falha de autenticação");
        pd.setCode("unauthorized");
        return ResponseEntity.status(unauthorized).body(pd);
    }

    @ExceptionHandler(TokenExpiredAuthException.class)
    public ResponseEntity<ProblemDetail> handleSpringTokenExpired(TokenExpiredAuthException ex, HttpServletRequest req) {
        log.warn("Spring Security AuthenticationException: {} - URL: {} - IP: {}", ex.getMessage(), req.getRequestURI(), req.getRemoteAddr());
        ProblemDetail pd = new ProblemDetail();
        pd.setType("auth");
        HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
        pd.setStatus(unauthorized.value());
        pd.setInstance(req.getRequestURI());
        pd.setTitle("Unauthorized");
        pd.setDetail("Token expirado");
        pd.setCode("token_expired");
        return ResponseEntity.status(unauthorized).body(pd);
    }

    @ExceptionHandler(TokenInvalidAuthException.class)
    public ResponseEntity<ProblemDetail> handleSpringTokenInvalid(TokenInvalidAuthException ex, HttpServletRequest req) {
        log.warn("Spring Security AuthenticationException: {} - URL: {} - IP: {}", ex.getMessage(), req.getRequestURI(), req.getRemoteAddr());
        ProblemDetail pd = new ProblemDetail();
        pd.setType("auth");
        HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
        pd.setStatus(unauthorized.value());
        pd.setInstance(req.getRequestURI());
        pd.setTitle("Unauthorized");
        pd.setDetail("Token inválido");
        pd.setCode("token_invalid");
        return ResponseEntity.status(unauthorized).body(pd);
    }

    /* ---------- helpers ---------- */

    /* helpers */
    private ProblemProperties toProblemProperties(DomainViolation v, String detailPrefix) {
        return new ProblemProperties(
                v.getField(),
                v.getMessage(),
                detailPrefix.concat(v.getCode())
        );
    }
    private String normalize(String s) { return s == null ? "" : s.trim().toLowerCase(); }

}
