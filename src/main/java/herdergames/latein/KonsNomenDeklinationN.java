package herdergames.latein;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

final class KonsNomenDeklinationN extends NomenDeklination {
    static final KonsNomenDeklinationN INSTANCE = new KonsNomenDeklinationN();

    static final Map<Numerus, Map<Kasus, String>> ENDUNGEN = Map.of(
            Numerus.SINGULAR, Map.of(
                    Kasus.GENITIV, "is",
                    Kasus.DATIV, "i",
                    Kasus.ABLATIV, "e"
            ),
            Numerus.PLURAL, Map.of(
                    Kasus.NOMINATIV, "a",
                    Kasus.GENITIV, "um",
                    Kasus.DATIV, "ibus",
                    Kasus.AKKUSATIV, "a",
                    Kasus.ABLATIV, "ibus",
                    Kasus.VOKATIV, "a"
            )
    );

    @Override
    Optional<Nomen> parseImpl(String nominativ, Optional<String> genitiv, Genus genus) {
        if (genitiv.isEmpty() || !genitiv.get().endsWith("is")) {
            return Optional.empty();
        }
        String stamm = genitiv.get().substring(0, genitiv.get().length() - "is".length());
        return Optional.of(new KonsNomenN(genus, nominativ, stamm));
    }

    @Override
    Optional<Genus> getStandardGenus() {
        return Optional.empty();
    }

    @Override
    Set<Genus> getErlaubteGenuse() {
        return Set.of(Genus.NEUTRUM);
    }
}
