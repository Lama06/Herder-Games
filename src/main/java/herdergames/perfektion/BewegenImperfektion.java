package herdergames.perfektion;

import processing.core.PApplet;

final class BewegenImperfektion extends Imperfektion {
    static final ImperfektionFactory FACTORY = new ImperfektionFactory() {
        @Override
        public Imperfektion mitZufallswerten(PApplet applet, Form form) {
            return new BewegenImperfektion(applet, form);
        }

        @Override
        public boolean istMitFormKompatibel(Form form) {
            return true;
        }
    };

    private static final float GESCHWINDIGKEIT = 0.00002f;

    private final int xRichtung = applet.random(1f) <= 0.5f ? -1 : 1;
    private final int yRichtung = applet.random(1f) <= 0.5f ? -1 : 1;

    private final Form form;

    private BewegenImperfektion(PApplet applet, Form form) {
        super(applet);
        this.form = form;
    }

    @Override
    void draw() {
        form.x += GESCHWINDIGKEIT * xRichtung;
        form.y += GESCHWINDIGKEIT * yRichtung;
    }
}
