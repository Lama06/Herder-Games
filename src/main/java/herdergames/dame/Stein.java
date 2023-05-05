package herdergames.dame;

import processing.core.PApplet;

enum Stein {
    STEIN_SPIELER_OBEN,
    STEIN_SPIELER_UNTEN,
    DAME_SPIELER_OBEN,
    DAME_SPIELER_UNTEN;

    boolean isDame() {
        return this == DAME_SPIELER_OBEN || this == DAME_SPIELER_UNTEN;
    }

    boolean isStein() {
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
