package herdergames.tic_tac_toe;

import herdergames.spiel.Spiel;
import herdergames.ai.AI;
import processing.core.PApplet;

import java.util.*;

public final class TicTacToe {
    private TicTacToe() {}

    private enum Spieler implements herdergames.ai.Spieler<Spieler> {
        KREUZ {
            @Override
            void drawSymbol(PApplet applet, int x, int y, int size) {
                applet.stroke(255);
                applet.strokeWeight(5);
                applet.line(x, y, x+size, y+size);
                applet.line(x+size, y, x, y+size);
            }
        },
        KREIS {
            @Override
            void drawSymbol(PApplet applet, int x, int y, int size) {
                applet.stroke(255);
                applet.noFill();
                applet.strokeWeight(5);
                applet.ellipseMode(PApplet.CORNER);
                applet.ellipse(x, y, size, size);
            }
        };

        abstract void drawSymbol(PApplet applet, int x, int y, int size);

        @Override
        public Spieler getGegner() {
            switch (this) {
                case KREIS:
                    return KREUZ;
                case KREUZ:
                    return KREIS;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    private static final class Brett implements herdergames.ai.Brett<Brett, Zug, Spieler> {
        private static final int SIZE = 3;

        private static final Brett LEER = createLeer();
        private static Brett createLeer() {
            return new Brett(List.of(
                    List.of(Optional.empty(), Optional.empty(), Optional.empty()),
                    List.of(Optional.empty(), Optional.empty(), Optional.empty()),
                    List.of(Optional.empty(), Optional.empty(), Optional.empty())
            ));
        }

        private final List<List<Optional<Spieler>>> zeilen;

        private Brett(List<List<Optional<Spieler>>> zeilen) {
            if (zeilen.size() != SIZE) {
                throw new IllegalArgumentException();
            }

            for (List<Optional<Spieler>> zeile : zeilen) {
                if (zeile.size() != SIZE) {
                    throw new IllegalArgumentException();
                }
            }

            List<List<Optional<Spieler>>> zeilenKopien = new ArrayList<>(SIZE);
            for (List<Optional<Spieler>> zeile : zeilen) {
                zeilenKopien.add(List.copyOf(zeile));
            }

            this.zeilen = List.copyOf(zeilenKopien);
        }

        private Optional<Spieler> getFeld(Position position) {
            return zeilen.get(position.zeile).get(position.spalte);
        }

        private Brett mitFeld(Position position, Optional<Spieler> feld) {
            List<List<Optional<Spieler>>> neueZeilen = new ArrayList<>(SIZE);
            for (int zeile = 0; zeile < SIZE; zeile++) {
                if (position.zeile == zeile) {
                    List<Optional<Spieler>> neueZeile = new ArrayList<>(zeilen.get(zeile));
                    neueZeile.set(position.spalte, feld);
                    neueZeilen.add(neueZeile);
                } else {
                    neueZeilen.add(zeilen.get(zeile));
                }
            }
            return new Brett(neueZeilen);
        }

        private boolean hatGewonnenInZeile(Spieler spieler, int zeile) {
            if (!Position.isValidZeile(zeile)) {
                throw new IllegalArgumentException();
            }

            for (int spalte = 0; spalte < SIZE; spalte++) {
                Position position = new Position(zeile, spalte);
                Optional<Spieler> feld = getFeld(position);
                if (feld.isEmpty() || feld.get() != spieler) {
                    return false;
                }
            }

            return true;
        }

        private boolean hatGewonnenInZeilen(Spieler spieler) {
            for (int zeile = 0; zeile < SIZE; zeile++) {
                if (hatGewonnenInZeile(spieler, zeile)) {
                    return true;
                }
            }

            return false;
        }

        private boolean hatGewonnenInSpalte(Spieler spieler, int spalte) {
            if (!Position.isValidSpalte(spalte)) {
                throw new IllegalArgumentException();
            }

            for (int zeile = 0; zeile < SIZE; zeile++) {
                Position position = new Position(zeile, spalte);
                Optional<Spieler> feld = getFeld(position);
                if (feld.isEmpty() || feld.get() != spieler) {
                    return false;
                }
            }

            return true;
        }

        private boolean hatGewonnenInSpalten(Spieler spieler) {
            for (int spalte = 0; spalte < SIZE; spalte++) {
                if (hatGewonnenInSpalte(spieler, spalte)) {
                    return true;
                }
            }

            return false;
        }

        private boolean hatGewonnenInDiagonale(Spieler spieler, List<Position> diagonale) {
            for (Position position : diagonale) {
                Optional<Spieler> feld = getFeld(position);
                if (feld.isEmpty() || feld.get() != spieler) {
                    return false;
                }
            }

            return true;
        }

        private boolean hatGewonnenDiagonal(Spieler spieler) {
            final List<List<Position>> diagonalen = List.of(
                List.of(new Position(0, 0), new Position(1, 1), new Position(2, 2)),
                List.of(new Position(0, 2), new Position(1, 1), new Position(2, 0))
            );

            for (List<Position> diagonale : diagonalen) {
                if (hatGewonnenInDiagonale(spieler, diagonale)) {
                    return true;
                }
            }

            return false;
        }

        private boolean hatGewonnen(Spieler spieler) {
            return hatGewonnenInZeilen(spieler) || hatGewonnenInSpalten(spieler) || hatGewonnenDiagonal(spieler);
        }

        @Override
        public Set<Zug> getMoeglicheZuegeFuerSpieler(Spieler spieler) {
            if (hatGewonnen(Spieler.KREIS) || hatGewonnen(Spieler.KREUZ)) {
                return Collections.emptySet();
            }

            Set<Zug> result = new HashSet<>();

            for (int zeile = 0; zeile < SIZE; zeile++) {
                for (int spalte = 0; spalte < SIZE; spalte++) {
                    Position position = new Position(zeile, spalte);
                    Optional<Spieler> feld = getFeld(position);

                    if (feld.isPresent()) {
                        continue;
                    }

                    Brett neuesBrett = mitFeld(position, Optional.of(spieler));
                    result.add(new Zug(position, neuesBrett));
                }
            }

            return result;
        }

        @Override
        public int getBewertung(Spieler perspektive) {
            final int GEWONNEN_BEWERTUNG = 1000;
            final int VERLOREN_BEWERTUNG = -1000;

            if (hatGewonnen(perspektive)) {
                return GEWONNEN_BEWERTUNG;
            }

            if (hatGewonnen(perspektive.getGegner())) {
                return VERLOREN_BEWERTUNG;
            }

            return 0;
        }

        private static int getSize(PApplet applet) {
            return Math.min(applet.width, applet.height);
        }

        private static int getAbstandHorizontal(PApplet applet) {
            return (applet.width - getSize(applet)) / 2;
        }

        private static int getAbstandVertikal(PApplet applet) {
            return (applet.height - getSize(applet)) / 2;
        }

        private static int getFeldSize(PApplet applet) {
            return getSize(applet) / SIZE;
        }

        private static int getSymbolSize(PApplet applet) {
            return (getFeldSize(applet) / 3) * 2;
        }

        private static int getSymbolAbstand(PApplet applet) {
            return (getFeldSize(applet) - getSymbolSize(applet)) / 2;
        }

        private void drawLinien(PApplet applet) {
            int abstandHorizontal = getAbstandHorizontal(applet);
            int abstandVertikal = getAbstandVertikal(applet);
            int brettSize = getSize(applet);
            int feldSize = getFeldSize(applet);

            applet.strokeWeight(5);
            applet.stroke(applet.color(255));

            for (int zeile = 1; zeile < SIZE; zeile++) {
                int linieXStart = abstandHorizontal;
                int linieXEnde = abstandHorizontal + brettSize;
                int linieY = abstandVertikal + feldSize*zeile;
                applet.line(linieXStart, linieY, linieXEnde, linieY);
            }

            for (int spalte = 1; spalte < SIZE; spalte++) {
                int linieYStart = abstandVertikal;
                int linieYEnde = abstandVertikal + brettSize;
                int linieX = abstandHorizontal + feldSize*spalte;
                applet.line(linieX, linieYStart, linieX, linieYEnde);
            }
        }

        private void drawSymbole(PApplet applet) {
            int abstandHorizontal = getAbstandHorizontal(applet);
            int abstandVertikal = getAbstandVertikal(applet);
            int feldSize = getFeldSize(applet);
            int symbolSize = getSymbolSize(applet);
            int symbolAbstand = getSymbolAbstand(applet);

            for (int zeile = 0; zeile < SIZE; zeile++) {
                for (int spalte = 0; spalte < SIZE; spalte++) {
                    Position position = new Position(zeile, spalte);
                    Optional<Spieler> feld = getFeld(position);
                    if (feld.isEmpty()) {
                        continue;
                    }

                    int feldX = abstandHorizontal + spalte*feldSize;
                    int feldY = abstandVertikal + zeile*feldSize;
                    int symbolX = feldX + symbolAbstand;
                    int symbolY = feldY + symbolAbstand;
                    feld.get().drawSymbol(applet, symbolX, symbolY, symbolSize);
                }
            }
        }

        private void draw(PApplet applet) {
            applet.background(11, 22, 10);
            drawLinien(applet);
            drawSymbole(applet);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Brett brett = (Brett) o;
            return Objects.equals(zeilen, brett.zeilen);
        }

        @Override
        public int hashCode() {
            return Objects.hash(zeilen);
        }
    }

    private static final class Position {
        private static Optional<Position> create(int zeile, int spalte) {
            if (!isValid(zeile, spalte)) {
                return Optional.empty();
            }
            return Optional.of(new Position(zeile, spalte));
        }

        private static Optional<Position> fromMausPosition(PApplet applet) {
            int mouseXOffset = applet.mouseX - Brett.getAbstandHorizontal(applet);
            int mouseYOffset = applet.mouseY - Brett.getAbstandVertikal(applet);
            int spalte = mouseXOffset / Brett.getFeldSize(applet);
            int zeile = mouseYOffset / Brett.getFeldSize(applet);
            return create(zeile, spalte);
        }

        private static boolean isValidZeile(int zeile) {
            return zeile >= 0 && zeile < Brett.SIZE;
        }

        private static boolean isValidSpalte(int spalte) {
            return spalte >= 0 && spalte < Brett.SIZE;
        }

        private static boolean isValid(int zeile, int spalte) {
            return isValidZeile(zeile) && isValidSpalte(spalte);
        }

        private final int zeile;
        private final int spalte;

        private Position(int zeile, int spalte) {
            if (!isValid(zeile, spalte)) {
                throw new IllegalArgumentException();
            }

            this.zeile = zeile;
            this.spalte = spalte;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return zeile == position.zeile && spalte == position.spalte;
        }

        @Override
        public int hashCode() {
            return Objects.hash(zeile, spalte);
        }
    }

    private static final class Zug implements herdergames.ai.Zug<Brett> {
        private final Position position;
        private final Brett ergebnis;

        private Zug(Position position, Brett ergebnis) {
            this.position = position;
            this.ergebnis = ergebnis;
        }

        @Override
        public Brett getErgebnis() {
            return ergebnis;
        }
    }

    public static final class SpielerGegenSpielerSpiel extends Spiel.SpielerGegenSpieler {
        private final Spieler spielerKreuz;
        private final Spieler spielerKreis;
        private Brett aktuellesBrett = Brett.LEER;
        private TicTacToe.Spieler amZug = TicTacToe.Spieler.KREUZ;

        public SpielerGegenSpielerSpiel(PApplet applet, Spieler spieler1, Spieler spieler2) {
            super(applet);
            if (spieler1.punkte < spieler2.punkte) {
                spielerKreuz = spieler1;
                spielerKreis = spieler2;
            } else {
                spielerKreuz = spieler2;
                spielerKreis = spieler1;
            }
        }

        @Override
        public void mousePressed() {
            Optional<Position> mausPosition = Position.fromMausPosition(applet);
            if (mausPosition.isEmpty()) {
                return;
            }

            Set<Zug> moeglicheZuege = aktuellesBrett.getMoeglicheZuegeFuerSpieler(amZug);
            for (Zug moeglicherZug : moeglicheZuege) {
                if (moeglicherZug.position.equals(mausPosition.get())) {
                    aktuellesBrett = moeglicherZug.ergebnis;
                    amZug = amZug.getGegner();
                    return;
                }
            }
        }

        @Override
        public Optional<Optional<Spieler.Id>> draw() {
            aktuellesBrett.draw(applet);

            if (aktuellesBrett.hatGewonnen(TicTacToe.Spieler.KREUZ)) {
                return Optional.of(Optional.of(spielerKreuz.id));
            }
            if (aktuellesBrett.hatGewonnen(TicTacToe.Spieler.KREIS)) {
                return Optional.of(Optional.of(spielerKreis.id));
            }
            if (aktuellesBrett.getMoeglicheZuegeFuerSpieler(amZug).isEmpty()) {
                return Optional.of(Optional.empty());
            }

            return Optional.empty();
        }
    }

    public static final class SpielerGegenAISpiel extends Spiel.Einzelspieler {
        private static final int AI_TIEFE = 9; // Ein Tic Tac Toe Spiel ist nach spätestens 9 Zügen beendet, weil dann das Brett voll ist
        private static final TicTacToe.Spieler MENSCH = TicTacToe.Spieler.KREIS;
        private static final TicTacToe.Spieler COMPUTER = TicTacToe.Spieler.KREUZ;
        private Brett aktuellesBrett;

        public SpielerGegenAISpiel(PApplet applet, Spieler spieler) {
            super(applet);

            Optional<Zug> ersterZug = AI.bestenNaechstenZugBerechnen(Brett.LEER, COMPUTER, AI_TIEFE);
            if (ersterZug.isEmpty()) {
                // Das sollte niemals passieren, weil der Computer immer einen Zug findet, wenn er anfängt
                throw new IllegalStateException();
            }
            aktuellesBrett = ersterZug.get().ergebnis;
        }

        @Override
        public void mousePressed() {
            Optional<Position> mausPosition = Position.fromMausPosition(applet);
            if (mausPosition.isEmpty()) {
                return;
            }

            Set<Zug> moeglicheZuege = aktuellesBrett.getMoeglicheZuegeFuerSpieler(MENSCH);
            for (Zug moeglicherZug : moeglicheZuege) {
                if (moeglicherZug.position.equals(mausPosition.get())) {
                    Optional<Zug> antwort = AI.bestenNaechstenZugBerechnen(moeglicherZug.ergebnis, COMPUTER, AI_TIEFE);
                    if (antwort.isEmpty()) {
                        aktuellesBrett = moeglicherZug.ergebnis;
                        return;
                    }

                    aktuellesBrett = antwort.get().ergebnis;
                    return;
                }
            }
        }

        @Override
        public Optional<Ergebnis> draw() {
            aktuellesBrett.draw(applet);

            if (aktuellesBrett.hatGewonnen(COMPUTER)) {
                return Optional.of(Ergebnis.VERLOREN);
            }
            if (aktuellesBrett.hatGewonnen(MENSCH)) {
                return Optional.of(Ergebnis.GEWONNEN);
            }
            return Optional.empty();
        }
    }
}
