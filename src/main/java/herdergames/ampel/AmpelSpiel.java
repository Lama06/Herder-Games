package herdergames.ampel;

import herdergames.spiel.MehrspielerSpiel;
import herdergames.spiel.Spieler;
import herdergames.util.Steuerung;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.util.*;

import static java.util.Comparator.*;

public final class AmpelSpiel extends MehrspielerSpiel {
    private static PImage menschBild;

    public static void init(PApplet applet) {
        menschBild = applet.loadImage("ampel/mensch.png");
    }

    private final List<Mensch> menschen = new ArrayList<>();
    private final List<Spieler.Id> spielerVerloren = new ArrayList<>();
    private AmpelPhase aktuelleAmpelPhase = AmpelPhase.Rot.INSTANCE;
    private int ampelPhaseVerbleibendeZeit = AmpelPhase.Rot.INSTANCE.getLaengeFrames();

    public AmpelSpiel(PApplet applet, Set<Spieler> alleSpieler) {
        super(applet);
        alleSpieler.stream()
                .sorted(comparing(Spieler::id))
                .map(Mensch::new)
                .forEach(this.menschen::add);
    }

    @Override
    public Optional<List<Spieler.Id>> draw() {
        applet.background(255);

        drawAmpel();
        menschen.forEach(Mensch::draw);

        if (ampelPhaseVerbleibendeZeit-- < 0) {
            aktuelleAmpelPhase = aktuelleAmpelPhase.getNaechstePhase();
            ampelPhaseVerbleibendeZeit = aktuelleAmpelPhase.getLaengeFrames();
        }

        menschen.stream().filter(Mensch::hatVerloren).map(mensch -> mensch.spieler.id()).forEach(spielerVerloren::add);
        menschen.removeIf(Mensch::hatVerloren);

        List<Spieler.Id> gewinner = menschen.stream().filter(Mensch::hatGewonnen).map(mensch -> mensch.spieler.id()).toList();
        if (!gewinner.isEmpty()) {
            List<Spieler.Id> rangliste = new ArrayList<>(gewinner);
            menschen.stream()
                    .filter(mensch -> !gewinner.contains(mensch.spieler.id()))
                    .sorted(comparingDouble(mensch -> mensch.y))
                    .map(mensch -> mensch.spieler.id())
                    .forEach(rangliste::add);
            rangliste.addAll(spielerVerloren);
            return Optional.of(rangliste);
        }

        return Optional.empty();
    }

    @Override
    public void keyPressed() {
        menschen.forEach(Mensch::keyPressed);
    }

    @Override
    public void keyReleased() {
        menschen.forEach(Mensch::keyReleased);
    }

    private void drawAmpel() {
        applet.ellipseMode(PConstants.CENTER);
        applet.stroke(0);
        applet.strokeWeight(2);
        applet.fill(aktuelleAmpelPhase.getFarbe(applet));
        applet.circle(applet.width / 2f, applet.height / 5f, applet.height / 7f);
    }

    private final class Mensch {
        private static final float GESCHWINDIGKEIT = 0.001f;
        private static final float BREITE = 0.05f;
        private static final float HOEHE = BREITE * 3;

        private final Spieler spieler;
        private final Steuerung steuerung;
        private float x = applet.random(0, 1f - BREITE);
        private float y = 1f - HOEHE;

        private Mensch(Spieler spieler) {
            this.spieler = spieler;
            steuerung = new Steuerung(applet, spieler.id());
        }

        private boolean hatVerloren() {
            if (!(aktuelleAmpelPhase instanceof AmpelPhase.Rot)) {
                return false;
            }

            return steuerung.istObenGedrueckt() || steuerung.istUntenGedrueckt();
        }

        private boolean hatGewonnen() {
            return y + HOEHE <= 0;
        }

        private void bewegen() {
            x += GESCHWINDIGKEIT * steuerung.getXRichtung();
            y += GESCHWINDIGKEIT * steuerung.getYRichtung();
        }

        private void draw() {
            bewegen();

            applet.imageMode(PConstants.CORNER);
            applet.image(menschBild, x * applet.width, y * applet.height, BREITE * applet.width, HOEHE * applet.height);
        }

        private void keyPressed() {
            steuerung.keyPressed();
        }

        private void keyReleased() {
            steuerung.keyReleased();
        }
    }
}
