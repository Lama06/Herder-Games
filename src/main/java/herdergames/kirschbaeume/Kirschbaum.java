package herdergames.kirschbaeume;

import herdergames.util.Rechteck;
import processing.core.PApplet;
import processing.core.PConstants;

final class Kirschbaum {
    static int getKirschbaumStammFarbe(PApplet applet) {
        return applet.color(100, 80, 50);
    }

    static void zeichnen(PApplet applet, float x, float y, float size) {
        applet.rectMode(PConstants.CORNERS);
        applet.ellipseMode(PConstants.CENTER);

        applet.fill(getKirschbaumStammFarbe(applet));
        applet.rect(x + size / 2 - size / 15, y + size / 3, x + size / 2 + size / 15, y + size / 3 + size / 15 * 10);

        applet.fill(0, 100, 0);
        applet.circle(x + size / 2, y + size / 3, 10 * (size / 15));

        applet.fill(250, 150, 250);
        applet.circle(x + size / 2, y + size / 3, size / 15);
        applet.circle(x + size / 2 + 3.5f * (size / 15), y + size / 3, size / 15);
        applet.circle(x + size / 2 - 3.5f * (size / 15), y + size / 3, size / 15);
        applet.circle(x + size / 2, y + size / 3 + 3.5f * (size / 15), size / 15);
        applet.circle(x + size / 2, y + size / 3 - 3.5f * (size / 15), size / 15);
        applet.circle(x + size / 2 + 2 * (size / 15), y + size / 3 + 2 * (size / 15), size / 15);
        applet.circle(x + size / 2 + 2 * (size / 15), y + size / 3 - 2 * (size / 15), size / 15);
        applet.circle(x + size / 2 - 2 * (size / 15), y + size / 3 + 2 * (size / 15), size / 15);
        applet.circle(x + size / 2 - 2 * (size / 15), y + size / 3 - 2 * (size / 15), size / 15);
    }

    private final KirschbaumSpiel kirschbaumSpiel;
    private final PApplet applet;
    private final int x;
    private final int y;

    Kirschbaum(KirschbaumSpiel spiel) {
        this.kirschbaumSpiel = spiel;
        this.applet = spiel.applet;
        x = applet.choice(KirschbaumSpiel.ANZAHL_FELDER);
        y = applet.choice(KirschbaumSpiel.ANZAHL_FELDER);
    }

    void draw() {
        float x = kirschbaumSpiel.getFeldPositionX(this.x);
        float y = kirschbaumSpiel.getFeldPositionY(this.y);
        float size = kirschbaumSpiel.getFeldSize();

        zeichnen(applet, x, y, size);
    }

    Rechteck getHitbox() {
        return new Rechteck(x, y, 1, 1);
    }

    boolean kollisionMitSpieler() {
        return getHitbox().kollidiertMit(kirschbaumSpiel.spieler.getKirschbaumHitbox());
    }
}