package br.ueg.appgenesis.core.usecase.support;

import br.ueg.appgenesis.core.domain.Auditable;
import java.time.LocalDateTime;

public class DefaultAuditableInitializer implements AuditableInitializer {
    @Override
    public <T> void onCreate(T entity) {
        if (entity instanceof Auditable aud) {
            LocalDateTime now = LocalDateTime.now();
            if (aud.getCreatedAt() == null) aud.setCreatedAt(now);
            aud.setUpdatedAt(now);
        }
    }

    @Override
    public <T> void onUpdate(T entity) {
        if (entity instanceof Auditable aud) {
            aud.setUpdatedAt(LocalDateTime.now());
        }
    }
}
