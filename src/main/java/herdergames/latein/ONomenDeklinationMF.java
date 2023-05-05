package herdergames.latein;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

final class ONomenDeklinationMF extends StammNomenDeklination {
    static final ONomenDeklinationMF INSTANCE = new ONomenDeklinationMF();

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
        return false;
    }

    @Override
    Map<Numerus, Map<Kasus, String>> getEndungen() {
        return Map.of(
                Numerus.SINGULAR, Map.of(
                        Kasus.NOMINATIV, "us",
                        Kasus.GENITIV, "i",
                        Kasus.DATIV, "o",
                        Kasus.AKKUSATIV, "um",
                        Kasus.ABLATIV, "o"
                ),
                Numerus.PLURAL, Map.of(
                        Kasus.NOMINATIV, "i",
                        Kasus.GENITIV, "orum",
                        Kasus.DATIV, "is",
                        Kasus.AKKUSATIV, "os",
                        Kasus.ABLATIV, "is",
                        Kasus.VOKATIV, "i"
                )
        );
    }

    @Override
    Optional<String> getStammAbhaengigeEndung(Numerus numerus, Kasus kasus, String stamm) {
        if (numerus != Numerus.SINGULAR || kasus != Kasus.VOKATIV) {
            return Optional.empty();
        }

        if (stamm.endsWith("i")) {
            return Optional.of(""); // Vokativ von Albius ist Albi
        }

        return Optional.of("e"); // Et tu, Brute :)
    }
}
