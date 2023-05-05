package herdergames.latein;

import java.util.Optional;
import java.util.Set;

abstract class NomenDeklination {
    abstract Optional<Nomen> parseImpl(String nominativ, Optional<String> genitiv, Genus genus);

    final Optional<Nomen> parse(NomenWoerterbuchEintrag eintrag) {
        Genus genus;
        if (eintrag.genus().isPresent()) {
            if (!getErlaubteGenuse().contains(eintrag.genus().get())) {
                return Optional.empty();
            }
            genus = eintrag.genus().get();
        } else {
            if (getStandardGenus().isEmpty()) {
                return Optional.empty();
            }
            genus = getStandardGenus().get();
        }
        return parseImpl(eintrag.nominativ(), eintrag.genitiv(), genus);
    }

    abstract Optional<Genus> getStandardGenus();

    abstract Set<Genus> getErlaubteGenuse();
}
