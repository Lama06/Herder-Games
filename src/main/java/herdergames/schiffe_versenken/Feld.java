package herdergames.schiffe_versenken;

import processing.core.PApplet;
import processing.core.PConstants;

sealed interface Feld {
    private static void umrandungMalen(PApplet applet, float x, float y, float size) {
        applet.rectMode(PConstants.CORNER);
        applet.noFill();
        applet.stroke(0);
        applet.strokeWeight(2);
        applet.rect(x, y, size, size);
    }

    private static void markierungMalen(PApplet applet, float x, float y, float size) {
        applet.fill(0, 255, 0);
        applet.ellipseMode(PConstants.CENTER);
        applet.circle(x+size/2, y+size/2, size*(2f/3f));
    }

    private static void schiffMalen(PApplet applet, float x, float y, float size) {
        applet.rectMode(PConstants.CORNER);
        applet.fill(0);
        applet.stroke(0);
        applet.strokeWeight(2);
        applet.rect(x, y, size, size);
    }

    void draw(PApplet applet, float x, float y, float size, boolean ausSichtDesGegner);

    record Leer(boolean vomGegnerMarkiert) implements Feld {
        @Override
        public void draw(PApplet applet, float x, float y, float size, boolean ausSichtDesGegner) {
            umrandungMalen(applet, x, y, size);

            if (ausSichtDesGegner && vomGegnerMarkiert) {
                markierungMalen(applet, x, y, size);
            }
        }

        Leer markierungWechseln() {
            return new Leer(!vomGegnerMarkiert);
        }
    }

    record Schiff(boolean vomGegnerMarkiert) implements Feld {
        @Override
        public void draw(PApplet applet, float x, float y, float size, boolean ausSichtDesGegner) {
            umrandungMalen(applet, x, y, size);
            if (ausSichtDesGegner) {
                if (vomGegnerMarkiert) {
                    markierungMalen(applet, x, y, size);
                }
            } else {
                schiffMalen(applet, x, y, size);
            }
        }

        Schiff markierungWechseln() {
            return new Schiff(!vomGegnerMarkiert);
        }
    }

    enum Getroffen implements Feld {
        INSTANCE;

        @Override
        public void draw(PApplet applet, float x, float y, float size, boolean ausSichtDesGegner) {
            umrandungMalen(applet, x, y, size);
            schiffMalen(applet, x, y, size);
            markierungMalen(applet, x, y, size);
        }
    }
}
