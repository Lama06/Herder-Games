package herdergames.dame;

enum RichtungVertikal {
    OBEN(-1),
    UNTEN(1);

    final int verschiebung;

    RichtungVertikal(int verschiebung) {
        this.verschiebung = verschiebung;
    }
}
