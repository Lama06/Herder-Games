package herdergames.latein;

import processing.core.PApplet;

import java.util.Objects;

record NomenForm(Numerus numerus, Kasus kasus) {
    static final int ANZAHL = Numerus.values().length * Kasus.values().length;

    static NomenForm zufaellig(PApplet applet) {
        Numerus numerus = Numerus.values()[applet.choice(Numerus.values().length)];
        Kasus kasus = Kasus.values()[applet.choice(Kasus.values().length)];
        return new NomenForm(numerus, kasus);
    }

    NomenForm {
        Objects.requireNonNull(numerus);
        Objects.requireNonNull(kasus);
    }

    String zuWort(Nomen nomen, Adjektiv adjektiv) {
        String nomenForm = nomen.deklinieren(numerus, kasus);
        String adjektivForm = adjektiv.deklinieren(nomen.genus, numerus, kasus);
        return nomenForm + " " + adjektivForm;
    }

    @Override
    public String toString() {
        return kasus + " " + numerus;
    }
}
