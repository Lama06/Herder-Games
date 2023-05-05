package herdergames.latein;

import java.util.Map;
import java.util.Objects;

final class KomperativAdjektiv extends Adjektiv {
    private static final String NOMINATIV_SINGULAR_MASKULINUM_FEMININUM_ENDUNG = "ior";
    private static final String NOMINATIV_SINGULAR_NEUTRUM_ENDUNG = "ius";
    private static final String KENNZEICHEN = "ior";

    private static final Map<String, String> UNREGELMAESSIGE_KOMPERATIVE = Map.of(
            "bonus", "meli",
            "malus", "pe",
            "magnus", "ma"
    );

    private final String stamm;

    KomperativAdjektiv(String nominativSingularMaskulinum, String stamm) {
        super(false);
        if (UNREGELMAESSIGE_KOMPERATIVE.containsKey(nominativSingularMaskulinum)) {
            this.stamm = UNREGELMAESSIGE_KOMPERATIVE.get(nominativSingularMaskulinum);
            return;
        }
        this.stamm = Objects.requireNonNull(stamm);
    }

    @Override
    String deklinieren(Genus genus, Numerus numerus, Kasus kasus) {
        if (numerus == Numerus.SINGULAR && (kasus == Kasus.NOMINATIV || kasus == Kasus.VOKATIV)) {
            return switch (genus) {
                case MASKULINUM, FEMININUM -> stamm + NOMINATIV_SINGULAR_MASKULINUM_FEMININUM_ENDUNG;
                case NEUTRUM -> stamm + NOMINATIV_SINGULAR_NEUTRUM_ENDUNG;
            };
        }

        if (genus == Genus.NEUTRUM && numerus == Numerus.SINGULAR && kasus == Kasus.AKKUSATIV) {
            return stamm + NOMINATIV_SINGULAR_NEUTRUM_ENDUNG;
        }

        return stamm + KENNZEICHEN + KonsAdjektivDeklination.ENDUNGEN.get(genus).get(numerus).get(kasus);
    }

    @Override
    Adjektiv steigern(Steigerung steigerung) {
        throw new UnsupportedOperationException();
    }
}
