package herdergames.latein;

import java.util.Objects;
import java.util.Set;

final class KonsNomenN extends Nomen {
    private final String nominativSingular;
    private final String stamm;

    KonsNomenN(Genus genus, String nominativSingular, String stamm) {
        super(genus);
        this.nominativSingular = Objects.requireNonNull(nominativSingular);
        this.stamm = Objects.requireNonNull(stamm);
    }

    @Override
    String deklinieren(Numerus numerus, Kasus kasus) {
        if (numerus == Numerus.SINGULAR && (Set.of(Kasus.NOMINATIV, Kasus.AKKUSATIV, Kasus.VOKATIV).contains(kasus))) {
            return nominativSingular;
        }

        return stamm + KonsNomenDeklinationN.ENDUNGEN.get(numerus).get(kasus);
    }
}
