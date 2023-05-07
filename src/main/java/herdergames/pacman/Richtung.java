package herdergames.pacman;

import herdergames.util.Steuerung;

enum Richtung {
    LINKS,
    RECHTS,
    OBEN,
    UNTEN;

    static Richtung vonSteuerungRichtung(Steuerung.Richtung richtung) {
        return switch (richtung) {
            case LINKS -> LINKS;
            case RECHTS -> RECHTS;
            case OBEN -> OBEN;
            case UNTEN -> UNTEN;
        };
    }
}
