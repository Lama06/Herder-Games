package herdergames.schach;

record VerschiebungVertikal(int verschiebung) {
    static final VerschiebungVertikal OBEN = new VerschiebungVertikal(-1);
    static final VerschiebungVertikal KEINE = new VerschiebungVertikal(0);
    static final VerschiebungVertikal UNTEN = new VerschiebungVertikal(1);

    VerschiebungVertikal doppelt() {
        return new VerschiebungVertikal(verschiebung * 2);
    }
}
