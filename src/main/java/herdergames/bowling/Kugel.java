package herdergames.bowling;

import herdergames.util.Rechteck;
import processing.core.PApplet;
import processing.core.PConstants;

final class Kugel {
    private static final float SIZE = 0.03f;
    private static final float X_START = 0.5f - SIZE / 2f;
    private static final float Y_START = 1f - SIZE * 4f;
    private static final float ZIELEN_GESCHWINDIGKEIT = 0.001f;
    private static final float ZIELEN_X_START = X_START + SIZE / 2f;
    private static final float ZIELEN_Y_START = Y_START + SIZE / 2f;
    private static final float ZIELEN_GESCHWINDIGKEIT_MULTIPLIKATOR = 0.1f;
    private static final float MIN_GESCHWINDIGKEIT_Y = -0.003f;

    private final Bahn bahn;
    private final Bowling spiel;
    private final PApplet appplet;
    private float x = X_START;
    private float y = Y_START;
    private float xSpeed = 0f;
    private float ySpeed = 0f;
    private float zielenX = ZIELEN_X_START;
    private float zielenY = ZIELEN_Y_START;

    Kugel(Bahn bahn) {
        this.bahn = bahn;
        spiel = bahn.spiel;
        appplet = bahn.applet;
    }

    private void kugelZeichnen(float x, float breite) {
        float size = Math.min(breite, appplet.height) * SIZE;
        appplet.ellipseMode(PConstants.CORNER);
        appplet.fill(0);
        appplet.noStroke();
        appplet.circle(x + this.x * breite, y * appplet.height, size);
    }

    private void zielen() {
        if (spiel.status != Status.ZIELEN) {
            return;
        }

        if (bahn.steuerung.istUntenGedrueckt() && zielenY < 1f) {
            zielenY += ZIELEN_GESCHWINDIGKEIT;
        }
        if (bahn.steuerung.istObenGedrueckt() && zielenY > ZIELEN_Y_START) {
            zielenY -= ZIELEN_GESCHWINDIGKEIT;
        }
        if (bahn.steuerung.istRechtsGedrueckt() && zielenX < 1) {
            zielenX += ZIELEN_GESCHWINDIGKEIT;
        }
        if (bahn.steuerung.istLinksGedrueckt() && zielenX > 0) {
            zielenX -= ZIELEN_GESCHWINDIGKEIT;
        }
    }

    private void zielenHilfeZeichnen(float x, float breite) {
        if (spiel.status != Status.ZIELEN) {
            return;
        }

        appplet.strokeWeight(10);
        appplet.stroke(255, 0, 0);
        appplet.point(x + zielenX * breite, zielenY * appplet.height);

        appplet.strokeWeight(4);
        appplet.stroke(0, 255, 0);
        appplet.line(
                x + ZIELEN_X_START * breite,
                ZIELEN_Y_START * appplet.height,
                x + zielenX * breite,
                zielenY * appplet.height
        );
    }

    private void bewegen() {
        if (spiel.status != Status.SCHIESSEN) {
            return;
        }

        x += xSpeed;
        y += ySpeed;

        if (x <= 0f) {
            xSpeed = Math.abs(xSpeed);
            x = 0;
        }

        if (x >= 1f) {
            xSpeed = -Math.abs(xSpeed);
            x = 1f;
        }
    }

    void draw(float x, float breite) {
        zielen();
        bewegen();
        kugelZeichnen(x, breite);
        zielenHilfeZeichnen(x, breite);
    }

    void schiessen() {
        xSpeed = ZIELEN_GESCHWINDIGKEIT_MULTIPLIKATOR * (ZIELEN_X_START - zielenX);
        ySpeed = Math.min(ZIELEN_GESCHWINDIGKEIT_MULTIPLIKATOR * (ZIELEN_Y_START - zielenY), MIN_GESCHWINDIGKEIT_Y);
    }

    boolean istObenRaus() {
        return y + SIZE <= 0;
    }

    Rechteck getHitbox() {
        return new Rechteck(x, y, SIZE, SIZE);
    }
}
