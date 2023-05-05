package herdergames.latein;

abstract class Nomen {
    final Genus genus;

    Nomen(Genus genus) {
        this.genus = genus;
    }

    abstract String deklinieren(Numerus numerus, Kasus kasus);
}
