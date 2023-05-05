package herdergames.latein;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

final class ANomenDeklination extends StammNomenDeklination {
    static final ANomenDeklination INSTANCE = new ANomenDeklination();

    @Override
    Optional<Genus> getStandardGenus() {
        return Optional.of(Genus.FEMININUM);
    }

    @Override
    Set<Genus> getErlaubteGenuse() {
        return Set.of(Genus.FEMININUM, Genus.MASKULINUM);
    }

    @Override
    boolean istGenitivNotwendig() {
        return false;
    }

    @Override
    Map<Numerus, Map<Kasus, String>> getEndungen() {
        return Map.of(
                Numerus.SINGULAR, Map.of(
                        Kasus.NOMINATIV, "a",
                        Kasus.GENITIV, "ae",
                        Kasus.DATIV, "ae",
                        Kasus.AKKUSATIV, "am",
                        Kasus.ABLATIV, "a",
                        Kasus.VOKATIV, "a"
                ),
                Numerus.PLURAL, Map.of(
                        Kasus.NOMINATIV, "ae",
                        Kasus.GENITIV, "arum",
                        Kasus.DATIV, "is",
                        Kasus.AKKUSATIV, "as",
                        Kasus.ABLATIV, "is",
                        Kasus.VOKATIV, "ae"
                )
        );
    }
}
