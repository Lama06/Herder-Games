package herdergames.dame;

enum Spieler implements herdergames.ai.Spieler<Spieler> {
    SPIELER_OBEN,
    SPIELER_UNTEN;

    Stein getStein() {
        return switch (this) {
            case SPIELER_OBEN -> Stein.STEIN_SPIELER_OBEN;
            case SPIELER_UNTEN -> Stein.STEIN_SPIELER_UNTEN;
        };
    }

    Stein getDame() {
        return switch (this) {
            case SPIELER_OBEN -> Stein.DAME_SPIELER_OBEN;
            case SPIELER_UNTEN -> Stein.DAME_SPIELER_UNTEN;
        };
    }

    RichtungVertikal getBewegungsRichtung() {
        return switch (this) {
            case SPIELER_OBEN -> RichtungVertikal.UNTEN;
            case SPIELER_UNTEN -> RichtungVertikal.OBEN;
        };
    }

    int getDameZeile() {
        return switch (this) {
            case SPIELER_OBEN -> 7;
            case SPIELER_UNTEN -> 0;
        };
    }

    @Override
    public Spieler getGegner() {
        return switch (this) {
            case SPIELER_OBEN -> SPIELER_UNTEN;
            case SPIELER_UNTEN -> SPIELER_OBEN;
        };
    }
}
