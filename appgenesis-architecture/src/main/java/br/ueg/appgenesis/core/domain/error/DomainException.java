package br.ueg.appgenesis.core.domain.error;

public abstract class DomainException extends RuntimeException {
    private final ErrorCode code;
    protected DomainException(String message, ErrorCode code) {
        super(message);
        this.code = code;
    }
    public ErrorCode getCode() { return code; }
}
