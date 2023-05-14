package herdergames.kirschbaeume;

import processing.core.PApplet;
import processing.core.PConstants;

import java.util.Optional;

final class KirschbaumFaellenSpiel {
    private static final float KIRSCHBAUM_BREITE = 0.5f;
    private static final float KIRSCHBAUM_X = 0.5f - KIRSCHBAUM_BREITE/2f;
    private static final float MOTORSAEGE_BREITE = 0.2f;
    private static final float MOTORSAEGE_HOEHE = MOTORSAEGE_BREITE / 2f;

    private final PApplet applet;

    private boolean spielGestartet = false;

    private int startBildschirmVerbleibendeZeit = 3 * 60;
    private int startBildschirmSaturation = 30;
    private int startBildschirmSaturationSpeed = 4;

    private int baumFaellenVerbleibendeZeit = 13*60;
    private int baumFaellenSaegeHeat = 0;
    private int baeumFaellenBaumLeben = 100;

    KirschbaumFaellenSpiel(PApplet applet) {
        this.applet = applet;
    }

    Optional<Ergebnis> draw() {
        if (!spielGestartet) {
            startBildschirmZeichnen();
            return Optional.empty();
        }

        if (baeumFaellenBaumLeben <= 0) {
            return Optional.of(Ergebnis.GEWONNEN);
        }
        if (baumFaellenVerbleibendeZeit <= 0 || baumFaellenSaegeHeat >= 100) {
            return Optional.of(Ergebnis.VERLOREN);
        }
        baumFaellenBildschirmZeichnen();
        return Optional.empty();
    }

    private void startBildschirmZeichnen() {
        if (startBildschirmVerbleibendeZeit <= 0) {
            spielGestartet = true;
            return;
        }
        startBildschirmVerbleibendeZeit--;

        if (startBildschirmSaturation > 179) {
            startBildschirmSaturationSpeed *= -1;
        }
        if (startBildschirmSaturation < 25) {
            startBildschirmSaturationSpeed *= -1;
        }
        startBildschirmSaturation += startBildschirmSaturationSpeed;

        applet.background(100);

        applet.colorMode(PConstants.RGB);
        applet.fill(255);
        applet.textSize(applet.height*0.1f);
        applet.textAlign(PConstants.CENTER);
        applet.text("Säge den Baum durch!", 0.5f * applet.width, 0.15f * applet.height);

        applet.colorMode(PConstants.HSB);
        applet.fill(255, 0, startBildschirmSaturation);
        applet.textSize(applet.height*0.08f);
        applet.textAlign(PConstants.CENTER);
        applet.text("Get Ready: " + startBildschirmVerbleibendeZeit / 60, 0.5f * applet.width, 0.25f * applet.height);

        applet.colorMode(PConstants.RGB);
    }

    private void baumFaellenBildschirmZeichnen() {
        baumFaellenVerbleibendeZeit--;

        if (baumFaellenSaegeHeat > 0 && applet.frameCount % 10 == 0) {
            baumFaellenSaegeHeat--;
        }

        if (applet.mousePressed) {
            baumFaellenSaegeHeat += 3;
        }

        applet.background(255);

        applet.fill(0);
        applet.textAlign(PConstants.CORNER);
        applet.textSize(applet.height * 0.06f);
        applet.text("Wärme: " + baumFaellenSaegeHeat + "%", 0.05f * applet.width, 0.25f * applet.height );
        applet.text("Verbleibende Zeit: " + (baumFaellenVerbleibendeZeit / 60) + "s", 0.05f * applet.width, 0.35f * applet.height);
        applet.text("Baum Leben: " + baeumFaellenBaumLeben + "%", 0.05f * applet.width, 0.45f * applet.height );

        Kirschbaum.zeichnen(applet, baumXBerechnen(), baumYBerechnen(), baumSizeBerechnen());

        applet.loadPixels();
        int stamFarbe = Kirschbaum.getKirschbaumStammFarbe(applet);
        int farbeUnterMotorsage = applet.pixels[saegeYBerechnen() * applet.width + saegeXBerechnen()];
        if (farbeUnterMotorsage == stamFarbe && applet.mousePressed) {
            baeumFaellenBaumLeben -= 2;
        }

        applet.imageMode(PConstants.CORNER);
        applet.image(KirschbaumSpiel.motorsaegeBild, applet.mouseX, applet.mouseY, MOTORSAEGE_BREITE * applet.width, MOTORSAEGE_HOEHE * applet.height);
    }

    private float baumSizeBerechnen() {
        return applet.width * KIRSCHBAUM_BREITE;
    }

    private float baumXBerechnen() {
        return KIRSCHBAUM_X * applet.width;
    }

    private float baumYBerechnen() {
        return applet.height - baumSizeBerechnen();
    }

    private int saegeXBerechnen() {
        return Math.min(applet.width-1, (int) (applet.mouseX + MOTORSAEGE_BREITE * applet.width));
    }

    private int saegeYBerechnen() {
        return Math.min(applet.height-1, (int) (applet.mouseY + MOTORSAEGE_HOEHE * applet.height));
    }

    enum Ergebnis {
        GEWONNEN,
        VERLOREN
    }
}
