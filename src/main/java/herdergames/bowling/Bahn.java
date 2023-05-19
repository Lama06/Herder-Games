package herdergames.bowling;

import herdergames.spiel.Spieler;
import herdergames.util.Steuerung;
import processing.core.PApplet;
import processing.core.PConstants;

import java.util.ArrayList;
import java.util.List;

final class Bahn {
    private static final int KEGEL_ZEILEN = 5;
    private static final float ABSTAND_ERSTE_KEGEL_ZEILE = Kegel.HOEHE * 2;
    private static final float ABSTAND_ZWISCHEN_KEGELN = Kegel.BREITE / 2f;
    private static final float ABSTAND_ZWISCHEN_KEGEL_ZEILEN = Kegel.HOEHE / 2f;

    final Bowling spiel;
    final Spieler spieler;
    final Steuerung steuerung;
    final PApplet applet;
    Kugel kugel;
    List<Kegel> kegel = new ArrayList<>();

    Bahn(Bowling spiel, Spieler spieler) {
        this.spiel = spiel;
        this.spieler = spieler;
        steuerung = new Steuerung(spiel.applet, spieler.id());
        applet = spiel.applet;
        kugel = new Kugel(this);

        for (int kegelZeile = 0; kegelZeile < KEGEL_ZEILEN; kegelZeile++) {
            float zeileY = ABSTAND_ERSTE_KEGEL_ZEILE + kegelZeile * Kegel.HOEHE + kegelZeile * ABSTAND_ZWISCHEN_KEGEL_ZEILEN;

            int anzahlKegel = kegelZeile + 1;
            float zeileBreite = anzahlKegel * Kegel.BREITE + (anzahlKegel - 1) * ABSTAND_ZWISCHEN_KEGELN;
            float zeileStartX = (1f - zeileBreite) / 2f;

            for (int kegelIndex = 0; kegelIndex < anzahlKegel; kegelIndex++) {
                float kegelX = zeileStartX + kegelIndex * Kegel.BREITE + kegelIndex * ABSTAND_ZWISCHEN_KEGELN;
                kegel.add(new Kegel(this, kegelX, zeileY));
            }
        }
    }

    private void drawUmrandung(float x, float breite) {
        applet.stroke(0);
        applet.strokeWeight(7);
        applet.rectMode(PConstants.CORNER);
        applet.noFill();
        applet.rect(x, 0, breite, applet.height);
    }

    private void drawSpielerName(float x, float breite) {
        applet.textSize(applet.height * 0.05f);
        applet.textAlign(PConstants.CENTER, PConstants.TOP);
        applet.fill(0);
        applet.text(
                spieler.name().equalsIgnoreCase("Hammi") ? "Bester Informatik Lehrer" : spieler.name(),
                x + 0.5f * breite,
                0
        );
    }

    private void drawKegel(float x, float breite) {
        kegel.removeIf(Kegel::istGetroffen);
        for (Kegel kegel : kegel) {
            kegel.draw(x, breite);
        }
    }

    void draw(float x, float breite) {
        drawUmrandung(x, breite);
        drawKegel(x, breite);
        kugel.draw(x, breite);
        drawSpielerName(x, breite);
    }

    void keyPressed() {
        steuerung.keyPressed();
    }

    void keyReleased() {
        steuerung.keyReleased();
    }

    void schiessen() {
        kugel.schiessen();
    }

    void resetKugel() {
        kugel = new Kugel(this);
    }
}
