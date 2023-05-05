package herdergames.latein;

import java.util.Set;

final class KonsNomenMF extends Nomen {
    private final String nominativSingular;
    private final String stamm;

    KonsNomenMF(Genus genus, String nominativSingular, String stamm) {
        super(genus);
        this.nominativSingular = nominativSingular;
        this.stamm = stamm;
    }

    @Override
    String deklinieren(Numerus numerus, Kasus kasus) {
        if (numerus == Numerus.SINGULAR && (Set.of(Kasus.NOMINATIV, Kasus.VOKATIV).contains(kasus))) {
            return nominativSingular;
        }

        return stamm + KonsNomenDeklinationMF.ENDUNGEN.get(numerus).get(kasus);
    }
}
