package herdergames;

import processing.core.PApplet;

abstract class Screen {
    protected final HerderGames herderGames;
    protected final PApplet applet;

    protected Screen(HerderGames herderGames) {
        this.herderGames = herderGames;
        applet = herderGames.getApplet();
    }

    abstract void draw();

    void keyPressed() { }

    void keyReleased() { }

    void mousePressed() { }

    void mouseReleased() { }
}
