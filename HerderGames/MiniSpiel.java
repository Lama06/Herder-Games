import processing.core.PApplet;

abstract class MiniSpiel {
    final PApplet applet;

    MiniSpiel(PApplet applet) {
        this.applet = applet;
    }

    abstract void draw();

    void mousePressed() { }

    void keyPressed() { }
}
