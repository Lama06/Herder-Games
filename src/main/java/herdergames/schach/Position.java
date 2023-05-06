package herdergames.schach;

import processing.core.PApplet;

import java.util.Optional;

record Position(int zeile, int spalte) {
    static boolean isValid(int zeile, int spalte) {
        return zeile >= 0 && zeile < Brett.SIZE && spalte >= 0 && spalte < Brett.SIZE;
    }

    static Optional<Position> create(int zeile, int spalte) {
        if (!isValid(zeile, spalte)) {
            return Optional.empty();
        }
        return Optional.of(new Position(zeile, spalte));
    }

    static Optional<Position> fromMousePosition(PApplet applet) {
        int mouseXOffset = applet.mouseX - Brett.getAbstandHorizontal(applet);
        int mouseYOffset = applet.mouseY - Brett.getAbstandVertikal(applet);
        int feldSize = Brett.getFeldSize(applet);
        int zeile = mouseYOffset / feldSize;
        int spalte = mouseXOffset / feldSize;
        return Position.create(zeile, spalte);
    }

    Position {
        if (!isValid(zeile, spalte)) {
            throw new IllegalArgumentException();
        }
    }

    Optional<Position> add(int zeilen, int spalten) {
        return create(zeile + zeilen, spalte + spalten);
    }

    boolean isSchwarz() {
        return zeile % 2 == spalte % 2;
    }
}
