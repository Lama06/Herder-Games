package herdergames.vier_gewinnt;

enum VerschiebungVertikal {
    OBEN(-1),
    KEINE(0),
    UNTEN(1);

    final int verschiebung;

    VerschiebungVertikal(int verschiebung) {
        this.verschiebung = verschiebung;
    }
}
