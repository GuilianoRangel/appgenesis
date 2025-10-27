package br.ueg.appgenesis.core.domain.error;

public enum ErrorCode {
    NOT_FOUND("TR-NOT-404"),
    BUSINESS_RULE("TR-BIZ-422"),
    VALIDATION("TR-VAL-001"),
    CONFLICT("TR-CON-409"),
    INTERNAL("TR-INT-500");

    private final String value;
    ErrorCode(String v) { this.value = v; }
    public String value() { return value; }
}
