package herdergames.vier_gewinnt;

enum VerschiebungHorizontal {
    LINKS(-1),
    KEINE(0),
    RECHTS(1);

    final int verschiebung;

    VerschiebungHorizontal(int verschiebung) {
        this.verschiebung = verschiebung;
    }
}
