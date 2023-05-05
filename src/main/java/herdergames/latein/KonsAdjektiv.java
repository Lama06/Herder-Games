package herdergames.latein;

import java.util.Objects;

final class KonsAdjektiv extends Adjektiv {
    private final String nominativSingularMaskulinum;
    private final String nominativSingularFemininum;
    private final String nominativSingularNeutrum;
    private final String stamm;

    KonsAdjektiv(
            String nominativSingularMaskulinum,
            String nominativSingularFemininum,
            String nominativSingularNeutrum,
            String stamm
    ) {
        super(true);
        this.nominativSingularMaskulinum = Objects.requireNonNull(nominativSingularMaskulinum);
        this.nominativSingularFemininum = Objects.requireNonNull(nominativSingularFemininum);
        this.nominativSingularNeutrum = Objects.requireNonNull(nominativSingularNeutrum);
        this.stamm = Objects.requireNonNull(stamm);
    }

    @Override
    String deklinieren(Genus genus, Numerus numerus, Kasus kasus) {
        if (numerus == Numerus.SINGULAR && (kasus == Kasus.NOMINATIV || kasus == Kasus.VOKATIV)) {
            return switch (genus) {
                case MASKULINUM -> nominativSingularMaskulinum;
                case FEMININUM -> nominativSingularFemininum;
                case NEUTRUM -> nominativSingularNeutrum;
            };
        }

        if (genus == Genus.NEUTRUM && numerus == Numerus.SINGULAR && kasus == Kasus.AKKUSATIV) {
            return nominativSingularNeutrum;
        }

        return stamm + KonsAdjektivDeklination.ENDUNGEN.get(genus).get(numerus).get(kasus);
    }

    @Override
    Adjektiv steigern(Steigerung steigerung) {
        return switch (steigerung) {
            case KOMPERATIV -> new KomperativAdjektiv(nominativSingularMaskulinum, stamm);
            case SUPERLATIV -> new SuperlativAdjektiv(nominativSingularMaskulinum, stamm);
        };
    }
}
