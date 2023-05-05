package herdergames.latein;

abstract class Adjektiv {
    final boolean steigerbar;

    Adjektiv(boolean steigerbar) {
        this.steigerbar = steigerbar;
    }

    abstract String deklinieren(Genus genus, Numerus numerus, Kasus kasus);

    abstract Adjektiv steigern(Steigerung steigerung);

    Nomen substantivieren(Genus genus) {
        return new Nomen(genus) {
            @Override
            String deklinieren(Numerus numerus, Kasus kasus) {
                return Adjektiv.this.deklinieren(genus, numerus, kasus);
            }
        };
    }
}
