package herdergames.tic_tac_toe;

import processing.core.PApplet;

import java.util.Optional;

record Position(int zeile, int spalte) {
    static Optional<Position> create(int zeile, int spalte) {
        if (!isValid(zeile, spalte)) {
            return Optional.empty();
        }
        return Optional.of(new Position(zeile, spalte));
    }

    static Optional<Position> fromMausPosition(PApplet applet) {
        int mouseXOffset = applet.mouseX - Brett.getAbstandHorizontal(applet);
        int mouseYOffset = applet.mouseY - Brett.getAbstandVertikal(applet);
        int spalte = mouseXOffset / Brett.getFeldSize(applet);
        int zeile = mouseYOffset / Brett.getFeldSize(applet);
        return create(zeile, spalte);
    }

    static boolean isValidZeile(int zeile) {
        return zeile >= 0 && zeile < Brett.SIZE;
    }

    static boolean isValidSpalte(int spalte) {
        return spalte >= 0 && spalte < Brett.SIZE;
    }

    static boolean isValid(int zeile, int spalte) {
        return isValidZeile(zeile) && isValidSpalte(spalte);
    }

    Position {
        if (!isValid(zeile, spalte)) {
            throw new IllegalArgumentException();
        }
    }
}
