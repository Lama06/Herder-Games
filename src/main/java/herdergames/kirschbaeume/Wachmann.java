package herdergames.kirschbaeume;

import herdergames.util.Rechteck;
import processing.core.PApplet;
import processing.core.PConstants;

final class Wachmann {
    private static final float MIN_ENTFERNUNG_ZU_SPIELER = 3;
    private static final int BEWEGUNG_AENDERN_DELAY = 180;
    private static final float GESCHWINDIGKEIT = 0.01f;

    private final KirschbaumSpiel spiel;
    private final PApplet applet;
    private int xRichtung;
    private int yRichtung;
    private int naechsteBewegung = 0;
    private float x;
    private float y;

    Wachmann(KirschbaumSpiel spiel) {
        this.spiel = spiel;
        applet = spiel.applet;

        while (true) {
            x = applet.random(KirschbaumSpiel.ANZAHL_FELDER);
            if (Math.abs(x - Spieler.START_X) < MIN_ENTFERNUNG_ZU_SPIELER) {
                continue;
            }
            break;
        }

        while (true) {
            y = applet.random(KirschbaumSpiel.ANZAHL_FELDER);
            if (Math.abs(y - Spieler.START_Y) < MIN_ENTFERNUNG_ZU_SPIELER) {
                continue;
            }
            break;
        }
    }

    private void richtungAendern() {
        if (naechsteBewegung-- < 0) {
            naechsteBewegung = BEWEGUNG_AENDERN_DELAY;
            xRichtung = applet.random(1) > 0.5 ? 1 : -1;
            yRichtung = applet.random(1) > 0.5 ? 1 : -1;
        }
    }

    private void bewegen() {
        x += xRichtung * GESCHWINDIGKEIT;
        y += yRichtung * GESCHWINDIGKEIT;
    }

    void draw() {
        richtungAendern();
        bewegen();

        float x = spiel.getFeldPositionX(this.x);
        float y = spiel.getFeldPositionY(this.y);
        float feldSize = spiel.getFeldSize();

        applet.imageMode(PConstants.CORNER);
        applet.image(KirschbaumSpiel.wachmannBild, x, y, feldSize, feldSize);
    }

    Rechteck getHitbox() {
        return new Rechteck(x, y, 1, 1);
    }
}
