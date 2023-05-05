package herdergames.dame;

import processing.core.PApplet;

import java.util.Optional;

record Position(int zeile, int spalte) {
    static boolean isValid(int zeile, int spalte) {
        return zeile >= 0 && zeile < Brett.SIZE && spalte >= 0 && spalte < Brett.SIZE && zeile % 2 == spalte % 2;
    }

    static Optional<Position> fromMousePosition(PApplet applet) {
        int mouseXOffset = applet.mouseX - Brett.calculateAbstandX(applet);
        int mouseYOffset = applet.mouseY - Brett.calculateAbstandY(applet);
        int feldSize = Brett.calculateFeldSize(applet);
        int zeile = mouseYOffset / feldSize;
        int spalte = mouseXOffset / feldSize;
        if (!isValid(zeile, spalte)) {
            return Optional.empty();
        }
        return Optional.of(new Position(zeile, spalte));
    }

    Position {
        if (!isValid(zeile, spalte)) {
            throw new IllegalArgumentException();
        }
    }

    Optional<Position> add(int zeilen, int spalten) {
        if (!isValid(zeile + zeilen, spalte + spalten)) {
            return Optional.empty();
        }
        return Optional.of(new Position(zeile + zeilen, spalte + spalten));
    }
}
