package herdergames.perfektion;

import herdergames.util.Rechteck;
import processing.core.PApplet;

abstract class Form {
    final PApplet applet;
    float x;
    float y;
    float breite;
    float hoehe;
    int farbe;
    int umrandungFarbe;
    int umrandungDicke;

    Form(PApplet applet, float x, float y, float breite, float hoehe) {
        this.applet = applet;
        this.x = x;
        this.y = y;
        this.breite = breite;
        this.hoehe = hoehe;
        farbe = applet.color(applet.choice(255), applet.choice(255), applet.choice(255));
        umrandungFarbe = applet.color(applet.choice(255), applet.choice(255), applet.choice(255));
        umrandungDicke = applet.choice(0, 4);
    }

    Form(Form andereForm, PApplet applet, float x, float y, float breite, float hoehe) {
        this.applet = applet;
        this.x = x;
        this.y = y;
        this.breite = breite;
        this.hoehe = hoehe;
        farbe = andereForm.farbe;
        umrandungDicke = andereForm.umrandungDicke;
        umrandungFarbe = andereForm.umrandungFarbe;
    }

    abstract void draw();

    Rechteck getHitbox() {
        return new Rechteck(x, y, breite, hoehe);
    }
}
