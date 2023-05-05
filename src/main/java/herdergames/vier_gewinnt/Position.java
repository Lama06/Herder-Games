package herdergames.vier_gewinnt;

import processing.core.PApplet;

import java.util.Optional;
import java.util.OptionalInt;

record Position(int zeile, int spalte) {
    static boolean isValidZeile(int zeile) {
        return zeile >= 0 && zeile < Brett.HEIGHT;
    }

    static boolean isValidSpalte(int spalte) {
        return spalte >= 0 && spalte < Brett.WIDTH;
    }

    static boolean isValid(int zeile, int spalte) {
        return isValidZeile(zeile) && isValidSpalte(spalte);
    }

    static Optional<Position> create(int zeile, int spalte) {
        if (!isValid(zeile, spalte)) {
            return Optional.empty();
        }
        return Optional.of(new Position(zeile, spalte));
    }

    static OptionalInt spalteFromMausPosition(PApplet applet) {
        int abstandHorizontal = Brett.getAbstandHorizontal(applet);
        int steinSize = Brett.getFeldSize(applet);
        int mouseXOffset = applet.mouseX - abstandHorizontal;
        int spalte = mouseXOffset / steinSize;
        if (!isValidSpalte(spalte)) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(spalte);
    }

    Position {
        if (!isValid(zeile, spalte)) {
            throw new IllegalArgumentException();
        }
    }

    Optional<Position> add(int zeilen, int spalten) {
        return create(zeile + zeilen, spalte + spalten);
    }
}
