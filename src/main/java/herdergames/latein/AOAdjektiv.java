package herdergames.latein;

import java.util.Objects;
import java.util.Optional;

final class AOAdjektiv extends Adjektiv {
    private final String stamm;
    private final Optional<String> nominativSingularM;

    AOAdjektiv(String stamm, Optional<String> nominativSingularM) {
        super(true);
        this.stamm = Objects.requireNonNull(stamm);
        this.nominativSingularM = Objects.requireNonNull(nominativSingularM);
    }

    @Override
    String deklinieren(Genus genus, Numerus numerus, Kasus kasus) {
        if (genus == Genus.MASKULINUM &&
                numerus == Numerus.SINGULAR &&
                (kasus == Kasus.NOMINATIV || kasus == Kasus.VOKATIV) &&
                nominativSingularM.isPresent()
        ) {
            return nominativSingularM.get();
        }

        return stamm + AOAdjektivDeklination.ENDUNGEN.get(genus).get(numerus).get(kasus);
    }

    @Override
    Adjektiv steigern(Steigerung steigerung) {
        String nominativSingularMasklulinum = deklinieren(Genus.MASKULINUM, Numerus.SINGULAR, Kasus.NOMINATIV);

        return switch (steigerung) {
            case KOMPERATIV -> new KomperativAdjektiv(nominativSingularMasklulinum, stamm);
            case SUPERLATIV -> new SuperlativAdjektiv(nominativSingularMasklulinum, stamm);
        };
    }
}
