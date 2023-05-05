package herdergames.tic_tac_toe;

import processing.core.PApplet;

enum Spieler implements herdergames.ai.Spieler<Spieler> {
    KREUZ {
        @Override
        void drawSymbol(PApplet applet, int x, int y, int size) {
            applet.stroke(255);
            applet.strokeWeight(5);
            applet.line(x, y, x + size, y + size);
            applet.line(x + size, y, x, y + size);
        }
    },
    KREIS {
        @Override
        void drawSymbol(PApplet applet, int x, int y, int size) {
            applet.stroke(255);
            applet.noFill();
            applet.strokeWeight(5);
            applet.ellipseMode(PApplet.CORNER);
            applet.ellipse(x, y, size, size);
        }
    };

    abstract void drawSymbol(PApplet applet, int x, int y, int size);

    @Override
    public Spieler getGegner() {
        return switch (this) {
            case KREIS -> KREUZ;
            case KREUZ -> KREIS;
        };
    }
}
