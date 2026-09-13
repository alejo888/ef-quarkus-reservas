package com.mitocode.profesional.infrastructure;

import io.quarkus.test.junit.QuarkusTestProfile;
import java.util.Set;

public class RepositorioLentoTestProfile implements QuarkusTestProfile {

    @Override
    public Set<Class<?>> getEnabledAlternatives() {
        return Set.of(ReservaRepositoryLenta.class);
    }
}
