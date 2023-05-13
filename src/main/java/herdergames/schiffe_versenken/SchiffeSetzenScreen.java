package herdergames.schiffe_versenken;

import herdergames.spiel.Spieler;
import herdergames.util.Rechteck;
import processing.core.PConstants;

import java.util.Optional;
import java.util.function.Supplier;

final class SchiffeSetzenScreen extends Screen {
    private final Spieler spieler;
    private final Supplier<Screen> naechsterScreenSupplier;
    private final WeiterKnopf weiterKnopf = new WeiterKnopf();
    private UnfertigesSpielBrett brett = UnfertigesSpielBrett.LEER;

    SchiffeSetzenScreen(SchiffeVersenken spiel, Spieler spieler, Supplier<Screen> naechsterScreen) {
        super(spiel);
        this.spieler = spieler;
        this.naechsterScreenSupplier = naechsterScreen;
    }

    @Override
    void draw() {
        brett.draw(applet, getAbstandX(), getAbstandY(), getFeldSize());
        weiterKnopf.draw();
    }

    @Override
    void mousePressed() {
        weiterKnopf.mousePressed();
        brett = brett.handleMousePressed(applet.mouseX - getAbstandX(), applet.mouseY - getAbstandY(), getFeldSize());
    }

    private float getFeldSize() {
        return Math.min(
                applet.height / (float) SpielBrett.SIZE,
                ((1-WeiterKnopf.ABSTAND_ZU_BRETT-WeiterKnopf.BREITE) * applet.width) / (float) SpielBrett.SIZE
        );
    }

    private float getBrettSize() {
        return getFeldSize() * SpielBrett.SIZE;
    }

    private float getBreiteGesamt() {
        return getBrettSize() + (WeiterKnopf.ABSTAND_ZU_BRETT + WeiterKnopf.BREITE) * applet.width;
    }

    private float getAbstandX() {
        return (applet.width - getBreiteGesamt()) / 2;
    }

    private float getAbstandY() {
        return (applet.height - getBrettSize()) / 2;
    }

    private final class WeiterKnopf {
        private static final float ABSTAND_ZU_BRETT = 0.1f;
        private static final float BREITE = 0.2f;
        private static final float HOEHE = 0.05f;
        private static final float TEXT_SIZE = HOEHE * (4f/5f);

        private float getX() {
            return getAbstandX() + getBrettSize() + ABSTAND_ZU_BRETT * applet.width;
        }

        private float getY() {
            return getAbstandY() + getBrettSize() / 2;
        }

        private void draw() {
            applet.rectMode(PConstants.CORNER);
            applet.fill(getFarbe());
            applet.stroke(0);
            applet.strokeWeight(2);
            applet.rect(getX(), getY(), BREITE * applet.width, HOEHE * applet.height);

            applet.textAlign(PConstants.CENTER, PConstants.CENTER);
            applet.textSize(applet.height * TEXT_SIZE);
            applet.fill(255);
            applet.text("Weiter", getX() + (BREITE/2) * applet.width, getY() + (HOEHE/2) * applet.height);
        }

        private void mousePressed() {
            if (!getHitbox().istDrinnen(applet.mouseX, applet.mouseY)) {
                return;
            }

            if (!brett.sindSchiffeLegal()) {
                return;
            }

            if (spieler == spiel.spieler1) {
                spiel.spieler1SpielBrett = Optional.of(brett.umwandeln());
            } else {
                spiel.spieler2SpielBrett = Optional.of(brett.umwandeln());
            }

            spiel.openScreen(naechsterScreenSupplier.get());
        }

        private Rechteck getHitbox() {
            return new Rechteck(getX(), getY(), BREITE * applet.width, HOEHE * applet.height);
        }

        private int getFarbe() {
            if (!brett.sindSchiffeLegal()) {
                return applet.color(150, 153, 158);
            }
            return applet.color(71, 142, 255);
        }
    }
}
