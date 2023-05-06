package herdergames.dame;

import processing.core.PApplet;

import java.util.Optional;

enum Stein {
    STEIN_SPIELER_OBEN,
    STEIN_SPIELER_UNTEN,
    DAME_SPIELER_OBEN,
    DAME_SPIELER_UNTEN;

    static Optional<Optional<Stein>> buchstabeZuFeld(char buchstabe) {
        return switch (buchstabe) {
            case 'a' -> Optional.of(Optional.of(STEIN_SPIELER_OBEN));
            case 'A' -> Optional.of(Optional.of(DAME_SPIELER_OBEN));
            case 'b' -> Optional.of(Optional.of(STEIN_SPIELER_UNTEN));
            case 'B' -> Optional.of(Optional.of(DAME_SPIELER_UNTEN));
            case '_' -> Optional.of(Optional.empty());
            default -> Optional.empty();
        };
    }

    static char feldZuBuchstabe(Optional<Stein> feld) {
        return feld.map(stein -> switch (stein) {
            case STEIN_SPIELER_OBEN -> 'a';
            case DAME_SPIELER_OBEN -> 'A';
            case STEIN_SPIELER_UNTEN -> 'b';
            case DAME_SPIELER_UNTEN -> 'B';
        }).orElse('_');
    }

    boolean istDame() {
        return this == DAME_SPIELER_OBEN || this == DAME_SPIELER_UNTEN;
    }

    boolean istStein() {
        return this == STEIN_SPIELER_OBEN || this == STEIN_SPIELER_UNTEN;
    }

    Spieler getSpieler() {
        return switch (this) {
            case STEIN_SPIELER_OBEN, DAME_SPIELER_OBEN -> Spieler.SPIELER_OBEN;
            case STEIN_SPIELER_UNTEN, DAME_SPIELER_UNTEN -> Spieler.SPIELER_UNTEN;
        };
    }

    int getColor(PApplet applet) {
        return switch (this) {
            case STEIN_SPIELER_OBEN -> applet.color(66, 176, 245);
            case DAME_SPIELER_OBEN -> applet.color(1, 44, 71);
            case STEIN_SPIELER_UNTEN -> applet.color(245, 103, 32);
            case DAME_SPIELER_UNTEN -> applet.color(196, 14, 35);
        };
    }
}
