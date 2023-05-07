package herdergames.spiel;

import processing.core.PApplet;

import java.util.Optional;

public abstract non-sealed class EinzelspielerSpiel extends Spiel {
    protected EinzelspielerSpiel(PApplet applet) {
        super(applet);
    }

    public abstract Optional<Ergebnis> draw();

    public enum Ergebnis {
        GEWONNEN(1),
        UNENTSCHIEDEN(0),
        VERLOREN(-1);

        public final int punkte;

        Ergebnis(int punkte) {
            this.punkte = punkte;
        }
    }

    @FunctionalInterface
    public non-sealed interface Factory extends Spiel.Factory {
        EinzelspielerSpiel neuesSpiel(PApplet applet, Spieler spieler);
    }
}
