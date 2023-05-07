package herdergames.pacman;

import herdergames.util.Rechteck;
import processing.core.PConstants;

record SuperPunkt(PacmanSpiel spiel, int x, int y) {
    private static final int SIZE = 2;

    void draw() {
        spiel.applet.rectMode(PConstants.CORNER);
        spiel.applet.noStroke();
        spiel.applet.fill(spiel.applet.color(255, 0, 0));
        spiel.applet.rect(x, y, SIZE, SIZE);
    }

    Rechteck getHitbox() {
        return new Rechteck(x, y, SIZE, SIZE);
    }
}
