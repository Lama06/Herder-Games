package herdergames.perfektion;

import processing.core.PApplet;

final class FarbeImperfektion extends Imperfektion {
    static final ImperfektionFactory FACTORY = new ImperfektionFactory() {
        @Override
        public Imperfektion mitZufallswerten(PApplet applet, Form form) {
            return new FarbeImperfektion(applet, form);
        }

        @Override
        public boolean istMitFormKompatibel(Form form) {
            return true;
        }
    };

    private static final float GESCHWINDIGKEIT = 0.001f;

    private final Form form;
    private final int startFarbe;
    private final int zielFarbe;
    private float prozent;

    private FarbeImperfektion(PApplet applet, Form form) {
        super(applet);
        this.form = form;
        startFarbe = form.farbe;
        zielFarbe = applet.color(applet.choice(255), applet.choice(255), applet.choice(255));
    }

    @Override
    void draw() {
        prozent += GESCHWINDIGKEIT;
        form.farbe = applet.lerpColor(startFarbe, zielFarbe, prozent);
    }
}
