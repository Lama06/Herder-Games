package herdergames.spiel;

import processing.core.PApplet;

import java.util.Optional;

public abstract non-sealed class EinzelspielerSpiel extends Spiel {
    protected EinzelspielerSpiel(PApplet applet) {
        super(applet);
    }

    public abstract Optional<Ergebnis> draw();

    public enum Ergebnis {
        GEWONNEN,
        UNENTSCHIEDEN,
        VERLOREN
    }

    @FunctionalInterface
    public non-sealed interface Factory extends Spiel.Factory {
        EinzelspielerSpiel neuesSpiel(PApplet applet, Spieler spieler);
    }
}
