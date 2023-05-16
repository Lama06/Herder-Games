package herdergames.crossy_road;

import herdergames.util.Rechteck;
import processing.core.PConstants;

import java.util.Collections;
import java.util.List;

final class Wiese extends Zeile {
    Wiese(CrossyRoad spiel, int y) {
        super(spiel, y);
    }

    @Override
    void draw() {
        applet.rectMode(PConstants.CORNER);
        applet.noStroke();
        applet.fill(0, 204, 0);
        applet.rect(0, y, CrossyRoad.BREITE, 1);
    }

    @Override
    List<Rechteck> getHitboxen() {
        return Collections.emptyList();
    }
}
