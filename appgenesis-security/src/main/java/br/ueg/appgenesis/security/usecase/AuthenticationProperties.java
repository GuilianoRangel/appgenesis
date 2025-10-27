package br.ueg.appgenesis.security.usecase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class AuthenticationProperties {
    /** expiração do token em minutos (default 120) */
    private long tokenTtlMinutes = 120L;
    /** expiração do token de refresh (default 7 dias ) */
    private long refreshTtlDays  = 7L;
}
