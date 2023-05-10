package herdergames.perfektion;

import processing.core.PApplet;

enum FormType {
    KREIS(KreisForm.FACTORY),
    RECHTECK(RechteckForm.FACTORY);

    static FormType zufaellig(PApplet applet) {
        return values()[applet.choice(values().length)];
    }

    final FormFactory factory;

    FormType(FormFactory factory) {
        this.factory = factory;
    }
}
