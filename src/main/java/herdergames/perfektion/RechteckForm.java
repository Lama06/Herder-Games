package herdergames.perfektion;

import processing.core.PApplet;
import processing.core.PConstants;

final class RechteckForm extends Form {
    static final FormFactory FACTORY = new FormFactory() {
        @Override
        public Form mitZufallswerten(PApplet applet, float x, float y, float breite, float hoehe) {
            return new RechteckForm(applet, x, y, breite, hoehe);
        }

        @Override
        public Form kopieVon(Form andereForm, PApplet applet, float x, float y, float breite, float hoehe) {
            if (!(andereForm instanceof RechteckForm rechteck)) {
                throw new IllegalArgumentException();
            }
            return new RechteckForm(rechteck, applet, x, y, breite, hoehe);
        }
    };

    private final float breiteProzent;
    private final float hoeheProzent;
    float rotation;

    private RechteckForm(PApplet applet, float x, float y, float breite, float hoehe) {
        super(applet, x, y, breite, hoehe);
        breiteProzent = applet.random(0.3f, 1f);
        hoeheProzent = applet.random(0.3f, 1f);
        if (applet.random(1) <= 0.4f) {
            rotation = applet.random(360);
        }
    }

    private RechteckForm(RechteckForm andereForm, PApplet applet, float x, float y, float breite, float hoehe) {
        super(andereForm, applet, x, y, breite, hoehe);
        breiteProzent = andereForm.breiteProzent;
        hoeheProzent = andereForm.hoeheProzent;
        rotation = andereForm.rotation;
    }

    @Override
    void draw() {
        applet.pushMatrix();

        applet.rectMode(PConstants.CENTER);
        applet.fill(farbe);
        applet.stroke(umrandungFarbe);
        applet.strokeWeight(umrandungDicke);
        applet.translate((x + breite/2) * applet.width, (y + hoehe/2) * applet.height);
        applet.rotate(PApplet.radians(rotation));
        applet.rect(0, 0, breite * breiteProzent * applet.width, hoehe * hoeheProzent * applet.height);

        applet.popMatrix();
    }
}
