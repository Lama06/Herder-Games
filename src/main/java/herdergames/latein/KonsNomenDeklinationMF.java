package herdergames.latein;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

final class KonsNomenDeklinationMF extends NomenDeklination {
    static final KonsNomenDeklinationMF INSTANCE = new KonsNomenDeklinationMF();

    static final Map<Numerus, Map<Kasus, String>> ENDUNGEN = Map.of(
            Numerus.SINGULAR, Map.of(
                    Kasus.GENITIV, "is",
                    Kasus.DATIV, "i",
                    Kasus.AKKUSATIV, "em",
                    Kasus.ABLATIV, "e"
            ),
            Numerus.PLURAL, Map.of(
                    Kasus.NOMINATIV, "es",
                    Kasus.GENITIV, "um",
                    Kasus.DATIV, "ibus",
                    Kasus.AKKUSATIV, "es",
                    Kasus.ABLATIV, "ibus",
                    Kasus.VOKATIV, "es"
            )
    );

    @Override
    Optional<Nomen> parseImpl(String nominativ, Optional<String> genitiv, Genus genus) {
        if (genitiv.isEmpty() || !genitiv.get().endsWith("is")) {
            return Optional.empty();
        }
        String stamm = genitiv.get().substring(0, genitiv.get().length() - "is".length());
        return Optional.of(new KonsNomenMF(genus, nominativ, stamm));
    }

    @Override
    Optional<Genus> getStandardGenus() {
        return Optional.empty();
    }

    @Override
    Set<Genus> getErlaubteGenuse() {
        return Set.of(Genus.MASKULINUM, Genus.FEMININUM);
    }
}
