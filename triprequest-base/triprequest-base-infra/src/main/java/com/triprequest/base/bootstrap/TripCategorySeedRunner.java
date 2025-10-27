package com.triprequest.base.bootstrap;

import br.ueg.appgenesis.core.security.CredentialContextPort;
import br.ueg.appgenesis.core.security.CredentialPrincipal;
import com.triprequest.base.tripcategory.domain.TripCategory;
import com.triprequest.base.tripcategory.usecase.TripCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Executa o seed inicial de categorias de viagem (trip_category)
 * Deve rodar após o SecuritySeedRunner.
 */
@Component
@DependsOn("securitySeedRunner")
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
@RequiredArgsConstructor
public class TripCategorySeedRunner implements ApplicationRunner {

    private final TripCategoryService tripCategoryService;
    private final CredentialContextPort credentialContext;

    @Override
    public void run(ApplicationArguments args) {

        // Usa o principal SEED com permissões temporárias
        var seedPrincipal = new CredentialPrincipal() {
            @Override
            public Long getUserId() {
                return 0L;
            }

            @Override
            public String getUsername() {
                return "SEED";
            }

            @Override
            public List<String> getPermissions() {
                return java.util.List.of("TRIP_CATEGORY_WRITE", "TRIP_CATEGORY_READ");
            }
        };

        credentialContext.setAuthenticatedPrincipal(seedPrincipal);

        try {
            upsertCategory("DOMESTIC",      "Viagem Nacional",                  "Viagem dentro do território nacional",     true);
            upsertCategory("INTERNATIONAL", "Viagem Internacional",             "Viagem para fora do país",                 true);
            upsertCategory("CLIENT_VISIT",  "Visita a Cliente",                 "Deslocamento para reuniões com clientes",  true);
            upsertCategory("TRAINING",      "Treinamento",                      "Participação em eventos ou capacitações",  true);
            upsertCategory("INTERNAL_EVENT", "Evento Interno / Corporativo",    "Reuniões, conferências internas etc.",     true);
        } finally {
            credentialContext.clear();
        }
    }

    /* ========================= UPSERT ========================= */

    private TripCategory upsertCategory(String code, String name, String description, boolean active) {
        Optional<TripCategory> found = tripCategoryService.findAll().stream()
                .filter(c -> c.getCode().equalsIgnoreCase(code))
                .findFirst();

        if (found.isPresent()) {
            TripCategory existing = found.get();
            boolean changed = false;

            if (!equalsStr(existing.getName(), name)) { existing.setName(name); changed = true; }
            if (!equalsStr(existing.getDescription(), description)) { existing.setDescription(description); changed = true; }
            if (existing.getActive() != active) { existing.setActive(active); changed = true; }

            return changed ? tripCategoryService.patch(existing.getId(), existing) : existing;
        }

        TripCategory input = TripCategory.builder()
                .code(code)
                .name(name)
                .description(description)
                .active(active)
                .build();

        return tripCategoryService.create(input);
    }

    private boolean equalsStr(String a, String b) {
        return (a == null && b == null) || (a != null && a.equals(b));
    }
}
