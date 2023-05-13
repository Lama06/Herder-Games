package herdergames.schiffe_versenken;

import processing.core.PApplet;

abstract class Screen {
    protected final SchiffeVersenken spiel;
    protected final PApplet applet;

    protected Screen(SchiffeVersenken spiel) {
        this.spiel = spiel;
        applet = spiel.applet;
    }

    abstract void draw();

    void mousePressed() { }

    void keyPressed() { }

    void keyReleased() { }
}
