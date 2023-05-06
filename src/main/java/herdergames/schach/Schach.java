package herdergames.schach;

import processing.core.PApplet;
import processing.core.PImage;

public final class Schach {
    static PImage WEISS_BAUER;
    static PImage WEISS_LAEUFER;
    static PImage WEISS_SPRINGER;
    static PImage WEISS_TURM;
    static PImage WEISS_DAME;
    static PImage WEISS_KOENIG;

    static PImage SCHWARZ_BAUER;
    static PImage SCHWARZ_LAEUFER;
    static PImage SCHWARZ_SPRINGER;
    static PImage SCHWARZ_TURM;
    static PImage SCHWARZ_DAME;
    static PImage SCHWARZ_KOENIG;

    public static void init(PApplet applet) {
        WEISS_BAUER = applet.loadImage("schach/weiss/bauer.png");
        WEISS_LAEUFER = applet.loadImage("schach/weiss/laeufer.png");
        WEISS_SPRINGER = applet.loadImage("schach/weiss/springer.png");
        WEISS_TURM = applet.loadImage("schach/weiss/turm.png");
        WEISS_DAME = applet.loadImage("schach/weiss/dame.png");
        WEISS_KOENIG = applet.loadImage("schach/weiss/koenig.png");

        SCHWARZ_BAUER = applet.loadImage("schach/schwarz/bauer.png");
        SCHWARZ_LAEUFER = applet.loadImage("schach/schwarz/laeufer.png");
        SCHWARZ_SPRINGER = applet.loadImage("schach/schwarz/springer.png");
        SCHWARZ_TURM = applet.loadImage("schach/schwarz/turm.png");
        SCHWARZ_DAME = applet.loadImage("schach/schwarz/dame.png");
        SCHWARZ_KOENIG = applet.loadImage("schach/schwarz/koenig.png");
    }

    private Schach() {}
}
