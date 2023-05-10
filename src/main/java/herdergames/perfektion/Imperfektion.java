package herdergames.perfektion;

import processing.core.PApplet;

abstract class Imperfektion {
    final PApplet applet;

    Imperfektion(PApplet applet) {
        this.applet = applet;
    }

    abstract void draw();
}
