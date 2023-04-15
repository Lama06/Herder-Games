import processing.core.PApplet;

import java.util.*;

abstract class Spiel {
    final PApplet applet;

    Spiel(PApplet applet) {
        this.applet = applet;
    }

    void mousePressed() { }

    void keyPressed() { }

    void keyReleased() { }

    interface Factory { }

    abstract static class Einzelspieler extends Spiel {
        Einzelspieler(PApplet applet) {
            super(applet);
        }

        abstract Optional<Ergebnis> draw();

        enum Ergebnis {
            GEWONNEN,
            UNENTSCHIEDEN,
            VERLOREN
        }

        @FunctionalInterface
        interface Factory extends Spiel.Factory {
            Einzelspieler neuesSpiel(PApplet applet, Spieler spieler);
        }
    }

    abstract static class SpielerGegenSpieler extends Spiel {
        SpielerGegenSpieler(PApplet applet) {
            super(applet);
        }

        abstract Optional<Optional<Spieler.Id>> draw();

        @FunctionalInterface
        interface Factory extends Spiel.Factory {
            SpielerGegenSpieler neuesSpiel(PApplet applet, Spieler spieler1, Spieler spieler2);
        }
    }

    abstract static class Mehrspieler extends Spiel {
        Mehrspieler(PApplet applet) {
            super(applet);
        }

        abstract Optional<List<Spieler.Id>> draw();

        @FunctionalInterface
        interface Factory extends Spiel.Factory {
            Mehrspieler neuesSpiel(PApplet applet, Set<Spieler> spieler);
        }
    }

    static final class Spieler {
        final Id id;
        final String name;
        final int punkte;

        Spieler(Id id, String name, int punkte) {
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

        enum Id {
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
