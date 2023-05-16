package herdergames.crossy_road;

import herdergames.spiel.Spieler;
import herdergames.util.Rechteck;
import herdergames.util.Steuerung;
import processing.core.PApplet;
import processing.core.PConstants;

final class Huhn {
    private static final float SIZE = 0.8f;

    private final CrossyRoad spiel;
    private final PApplet applet;
    final Spieler spieler;
    private final int farbe;
    private int feldX =  CrossyRoad.BREITE / 2;
    private int feldY = CrossyRoad.HOEHE - 1;

    Huhn(CrossyRoad spiel, Spieler spieler) {
        this.spiel = spiel;
        applet = spiel.applet;
        this.spieler = spieler;
        farbe = applet.color(applet.choice(255), applet.choice(255), applet.choice(255));
    }

    private float getX() {
        return feldX + (1 - SIZE) / 2;
    }

    private float getY() {
        return feldY + (1 - SIZE) / 2;
    }

    void draw() {
        applet.ellipseMode(PConstants.CORNER);
        applet.fill(farbe);
        applet.noStroke();
        applet.circle(getX(), getY(), SIZE);
    }

    void keyPressed() {
        if (Steuerung.Richtung.OBEN.istTasteGedrueckt(applet, spieler.id())) {
            feldY--;
        }
        if (Steuerung.Richtung.UNTEN.istTasteGedrueckt(applet, spieler.id())) {
            feldY++;
        }
        if (Steuerung.Richtung.LINKS.istTasteGedrueckt(applet, spieler.id()) && feldX > 0) {
            feldX--;
        }
        if (Steuerung.Richtung.RECHTS.istTasteGedrueckt(applet, spieler.id()) && feldX < CrossyRoad.BREITE-1) {
            feldX++;
        }
    }

    private Rechteck getHitbox() {
        return new Rechteck(getX(), getY(), SIZE, SIZE);
    }

    boolean istTot() {
        for (Zeile zeile : spiel.zeilen) {
            for (Rechteck hitbox : zeile.getHitboxen()) {
                if (getHitbox().kollidiertMit(hitbox)) {
                    return true;
                }
            }
        }

        return spiel.istWeg(getY(), SIZE);
    }
}
