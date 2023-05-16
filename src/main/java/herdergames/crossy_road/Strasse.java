package herdergames.crossy_road;

import herdergames.util.Rechteck;
import processing.core.PConstants;

import java.util.ArrayList;
import java.util.List;

final class Strasse extends Zeile {
    private static final int MIN_AUTO_SPAWN_DELAY = 5 * 60;
    private static final int MAX_AUTO_SPAWN_DELAY = MIN_AUTO_SPAWN_DELAY * 4;
    private static final float MIN_SPEED = 0.005f;
    private static final float MAX_SPEED = MIN_SPEED * 4;

    private final float speed;
    private final List<Auto> autos = new ArrayList<>();
    private int naechstesAuto = MIN_AUTO_SPAWN_DELAY;

    Strasse(CrossyRoad spiel, int y) {
        super(spiel, y);
        int richtung = applet.random(1) > 0.5 ? 1 : -1;
        speed = richtung * applet.random(MIN_SPEED, MAX_SPEED);
        for (
                float x = speed > 0 ? 0 : CrossyRoad.BREITE;
                speed > 0 ? x < CrossyRoad.BREITE : x > 0;
                x += speed * applet.random(MIN_AUTO_SPAWN_DELAY, MAX_AUTO_SPAWN_DELAY)
        ) {
            autos.add(new Auto(x));
        }
    }

    @Override
    void draw() {
        if (naechstesAuto-- <= 0) {
            naechstesAuto = applet.choice(MIN_AUTO_SPAWN_DELAY, MAX_AUTO_SPAWN_DELAY);
            autos.add(new Auto());
        }

        applet.rectMode(PConstants.CORNER);
        applet.noStroke();
        applet.fill(74, 62, 73);
        applet.rect(0, y, CrossyRoad.BREITE, 1);

        autos.forEach(Auto::draw);
    }

    @Override
    List<Rechteck> getHitboxen() {
        return autos.stream().map(Auto::getHitbox).toList();
    }

    private final class Auto {
        private static final float HOEHE = 0.8f;
        private static final float BREITE = 2f;

        private final int farbe = applet.color(applet.choice(255), applet.choice(255), applet.choice(255));
        private float x;

        private Auto(float x) {
            this.x = x;
        }

        private Auto() {
            x = speed > 0 ? -BREITE : CrossyRoad.BREITE;
        }

        private float getY() {
            return y + (1 - HOEHE) / 2;
        }

        private void draw() {
            x += speed;

            applet.rectMode(PConstants.CORNER);
            applet.fill(farbe);
            applet.noStroke();
            applet.rect(x, getY(), BREITE, HOEHE);
        }

        private Rechteck getHitbox() {
            return new Rechteck(x, getY(), BREITE, HOEHE);
        }
    }
}
