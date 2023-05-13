package herdergames.schiffe_versenken;

import processing.core.PApplet;
import processing.core.PConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

record UnfertigesSpielBrett(List<List<Boolean>> zeilen) {
    static final UnfertigesSpielBrett LEER = createLeer();

    private static UnfertigesSpielBrett createLeer() {
        List<List<Boolean>> zeilen = new ArrayList<>();
        for (int zeileIndex = 0; zeileIndex < SpielBrett.SIZE; zeileIndex++) {
            List<Boolean> zeile = new ArrayList<>();
            zeilen.add(zeile);
            for (int spalteIndex = 0; spalteIndex < SpielBrett.SIZE; spalteIndex++) {
                zeile.add(false);
            }
        }
        return new UnfertigesSpielBrett(zeilen);
    }

    UnfertigesSpielBrett(List<List<Boolean>> zeilen) {
        if (zeilen.size() != SpielBrett.SIZE) {
            throw new IllegalArgumentException();
        }

        for (List<Boolean> zeile : zeilen) {
            if (zeile.size() != SpielBrett.SIZE) {
                throw new IllegalArgumentException();
            }
        }

        List<List<Boolean>> zeilenKopien = new ArrayList<>();
        for (List<Boolean> zeile : zeilen) {
            zeilenKopien.add(List.copyOf(zeile));
        }
        this.zeilen = Collections.unmodifiableList(zeilenKopien);
    }

    UnfertigesSpielBrett schiffToggeln(Position position) {
        List<List<Boolean>> neueZeilen = new ArrayList<>();
        for (int zeile = 0; zeile < SpielBrett.SIZE; zeile++) {
            if (position.zeile() == zeile) {
                List<Boolean> neueZeile = new ArrayList<>(zeilen.get(zeile));
                neueZeile.set(position.spalte(), !neueZeile.get(position.spalte()));
                neueZeilen.add(neueZeile);
                continue;
            }

            neueZeilen.add(zeilen.get(zeile));
        }
        return new UnfertigesSpielBrett(neueZeilen);
    }

    private List<List<Feld>> zuFelderZeilen() {
        List<List<Feld>> neueZeilen = new ArrayList<>();
        for (List<Boolean> zeile : zeilen) {
            List<Feld> neueZeile = new ArrayList<>();
            neueZeilen.add(neueZeile);
            for (boolean schiff : zeile) {
                if (schiff) {
                    neueZeile.add(new Feld.Schiff(false));
                } else {
                    neueZeile.add(new Feld.Leer(false));
                }
            }
        }
        return neueZeilen;
    }

    boolean sindSchiffeLegal() {
        return SpielBrett.sindSchiffeLegal(zuFelderZeilen());
    }

    SpielBrett umwandeln() {
        List<List<Feld>> felderZeilen = zuFelderZeilen();
        if (!sindSchiffeLegal()) {
            throw new IllegalStateException();
        }
        return new SpielBrett(felderZeilen);
    }

    void draw(PApplet applet, float xStart, float yStart, float feldSize) {
        for (int zeile = 0; zeile < SpielBrett.SIZE; zeile++) {
            for (int spalte = 0; spalte < SpielBrett.SIZE; spalte++) {
                float x = xStart + feldSize * spalte;
                float y = yStart + feldSize * zeile;

                boolean schiff = zeilen.get(zeile).get(spalte);
                if (schiff) {
                    applet.fill(0);
                } else {
                    applet.noFill();
                }

                applet.rectMode(PConstants.CORNER);
                applet.stroke(0);
                applet.strokeWeight(2);
                applet.rect(x, y, feldSize, feldSize);
            }
        }
    }

    UnfertigesSpielBrett handleMousePressed(float xOffset, float yOffset, float feldSize) {
        Optional<Position> mausPosition = Position.fromMausPosition(xOffset, yOffset, feldSize);
        return mausPosition.map(this::schiffToggeln).orElse(this);
    }
}
