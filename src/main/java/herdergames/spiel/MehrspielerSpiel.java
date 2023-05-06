package herdergames.spiel;

import processing.core.PApplet;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract non-sealed class MehrspielerSpiel extends Spiel {
    protected MehrspielerSpiel(PApplet applet) {
        super(applet);
    }

    public abstract Optional<List<Spieler.Id>> draw();

    @FunctionalInterface
    public non-sealed interface Factory extends Spiel.Factory {
        MehrspielerSpiel neuesSpiel(PApplet applet, Set<Spieler> spieler);
    }
}
