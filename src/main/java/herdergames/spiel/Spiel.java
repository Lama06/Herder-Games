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

    public static final class Spieler {
        public final Id id;
        public final String name;
        public final int punkte;

        public Spieler(Id id, String name, int punkte) {
            this.id = id;
            this.name = name;
            this.punkte = punkte;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Spieler spieler = (Spieler) o;
            return id == spieler.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        public enum Id {
            // Reihenfolge ist wichtig, weil Spiele ordinal() verwenden
            SPIELER_1,
            SPIELER_2,
            SPIELER_3,
            SPIELER_4;

            @Override
            public String toString() {
                switch (this) {
                    case SPIELER_1:
                        return "Spieler 1";
                    case SPIELER_2:
                        return "Spieler 2";
                    case SPIELER_3:
                        return "Spieler 3";
                    case SPIELER_4:
                        return "Spieler 4";
                    default:
                        throw new IllegalArgumentException();
                }
            }
        }
    }
}
