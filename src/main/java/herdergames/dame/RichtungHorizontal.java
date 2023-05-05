package herdergames.dame;

enum RichtungHorizontal {
    LINKS(-1),
    RECHTS(1);

    final int verschiebung;

    RichtungHorizontal(int verschiebung) {
        this.verschiebung = verschiebung;
    }
}
