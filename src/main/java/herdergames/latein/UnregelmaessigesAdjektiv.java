package herdergames.latein;

import java.util.Objects;

final class UnregelmaessigesAdjektiv extends Adjektiv {
    private final UnregelmaessigeAdjektivDeklination deklination;

    UnregelmaessigesAdjektiv(UnregelmaessigeAdjektivDeklination deklination) {
        super(false);
        this.deklination = Objects.requireNonNull(deklination);
    }

    @Override
    String deklinieren(Genus genus, Numerus numerus, Kasus kasus) {
        return deklination.getFormen().get(genus).get(numerus).get(kasus);
    }

    @Override
    Adjektiv steigern(Steigerung steigerung) {
        throw new UnsupportedOperationException();
    }
}
