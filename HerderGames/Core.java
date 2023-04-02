import processing.core.PApplet;

abstract class Screen {
    final PApplet applet;

    Screen(PApplet applet) {
        this.applet = applet;
    }

    abstract Screen draw();
}

abstract class Spiel {
    final PApplet applet;

    Spiel(PApplet applet) {
        this.applet = applet;
    }

    abstract void draw();
}

final class SpielScreen extends Screen {
    private final Spiel spiel;

    SpielScreen(PApplet applet, Spiel spiel) {
        super(applet);
        this.spiel = spiel;
    }

    @Override
    Screen draw() {
        spiel.draw();
        return this;
    }
}