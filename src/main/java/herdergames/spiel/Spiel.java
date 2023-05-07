package herdergames.spiel;

import processing.core.PApplet;

public abstract sealed class Spiel permits EinzelspielerSpiel, SpielerGegenSpielerSpiel, MehrspielerSpiel {
    public final PApplet applet;

    protected Spiel(PApplet applet) {
        this.applet = applet;
    }

    public void mousePressed() { }

    public void keyPressed() { }

    public void keyReleased() { }

    public sealed interface Factory permits EinzelspielerSpiel.Factory, SpielerGegenSpielerSpiel.Factory, MehrspielerSpiel.Factory { }
}
