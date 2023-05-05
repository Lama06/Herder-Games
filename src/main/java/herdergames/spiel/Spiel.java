package herdergames.spiel;

import processing.core.PApplet;

import java.util.*;

public abstract class Spiel {
    protected final PApplet applet;

    protected Spiel(PApplet applet) {
        this.applet = applet;
    }

    public void mousePressed() { }

    public void keyPressed() { }

    public void keyReleased() { }

    public interface Factory { }

    public abstract static class Einzelspieler extends Spiel {
        protected Einzelspieler(PApplet applet) {
            super(applet);
        }

        public abstract Optional<Ergebnis> draw();

        public enum Ergebnis {
            GEWONNEN,
            UNENTSCHIEDEN,
            VERLOREN
        }

        @FunctionalInterface
        public interface Factory extends Spiel.Factory {
            Einzelspieler neuesSpiel(PApplet applet, Spieler spieler);
        }
    }

    public abstract static class SpielerGegenSpieler extends Spiel {
        protected SpielerGegenSpieler(PApplet applet) {
            super(applet);
        }

        public abstract Optional<Optional<Spieler.Id>> draw();

        @FunctionalInterface
        public interface Factory extends Spiel.Factory {
            SpielerGegenSpieler neuesSpiel(PApplet applet, Spieler spieler1, Spieler spieler2);
        }
    }

    public abstract static class Mehrspieler extends Spiel {
        protected Mehrspieler(PApplet applet) {
            super(applet);
        }

        public abstract Optional<List<Spieler.Id>> draw();

        @FunctionalInterface
        public interface Factory extends Spiel.Factory {
            Mehrspieler neuesSpiel(PApplet applet, Set<Spieler> spieler);
        }
    }
}
