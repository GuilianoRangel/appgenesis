package br.ueg.appgenesis.core.domain.error;

public class BusinessException extends DomainException {
    public BusinessException(String message) { super(message, ErrorCode.BUSINESS_RULE); }
}
