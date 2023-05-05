package herdergames.latein;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

final class ONomenDeklinationN extends StammNomenDeklination {
    static final ONomenDeklinationN INSTANCE = new ONomenDeklinationN();

    @Override
    Optional<Genus> getStandardGenus() {
        return Optional.of(Genus.NEUTRUM);
    }

    @Override
    Set<Genus> getErlaubteGenuse() {
        return Set.of(Genus.NEUTRUM);
    }

    @Override
    boolean istGenitivNotwendig() {
        return false;
    }

    @Override
    Map<Numerus, Map<Kasus, String>> getEndungen() {
        return Map.of(
                Numerus.SINGULAR, Map.of(
                        Kasus.NOMINATIV, "um",
                        Kasus.GENITIV, "i",
                        Kasus.DATIV, "o",
                        Kasus.AKKUSATIV, "um",
                        Kasus.ABLATIV, "o",
                        Kasus.VOKATIV, "um"
                ),
                Numerus.PLURAL, Map.of(
                        Kasus.NOMINATIV, "a",
                        Kasus.GENITIV, "orum",
                        Kasus.DATIV, "is",
                        Kasus.AKKUSATIV, "a",
                        Kasus.ABLATIV, "is",
                        Kasus.VOKATIV, "a"
                )
        );
    }
}
