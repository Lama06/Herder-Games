package herdergames.latein;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

final class UNomenDeklinations extends StammNomenDeklination {
    static final UNomenDeklinations INSTANCE = new UNomenDeklinations();

    @Override
    Optional<Genus> getStandardGenus() {
        return Optional.of(Genus.MASKULINUM);
    }

    @Override
    Set<Genus> getErlaubteGenuse() {
        return Set.of(Genus.MASKULINUM, Genus.FEMININUM);
    }

    @Override
    boolean istGenitivNotwendig() {
        return true; // Um Verwechselung mit O-Deklination zu vermeiden
    }

    @Override
    Map<Numerus, Map<Kasus, String>> getEndungen() {
        return Map.of(
                Numerus.SINGULAR, Map.of(
                        Kasus.NOMINATIV, "us",
                        Kasus.GENITIV, "us",
                        Kasus.DATIV, "ui",
                        Kasus.AKKUSATIV, "um",
                        Kasus.ABLATIV, "u",
                        Kasus.VOKATIV, "us"
                ),
                Numerus.PLURAL, Map.of(
                        Kasus.NOMINATIV, "us",
                        Kasus.GENITIV, "uum",
                        Kasus.DATIV, "ibus",
                        Kasus.AKKUSATIV, "us",
                        Kasus.ABLATIV, "ibus",
                        Kasus.VOKATIV, "us"
                )
        );
    }
}
