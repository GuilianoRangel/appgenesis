package br.ueg.appgenesis.core.domain.error;

public class NotFoundException extends DomainException {
    public NotFoundException(String message) { super(message, ErrorCode.NOT_FOUND); }
}
