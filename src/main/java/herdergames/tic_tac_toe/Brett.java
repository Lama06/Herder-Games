package herdergames.tic_tac_toe;

import processing.core.PApplet;

import java.util.*;

final class Brett implements herdergames.ai.Brett<Brett, Zug, Spieler> {
    static final int SIZE = 3;

    static final Brett LEER = createLeer();

    private static Brett createLeer() {
        return new Brett(List.of(
                List.of(Optional.empty(), Optional.empty(), Optional.empty()),
                List.of(Optional.empty(), Optional.empty(), Optional.empty()),
                List.of(Optional.empty(), Optional.empty(), Optional.empty())
        ));
    }

    static int getSize(PApplet applet) {
        return Math.min(applet.width, applet.height);
    }

    static int getAbstandHorizontal(PApplet applet) {
        return (applet.width - getSize(applet)) / 2;
    }

    static int getAbstandVertikal(PApplet applet) {
        return (applet.height - getSize(applet)) / 2;
    }

    static int getFeldSize(PApplet applet) {
        return getSize(applet) / SIZE;
    }

    static int getSymbolSize(PApplet applet) {
        return (getFeldSize(applet) / 3) * 2;
    }

    static int getSymbolAbstand(PApplet applet) {
        return (getFeldSize(applet) - getSymbolSize(applet)) / 2;
    }

    private final List<List<Optional<Spieler>>> zeilen;

    Brett(List<List<Optional<Spieler>>> zeilen) {
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
        return zeilen.get(position.zeile()).get(position.spalte());
    }

    private Brett mitFeld(Position position, Optional<Spieler> feld) {
        List<List<Optional<Spieler>>> neueZeilen = new ArrayList<>(SIZE);
        for (int zeile = 0; zeile < SIZE; zeile++) {
            if (position.zeile() == zeile) {
                List<Optional<Spieler>> neueZeile = new ArrayList<>(zeilen.get(zeile));
                neueZeile.set(position.spalte(), feld);
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

    boolean hatGewonnen(Spieler spieler) {
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
            int linieY = abstandVertikal + feldSize * zeile;
            applet.line(linieXStart, linieY, linieXEnde, linieY);
        }

        for (int spalte = 1; spalte < SIZE; spalte++) {
            int linieYStart = abstandVertikal;
            int linieYEnde = abstandVertikal + brettSize;
            int linieX = abstandHorizontal + feldSize * spalte;
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

                int feldX = abstandHorizontal + spalte * feldSize;
                int feldY = abstandVertikal + zeile * feldSize;
                int symbolX = feldX + symbolAbstand;
                int symbolY = feldY + symbolAbstand;
                feld.get().drawSymbol(applet, symbolX, symbolY, symbolSize);
            }
        }
    }

    void draw(PApplet applet) {
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
