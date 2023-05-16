package herdergames.crossy_road;

import herdergames.util.Rechteck;
import processing.core.PConstants;

import java.util.Collections;
import java.util.List;

final class Bahnstrecke extends Zeile {
    private static final float SCHIENE_DICKE = 0.1f;
    private static final float ZUG_WAHRSCHEINLICHKEIT = 0.001f;
    private static final int WARNUNG_ZEIT = (int) (1.5 * 60);
    private static final float WARNUNG_SIZE = 0.5f;
    private static final int ZUG_ZEIT = 2 * 60;

    private Status status = Status.NICHTS;
    private int warnungVerbleibendeZeit;
    private int zugVerbleibendeZeit;

    Bahnstrecke(CrossyRoad spiel, int y) {
        super(spiel, y);
    }

    @Override
    void draw() {
        // Hintergrund
        applet.rectMode(PConstants.CORNER);
        applet.noStroke();
        applet.fill(74, 62, 73);
        applet.rect(0, y, CrossyRoad.BREITE, 1);

        // Schienen
        applet.stroke(0);
        applet.strokeWeight(SCHIENE_DICKE);
        applet.line(0, y + 0.25f, CrossyRoad.BREITE, y + 0.25f);
        applet.line(0, y + 0.75f, CrossyRoad.BREITE, y + 0.75f);

        switch (status) {
            case NICHTS -> {
                if (applet.random(1) <= ZUG_WAHRSCHEINLICHKEIT) {
                    status = Status.WARNUNG;
                    warnungVerbleibendeZeit = WARNUNG_ZEIT;
                }
            }
            case WARNUNG -> {
                for (int x = 0; x < CrossyRoad.BREITE; x++) {
                    applet.ellipseMode(PConstants.CORNER);
                    applet.noStroke();
                    applet.fill(255, 0, 0);
                    applet.circle(
                            x + (1 - WARNUNG_SIZE) / 2,
                            y + (1 - WARNUNG_SIZE) / 2,
                            WARNUNG_SIZE
                    );
                }

                if (warnungVerbleibendeZeit-- < 0) {
                    status = Status.ZUG_FAEHRT;
                    zugVerbleibendeZeit = ZUG_ZEIT;
                }
            }
            case ZUG_FAEHRT -> {
                // Zug
                applet.rectMode(PConstants.CORNER);
                applet.noStroke();
                applet.fill(0);
                applet.rect(0, y + 0.1f, CrossyRoad.BREITE, 0.8f);

                if (zugVerbleibendeZeit-- < 0) {
                    status = Status.NICHTS;
                }
            }
        }
    }

    @Override
    List<Rechteck> getHitboxen() {
        if (status == Status.ZUG_FAEHRT) {
            return Collections.singletonList(new Rechteck(0, y, CrossyRoad.BREITE, 1));
        }

        return Collections.emptyList();
    }

    private enum Status {
        NICHTS,
        WARNUNG,
        ZUG_FAEHRT
    }
}
