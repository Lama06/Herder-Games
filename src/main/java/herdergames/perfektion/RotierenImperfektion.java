package herdergames.perfektion;

import processing.core.PApplet;

final class RotierenImperfektion extends Imperfektion {
    static final ImperfektionFactory FACTORY = new ImperfektionFactory() {
        @Override
        public Imperfektion mitZufallswerten(PApplet applet, Form form) {
            if (!(form instanceof RechteckForm rechteck)) {
                throw new IllegalArgumentException();
            }
            return new RotierenImperfektion(applet, rechteck);
        }

        @Override
        public boolean istMitFormKompatibel(Form form) {
            return form instanceof RechteckForm;
        }
    };

    private static final float GESCHWINDIGKEIT = 0.01f;

    private final RechteckForm rechteck;
    private final int richtung = applet.random(1f) <= 0.5f ? -1 : 1;

    private RotierenImperfektion(PApplet applet, RechteckForm rechteck) {
        super(applet);
        this.rechteck = rechteck;
    }

    @Override
    void draw() {
        rechteck.rotation += GESCHWINDIGKEIT * richtung;
    }
}
