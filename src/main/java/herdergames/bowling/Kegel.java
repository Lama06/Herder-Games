package herdergames.bowling;

import herdergames.util.Rechteck;
import processing.core.PApplet;
import processing.core.PConstants;

final class Kegel {
    static final float BREITE = 0.025f;
    static final float HOEHE = 0.05f;

    private final Bahn bahn;
    private final PApplet applet;
    private final float x;
    private final float y;

    Kegel(Bahn bahn, float x, float y) {
        this.bahn = bahn;
        applet = bahn.applet;
        this.x = x;
        this.y = y;
    }

    void draw(float x, float breite) {
        applet.imageMode(PConstants.CORNER);
        applet.image(Bowling.kegelBild, x + this.x * breite, y * applet.height, BREITE * breite, HOEHE * applet.height);
    }

    boolean istGetroffen() {
        return bahn.kugel.getHitbox().kollidiertMit(getHitbox());
    }

    private Rechteck getHitbox() {
        return new Rechteck(x, y, BREITE, HOEHE);
    }
}
