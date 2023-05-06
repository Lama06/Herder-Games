package herdergames.latein;

import java.util.Map;
import java.util.Objects;

final class SuperlativAdjektiv extends Adjektiv {
    private static final String KENNZEICHEN = "issim";
    private static final String KENNZEICHEN_ER = "rim";

    private static final Map<String, String> UNREGELMAESSIGE_SUPERLATIVE = Map.of(
            "bonus", "optim",
            "malus", "pessim",
            "magnus", "maxim"
    );

    private final String stamm;

    SuperlativAdjektiv(String nominativSingularMaskulinum, String stamm) {
        super(false);

        if (UNREGELMAESSIGE_SUPERLATIVE.containsKey(nominativSingularMaskulinum)) {
            this.stamm = UNREGELMAESSIGE_SUPERLATIVE.get(nominativSingularMaskulinum);
        } else if (nominativSingularMaskulinum.endsWith("er")) {
            this.stamm = nominativSingularMaskulinum + KENNZEICHEN_ER;
        } else {
            this.stamm = Objects.requireNonNull(stamm) + KENNZEICHEN;
        }
    }

    @Override
    String deklinieren(Genus genus, Numerus numerus, Kasus kasus) {
        String endung = AOAdjektivDeklination.ENDUNGEN.get(genus).get(numerus).get(kasus);
        return stamm + endung;
    }

    @Override
    Adjektiv steigern(Steigerung steigerung) {
        throw new UnsupportedOperationException();
    }
}
