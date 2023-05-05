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

    private final String nominativSingularMaskulinum;
    private final String stamm;

    SuperlativAdjektiv(String nominativSingularMaskulinum, String stamm) {
        super(false);
        this.nominativSingularMaskulinum = Objects.requireNonNull(nominativSingularMaskulinum);

        if (UNREGELMAESSIGE_SUPERLATIVE.containsKey(nominativSingularMaskulinum)) {
            this.stamm = UNREGELMAESSIGE_SUPERLATIVE.get(nominativSingularMaskulinum);
            return;
        }
        this.stamm = Objects.requireNonNull(stamm);
    }

    @Override
    String deklinieren(Genus genus, Numerus numerus, Kasus kasus) {
        String endung = AOAdjektivDeklination.ENDUNGEN.get(genus).get(numerus).get(kasus);

        if (nominativSingularMaskulinum.endsWith("er")) {
            // Der Superlativ von pulcher ist pulcherrimus
            return nominativSingularMaskulinum + KENNZEICHEN_ER + endung;
        }

        return stamm + KENNZEICHEN + endung;
    }

    @Override
    Adjektiv steigern(Steigerung steigerung) {
        throw new UnsupportedOperationException();
    }
}
