package herdergames.latein;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

final class ENomenDeklination extends StammNomenDeklination {
    static final ENomenDeklination INSTANCE = new ENomenDeklination();

    @Override
    Optional<Genus> getStandardGenus() {
        return Optional.of(Genus.FEMININUM);
    }

    @Override
    Set<Genus> getErlaubteGenuse() {
        return Set.of(Genus.MASKULINUM, Genus.FEMININUM);
    }

    @Override
    boolean istGenitivNotwendig() {
        return true;
    }

    @Override
    Map<Numerus, Map<Kasus, String>> getEndungen() {
        return Map.of(
                Numerus.SINGULAR, Map.of(
                        Kasus.NOMINATIV, "es",
                        Kasus.GENITIV, "ei",
                        Kasus.DATIV, "ei",
                        Kasus.AKKUSATIV, "em",
                        Kasus.ABLATIV, "e",
                        Kasus.VOKATIV, "es"
                ),
                Numerus.PLURAL, Map.of(
                        Kasus.NOMINATIV, "es",
                        Kasus.GENITIV, "erum",
                        Kasus.DATIV, "ebus",
                        Kasus.AKKUSATIV, "es",
                        Kasus.ABLATIV, "ebus",
                        Kasus.VOKATIV, "es"
                )
        );
    }
}
