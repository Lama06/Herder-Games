package herdergames.kirschbaeume;

import herdergames.util.Rechteck;
import herdergames.util.Steuerung;
import processing.core.PApplet;
import processing.core.PConstants;

final class Spieler {
    static final float START_X = (float) KirschbaumSpiel.ANZAHL_FELDER / 2;
    static final float START_Y = (float) KirschbaumSpiel.ANZAHL_FELDER / 2;
    private static final float GESCHWINDIGKEIT = 0.02f;
    private static final float SICHTRADIUS_LAMPE_AN = 0.6f;
    private static final float SICHTRADIUS_LAMPE_AUS = 0.25f;
    private static final float SICHTRADIUS_AENDERUNG = (SICHTRADIUS_LAMPE_AN-SICHTRADIUS_LAMPE_AUS) / 10;
    private static final float HITBOX_LAMPE_AN = 2;

    private final KirschbaumSpiel kirschbaumSpiel;
    private final PApplet applet;
    private final Steuerung steuerung;
    float x = START_X;
    float y = START_Y;
    private boolean lampe = false;
    private float sichtradiusProzent = SICHTRADIUS_LAMPE_AUS;

    Spieler(KirschbaumSpiel kirschbaumSpiel, herdergames.spiel.Spieler spieler) {
        this.kirschbaumSpiel = kirschbaumSpiel;
        applet = kirschbaumSpiel.applet;
        steuerung = new Steuerung(applet, spieler.id());
    }

    private void bewegen() {
        if (steuerung.istLinksGedrueckt()) {
            x -= GESCHWINDIGKEIT;
        }
        if (steuerung.istRechtsGedrueckt()) {
            x += GESCHWINDIGKEIT;
        }
        if (steuerung.istObenGedrueckt()) {
            y -= GESCHWINDIGKEIT;
        }
        if (steuerung.istUntenGedrueckt()) {
            y += GESCHWINDIGKEIT;
        }
    }

    private void sichtradiusAendern() {
        if (lampe) {
            sichtradiusProzent = Math.min(SICHTRADIUS_LAMPE_AN, sichtradiusProzent+SICHTRADIUS_AENDERUNG);
        } else {
            sichtradiusProzent = Math.max(SICHTRADIUS_LAMPE_AUS, sichtradiusProzent-SICHTRADIUS_AENDERUNG);
        }
    }

    void draw() {
        bewegen();
        sichtradiusAendern();

        float x = kirschbaumSpiel.getFeldPositionX(this.x);
        float y = kirschbaumSpiel.getFeldPositionY(this.y);
        float feldSize = kirschbaumSpiel.getFeldSize();

        applet.fill(255, 0, 0);
        applet.rectMode(PConstants.CORNER);
        applet.rect(x, y, feldSize, feldSize);
    }

    void dunkelheitOverlayZeichnen() {
        applet.loadPixels();

        float mitteX = kirschbaumSpiel.getFeldPositionX(x) + kirschbaumSpiel.getFeldSize() / 2f;
        float mitteY = kirschbaumSpiel.getFeldPositionY(y) + kirschbaumSpiel.getFeldSize() / 2f;
        float maximaleEntfernungVonMitte = (float) Math.sqrt(mitteX * mitteX + mitteY * mitteY);
        float sichtradiusVonMitte = maximaleEntfernungVonMitte * sichtradiusProzent;

        for (int x = 0; x < applet.width; x++) {
            for (int y = 0; y < applet.height; y++) {
                float entfernungZuMitteX = Math.abs(mitteX - x);
                float entfernungZuMitteY = Math.abs(mitteY - y);
                float entfernungZuMitte = (float) Math.sqrt(
                        entfernungZuMitteX * entfernungZuMitteX + entfernungZuMitteY * entfernungZuMitteY
                );
                float prozentDesSichtradius = entfernungZuMitte / sichtradiusVonMitte;

                int aktuelleFarbe = applet.pixels[y*applet.width + x];
                int neueFarbe;
                if (entfernungZuMitte >= sichtradiusVonMitte) {
                    neueFarbe = applet.color(0);
                } else {
                    neueFarbe = applet.lerpColor(aktuelleFarbe, applet.color(0), prozentDesSichtradius);
                }
                applet.pixels[y * applet.width + x] = neueFarbe;
            }
        }

        applet.updatePixels();
    }

    void keyPressed() {
        steuerung.keyPressed();

        if (applet.key == ' ') {
            lampe = !lampe;
        }
    }

    void keyReleased() {
        steuerung.keyReleased();
    }

    Rechteck getWachmannHitbox() {
        if (lampe) {
            return new Rechteck(
                    x-HITBOX_LAMPE_AN,
                    y-HITBOX_LAMPE_AN,
                    HITBOX_LAMPE_AN*2+1,
                    HITBOX_LAMPE_AN*2+1
            );
        }
        return new Rechteck(x, y, 1, 1);
    }

    Rechteck getKirschbaumHitbox() {
        return new Rechteck(x, y, 1, 1);
    }

    boolean istGefangen() {
        return kirschbaumSpiel.wachmaenner.stream()
                .anyMatch(wachmann -> wachmann.getHitbox().kollidiertMit(getWachmannHitbox()));
    }
}
