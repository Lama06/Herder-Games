package herdergames.perfektion;

import processing.core.PApplet;
import processing.core.PConstants;

final class KreisForm extends Form {
    static final FormFactory FACTORY = new FormFactory() {
        @Override
        public Form mitZufallswerten(PApplet applet, float x, float y, float breite, float hoehe) {
            return new KreisForm(applet, x, y, breite, hoehe);
        }

        @Override
        public Form kopieVon(Form andereForm, PApplet applet, float x, float y, float breite, float hoehe) {
            if (!(andereForm instanceof KreisForm kreis)) {
                throw new IllegalArgumentException();
            }
            return new KreisForm(kreis, applet, x, y, breite, hoehe);
        }
    };

    private KreisForm(PApplet applet, float x, float y, float breite, float hoehe) {
        super(applet, x, y, breite, hoehe);
    }

    private KreisForm(KreisForm andereForm, PApplet applet, float x, float y, float breite, float hoehe) {
        super(andereForm, applet, x, y, breite, hoehe);
    }

    @Override
    void draw() {
        applet.ellipseMode(PConstants.CENTER);
        applet.fill(farbe);
        applet.stroke(umrandungFarbe);
        applet.strokeWeight(umrandungDicke);
        float size = Math.min(applet.width * breite, applet.height * hoehe);
        applet.circle((x + breite/2) * applet.width, (y + hoehe/2) * applet.height, size);
    }
}
