package herdergames.util;

import processing.core.PApplet;
import processing.core.PConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class PartikelManager {
    private final PApplet applet;
    private final List<Partikel> partikel = new ArrayList<>();

    public PartikelManager(PApplet applet) {
        this.applet = applet;
    }

    public void draw() {
        Iterator<Partikel> partikelIterator = partikel.iterator();
        while (partikelIterator.hasNext()) {
            Partikel partikel = partikelIterator.next();
            if (partikel.istUnsichtbar()) {
                partikelIterator.remove();
                continue;
            }
            partikel.draw();
        }
    }

    public void spawnPartikel(float x, float y, int anzahl) {
        for (int i = 0; i < anzahl; i++) {
            partikel.add(new Partikel(x, y));
        }
    }

    public void spawnPartikel(float x, float y) {
        spawnPartikel(x, y, 150);
    }

    private final class Partikel {
        private static final float SIZE = 0.002f;
        private static final float MAX_GESCHWINDIGKEIT = 0.002f;
        private static final float TRANSPARENZ_AENDERUNG = -3f;

        private final float xGeschwindigkeit = applet.random(-MAX_GESCHWINDIGKEIT, MAX_GESCHWINDIGKEIT);
        private final float yGeschwindigkeit = applet.random(-MAX_GESCHWINDIGKEIT, MAX_GESCHWINDIGKEIT);
        private final int farbe = applet.color((int) applet.random(255), (int) applet.random(255), (int) applet.random(255));
        private float x;
        private float y;
        private float transparenz = 255;

        private Partikel(float x, float y) {
            this.x = x;
            this.y = y;
        }

        private boolean istUnsichtbar() {
            return transparenz <= 0;
        }

        private void draw() {
            x += xGeschwindigkeit;
            y += yGeschwindigkeit;
            transparenz += TRANSPARENZ_AENDERUNG;

            applet.rectMode(PConstants.CENTER);
            applet.noStroke();
            applet.fill(farbe, transparenz);
            float size = Math.max(applet.width * SIZE, applet.height * SIZE);
            applet.rect(x * applet.width, y * applet.height, size, size);
        }
    }
}
