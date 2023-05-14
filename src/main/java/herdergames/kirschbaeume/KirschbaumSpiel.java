package herdergames.kirschbaeume;

import herdergames.spiel.EinzelspielerSpiel;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class KirschbaumSpiel extends EinzelspielerSpiel {
    static final int ANZAHL_FELDER = 20;
    private static final int ANZAHL_FELDER_AUF_BILDSCHIRM_MIN = 10;
    private static final int ANZAHL_KIRSCHBAEUME = 10;
    private static final int ANZAHL_WACHMAENNER = 10;

    static PImage wachmannBild;
    static PImage motorsaegeBild;

    public static void init(PApplet applet) {
        wachmannBild = applet.loadImage("kirschbaeume/wachmann.png");
        motorsaegeBild = applet.loadImage("kirschbaeume/motorsaege.png");
    }

    final List<Kirschbaum> kirschbaeume = new ArrayList<>();
    final List<Wachmann> wachmaenner = new ArrayList<>();
    final Spieler spieler;
    Optional<KirschbaumFaellenSpiel> kirschbaumFaellenSpiel = Optional.empty();

    public KirschbaumSpiel(PApplet applet, herdergames.spiel.Spieler spieler) {
        super(applet);
        this.spieler = new Spieler(this, spieler);

        for (int i = 0; i < ANZAHL_WACHMAENNER; i++) {
            wachmaenner.add(new Wachmann(this));
        }

        for (int i = 0; i < ANZAHL_KIRSCHBAEUME; i++) {
            kirschbaeume.add(new Kirschbaum(this));
        }
    }

    @Override
    public Optional<Ergebnis> draw() {
        applet.background(0, 204, 0);

        if (kirschbaumFaellenSpiel.isEmpty()) {
            for (Kirschbaum kirschbaum : kirschbaeume) {
                if (kirschbaum.kollisionMitSpieler()) {
                    kirschbaumFaellenSpiel = Optional.of(new KirschbaumFaellenSpiel(applet));
                    break;
                }
            }
        }

        if (kirschbaumFaellenSpiel.isPresent()) {
            Optional<KirschbaumFaellenSpiel.Ergebnis> ergebnis = kirschbaumFaellenSpiel.get().draw();
            if (ergebnis.isPresent()) {
                return switch (ergebnis.get()) {
                    case VERLOREN -> Optional.of(Ergebnis.VERLOREN);
                    case GEWONNEN -> {
                        kirschbaeume.removeIf(Kirschbaum::kollisionMitSpieler);
                        kirschbaumFaellenSpiel = Optional.empty();
                        yield Optional.empty();
                    }
                };
            } else {
                return Optional.empty();
            }
        }

        for (Wachmann wachmann : wachmaenner) {
            wachmann.draw();
        }

        for (Kirschbaum kirschbaum : kirschbaeume) {
            kirschbaum.draw();
        }

        spieler.draw();

        spieler.dunkelheitOverlayZeichnen();

        if (spieler.istGefangen()) {
            return Optional.of(Ergebnis.VERLOREN);
        }

        if (kirschbaeume.isEmpty()) {
            return Optional.of(Ergebnis.GEWONNEN);
        }

        return Optional.empty();
    }

    @Override
    public void keyPressed() {
        spieler.keyPressed();
    }

    @Override
    public void keyReleased() {
        spieler.keyReleased();
    }

    float getFeldPositionX(float x) {
        float xOffsetSpieler = x - spieler.x;
        return xOffsetSpieler * getFeldSize() + applet.width/2f;
    }

    float getFeldPositionY(float y) {
        float yOffsetSpieler = y - spieler.y;
        return yOffsetSpieler * getFeldSize() + applet.height/2f;
    }

    float getFeldSize() {
        return Math.min(applet.width, applet.height) / (float) ANZAHL_FELDER_AUF_BILDSCHIRM_MIN;
    }
}
