package herdergames.latein;

import java.util.Objects;

abstract class Nomen {
    final Genus genus;

    Nomen(Genus genus) {
        this.genus = Objects.requireNonNull(genus);
    }

    abstract String deklinieren(Numerus numerus, Kasus kasus);
}
