package herdergames.spiel;

import processing.core.PApplet;

import java.util.Optional;

public abstract non-sealed class SpielerGegenSpielerSpiel extends Spiel {
    protected SpielerGegenSpielerSpiel(PApplet applet) {
        super(applet);
    }

    public abstract Optional<Optional<Spieler.Id>> draw();

    @FunctionalInterface
    public non-sealed interface Factory extends Spiel.Factory {
        SpielerGegenSpielerSpiel neuesSpiel(PApplet applet, Spieler spieler1, Spieler spieler2);
    }
}
