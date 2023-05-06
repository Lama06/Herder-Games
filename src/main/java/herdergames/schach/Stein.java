package herdergames.schach;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

record Stein(Spieler spieler, Figur figur) {
    private PImage getImage() {
        return switch (spieler) {
            case WEISS -> switch (figur) {
                case BAUER -> Schach.WEISS_BAUER;
                case LAEUFER -> Schach.WEISS_LAEUFER;
                case SPRINGER -> Schach.WEISS_SPRINGER;
                case TURM -> Schach.WEISS_TURM;
                case DAME -> Schach.WEISS_DAME;
                case KOENIG -> Schach.WEISS_KOENIG;
            };
            case SCHWARZ -> switch (figur) {
                case BAUER -> Schach.SCHWARZ_BAUER;
                case LAEUFER -> Schach.SCHWARZ_LAEUFER;
                case SPRINGER -> Schach.SCHWARZ_SPRINGER;
                case TURM -> Schach.SCHWARZ_TURM;
                case DAME -> Schach.SCHWARZ_DAME;
                case KOENIG -> Schach.SCHWARZ_KOENIG;
            };
        };
    }

    void draw(PApplet applet, int x, int y, int width, int height) {
        applet.imageMode(PConstants.CORNER);
        applet.image(getImage(), x, y, width, height);
    }
}
