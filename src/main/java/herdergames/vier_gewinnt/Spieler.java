package herdergames.vier_gewinnt;

import processing.core.PApplet;

enum Spieler implements herdergames.ai.Spieler<Spieler> {
    SPIELER_1,
    SPIELER_2;

    @Override
    public Spieler getGegner() {
        return switch (this) {
            case SPIELER_1 -> SPIELER_2;
            case SPIELER_2 -> SPIELER_1;
        };
    }

    int getColor(PApplet applet) {
        return switch (this) {
            case SPIELER_1 -> applet.color(66, 135, 245);
            case SPIELER_2 -> applet.color(250, 110, 22);
        };
    }
}
