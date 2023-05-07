package herdergames.break_out;

import processing.core.PApplet;

import java.util.List;

abstract class Entity {
    final Welt welt;
    final PApplet applet;

    Entity(Welt welt) {
        this.welt = welt;
        applet = welt.applet;
    }

    abstract List<Runnable> draw();

    void keyPressed() { }

    void keyReleased() { }
}
