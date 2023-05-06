package herdergames.latein;

import java.util.Objects;

final class StammNomen extends Nomen {
    private final StammNomenDeklination deklination;
    private final String stamm;

    StammNomen(Genus genus, StammNomenDeklination deklination, String stamm) {
        super(genus);
        this.stamm = Objects.requireNonNull(stamm);
        this.deklination = Objects.requireNonNull(deklination);
    }

    @Override
    String deklinieren(Numerus numerus, Kasus kasus) {
        String endung;
        if (deklination.getStammAbhaengigeEndung(numerus, kasus, stamm).isPresent()) {
            endung = deklination.getStammAbhaengigeEndung(numerus, kasus, stamm).get();
        } else {
            endung = deklination.getEndungen().get(numerus).get(kasus);
        }

        return stamm + endung;
    }
}
