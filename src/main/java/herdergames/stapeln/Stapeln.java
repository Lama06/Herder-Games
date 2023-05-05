package herdergames.stapeln;

import herdergames.spiel.Spiel;
import herdergames.util.Steuerung;
import processing.core.PApplet;
import processing.core.PConstants;

import java.util.*;

public final class Stapeln extends Spiel.Mehrspieler {
    private final List<SpielBrett> spielBretter = new ArrayList<>();
    private final List<Spieler.Id> rangliste = new ArrayList<>();

    public Stapeln(PApplet applet, Set<Spieler> alleSpieler) {
        super(applet);
        List<Spieler> spielerSortiert = new ArrayList<>(alleSpieler);
        spielerSortiert.sort(Comparator.comparing(spieler -> spieler.id));
        for (Spieler spieler : spielerSortiert) {
            spielBretter.add(new SpielBrett(spieler));
        }
    }

    @Override
    public Optional<List<Spieler.Id>> draw() {
        applet.background(0);

        Iterator<SpielBrett> spielBretterIterator = spielBretter.iterator();
        int index = 0;
        while (spielBretterIterator.hasNext()) {
            SpielBrett spielBrett = spielBretterIterator.next();
            if (spielBrett.gewonnen()) {
                spielBretterIterator.remove();
                rangliste.add(0, spielBrett.spieler.id);
                continue;
            }
            if (spielBrett.verloren) {
                spielBretterIterator.remove();
                rangliste.add(spielBrett.spieler.id);
                continue;
            }
            spielBrett.draw(index++);
        }

        if (spielBretter.isEmpty()) {
            return Optional.of(rangliste);
        }

        return Optional.empty();
    }

    @Override
    public void keyPressed() {
        for (SpielBrett spielBrett : spielBretter) {
            spielBrett.keyPressed();
        }
    }

    @Override
    public void keyReleased() {
        for (SpielBrett spielBrett : spielBretter) {
            spielBrett.keyReleased();
        }
    }

    private float getSpielBrettBreite() {
        return (float) applet.width / spielBretter.size();
    }

    private final class SpielBrett {
        private final Spieler spieler;
        private final Steuerung steuerung;
        private FallenderStein fallenderStein;
        private final List<GefallenerStein> gefalleneSteine;
        private boolean verloren;

        private SpielBrett(Spieler spieler) {
            this.spieler = spieler;
            steuerung = new Steuerung(applet, spieler.id);

            gefalleneSteine = new ArrayList<>();

            // Muss nach gefalleneSteine initialisiert werden, weil dieses Feld im Konsturkor von FallenderStein
            // benötigt wird.
            fallenderStein = new FallenderStein();
        }

        private void draw(int index) {
            float spielBrettBreite = getSpielBrettBreite();
            applet.rectMode(PConstants.CORNER);
            applet.fill(255);
            applet.strokeWeight(3);
            applet.stroke(0);
            applet.rect(spielBrettBreite*index, 0, spielBrettBreite, applet.height);

            applet.textAlign(PConstants.CENTER, PConstants.CENTER);
            applet.textSize(40);
            applet.fill(fallenderStein.farbe);
            applet.text(spieler.name, spielBrettBreite*index + spielBrettBreite/2, (float) applet.height/10);

            fallenderStein.draw(index);
            for (GefallenerStein gefallenerStein : gefalleneSteine) {
                gefallenerStein.draw(index);
            }
        }

        private boolean gewonnen() {
            for (GefallenerStein gefallenerStein : gefalleneSteine) {
                if (gefallenerStein.istOben()) {
                    return true;
                }
            }

            return false;
        }

        private void keyPressed() {
            steuerung.keyPressed();
        }

        private void keyReleased() {
            steuerung.keyReleased();
        }

        private final class FallenderStein {
            private static final float START_BREITE = 0.9f;
            private static final float HOEHE = 0.025f;
            private static final float GESCHWINDIGKEIT_Y_NORMAL = 0.007f;
            private static final float GESCHWINDIGKEIT_Y_BESCHLEUNIGT = GESCHWINDIGKEIT_Y_NORMAL * 2;
            private static final float GESCHWINDIGKEIT_X = 0.01f;

            private final float breite;
            private float x;
            private float y;
            private final int farbe;

            private FallenderStein() {
                List<Integer> farben = List.of(
                        applet.color(118, 1, 136),
                        applet.color(0, 76, 255),
                        applet.color(0, 129, 33),
                        applet.color(255, 238, 0),
                        applet.color(255, 141, 0),
                        applet.color(229, 0, 0)
                );
                farbe = farben.get(gefalleneSteine.size() % farben.size());

                if (gefalleneSteine.isEmpty()) {
                    breite = START_BREITE;
                } else {
                    GefallenerStein obersterGefallenerStein = gefalleneSteine.get(gefalleneSteine.size() - 1);
                    breite = obersterGefallenerStein.breite;
                }

                x = applet.random(1-breite);
            }

            private boolean amBoden() {
                return y+HOEHE >= 1;
            }

            private Optional<GefallenerStein> getLiegtAufStein() {
                for (GefallenerStein gefallenerStein : gefalleneSteine) {
                    if (y + HOEHE >= gefallenerStein.y) {
                        return Optional.of(gefallenerStein);
                    }
                }

                return Optional.empty();
            }

            private void draw(int index) {
                y += steuerung.istUntenGedrueckt() ? GESCHWINDIGKEIT_Y_BESCHLEUNIGT : GESCHWINDIGKEIT_Y_NORMAL;

                if (steuerung.istLinksGedrueckt() && x > 0) {
                    x -= GESCHWINDIGKEIT_X;
                }

                if (steuerung.istRechtsGedrueckt() && x+breite < 1) {
                    x += GESCHWINDIGKEIT_X;
                }

                if (amBoden()) {
                    gefalleneSteine.add(new GefallenerStein(breite, x, 1-GefallenerStein.HOEHE, farbe));
                    fallenderStein = new FallenderStein();
                } else if (getLiegtAufStein().isPresent()) {
                    GefallenerStein liegtAufStein = getLiegtAufStein().get();
                    if (x+breite <= liegtAufStein.x+liegtAufStein.breite && x+breite >= liegtAufStein.x) { // Überhang links
                        gefalleneSteine.add(new GefallenerStein(
                                breite - Math.abs(x-liegtAufStein.x),
                                liegtAufStein.x,
                                liegtAufStein.y-GefallenerStein.HOEHE,
                                farbe
                        ));
                        fallenderStein = new FallenderStein();
                    } else if (x <= liegtAufStein.x+liegtAufStein.breite && x >= liegtAufStein.x) { // Überhang rechts
                        gefalleneSteine.add(new GefallenerStein(
                                (liegtAufStein.x+liegtAufStein.breite) - x,
                                x,
                                liegtAufStein.y-GefallenerStein.HOEHE,
                                farbe
                        ));
                        fallenderStein = new FallenderStein();
                    } else { // Daneben
                        fallenderStein = null;
                        verloren = true;
                    }
                }

                float brettBreite = getSpielBrettBreite();
                float brettX = brettBreite * index;

                applet.rectMode(PConstants.CORNER);
                applet.fill(farbe);
                applet.stroke(0);
                applet.strokeWeight(1);
                applet.rect(brettX + x * brettBreite, y * applet.height, breite * brettBreite, HOEHE * applet.height);
            }
        }

        private final class GefallenerStein {
            private static final float HOEHE = FallenderStein.HOEHE;

            private final float breite;
            private final float x;
            private final float y;
            private final int farbe;

            private GefallenerStein(float breite, float x, float y, int farbe) {
                this.breite = breite;
                this.x = x;
                this.y = y;
                this.farbe = farbe;
            }

            private void draw(int index) {
                float brettBreite = getSpielBrettBreite();
                float brettX = brettBreite * index;

                applet.rectMode(PConstants.CORNER);
                applet.fill(farbe);
                applet.stroke(0);
                applet.strokeWeight(1);
                applet.rect(brettX + x * brettBreite, y * applet.height, breite * brettBreite, HOEHE * applet.height);
            }

            private boolean istOben() {
                return y <= 0;
            }
        }
    }
}
