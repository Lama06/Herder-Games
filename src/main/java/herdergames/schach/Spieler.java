package herdergames.schach;

enum Spieler implements herdergames.ai.Spieler<Spieler> {
    SCHWARZ,
    WEISS;

    @Override
    public Spieler getGegner() {
        return switch (this) {
            case SCHWARZ -> WEISS;
            case WEISS -> SCHWARZ;
        };
    }

    VerschiebungVertikal getBauerBewegenVerschiebung() {
        return switch (this) {
            case SCHWARZ -> VerschiebungVertikal.OBEN;
            case WEISS -> VerschiebungVertikal.UNTEN;
        };
    }

    int getBauernUmwandlungsZeile() {
        return switch (this) {
            case SCHWARZ -> 0;
            case WEISS -> 7;
        };
    }

    int getBauernStartZeile() {
        return switch (this) {
            case SCHWARZ -> 6;
            case WEISS -> 1;
        };
    }
}
