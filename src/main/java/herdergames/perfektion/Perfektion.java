package herdergames.perfektion;

import herdergames.spiel.MehrspielerSpiel;
import herdergames.spiel.Spieler;
import herdergames.util.Rechteck;
import herdergames.util.Steuerung;
import processing.core.PApplet;
import processing.core.PConstants;

import java.util.*;

public final class Perfektion extends MehrspielerSpiel {
    private final List<SpielerAuswahl> spielerAuswahlen = new ArrayList<>();
    private List<Form> formen;
    private Form unperfekteForm;
    private Imperfektion imperfektion;

    public Perfektion(PApplet applet, Set<Spieler> alleSpieler) {
        super(applet);
        formenErstellen();
        for (Spieler spieler : alleSpieler) {
            spielerAuswahlen.add(new SpielerAuswahl(spieler));
        }
    }

    private void formenErstellen() {
        int zeilen = applet.choice(5, 10);
        int spalten = applet.choice(5, 10);

        float hoehe = 1f / zeilen;
        float breite = 1f / spalten;

        FormType formType = FormType.zufaellig(applet);

        formen = new ArrayList<>();
        for (int x = 0; x < spalten; x++) {
            for (int y = 0; y < zeilen; y++) {
                float xPosition = x * breite;
                float yPosition = y * hoehe;

                Form form;
                if (formen.isEmpty()) {
                    form = formType.factory.mitZufallswerten(applet, xPosition, yPosition, breite, hoehe);
                } else {
                    form = formType.factory.kopieVon(formen.get(0) , applet, xPosition, yPosition, breite, hoehe);
                }
                formen.add(form);
            }
        }

        unperfekteForm = formen.get(applet.choice(formen.size()));

        List<ImperfektionType> moeglicheImperfektionen = Arrays.stream(ImperfektionType.values())
                .filter(imperfektionType -> imperfektionType.factory.istMitFormKompatibel(unperfekteForm))
                .toList();

        imperfektion = moeglicheImperfektionen.get(applet.choice(moeglicheImperfektionen.size())).factory.mitZufallswerten(applet, unperfekteForm);
    }

    @Override
    public Optional<List<Spieler.Id>> draw() {
        applet.background(255);

        imperfektion.draw();

        for (Form form : formen) {
            form.draw();
        }

        for (SpielerAuswahl spielerAuswahl : spielerAuswahlen) {
            if (spielerAuswahl.punkte >= SpielerAuswahl.ZIEL_PUNKTZAHL) {
                List<Spieler.Id> rangliste = spielerAuswahlen.stream()
                        .sorted(Comparator.<SpielerAuswahl>comparingInt(s -> s.punkte).reversed())
                        .map(s -> s.spieler.id())
                        .toList();
                return Optional.of(rangliste);
            }
            spielerAuswahl.draw();
        }

        return Optional.empty();
    }

    @Override
    public void keyPressed() {
        for (SpielerAuswahl spielerAuswahl : spielerAuswahlen) {
            spielerAuswahl.keyPressed();
        }
    }

    @Override
    public void keyReleased() {
        for (SpielerAuswahl spielerAuswahl : spielerAuswahlen) {
            spielerAuswahl.keyReleased();
        }
    }

    private final class SpielerAuswahl {
        private static final float BEWEGUNGS_GESCHWINDIGKEIT = 0.004f;
        private static final float SIZE = 0.01f;
        private static final float TEXT_SIZE = SIZE*2;
        private static final int ZIEL_PUNKTZAHL = 5;

        private final Spieler spieler;
        private final Steuerung steuerung;
        private float x = 0.5f;
        private float y = 0.5f;
        private int punkte = 0;

        private SpielerAuswahl(Spieler spieler) {
            this.spieler = spieler;
            steuerung = new Steuerung(applet, spieler.id());
        }

        private Optional<Form> getKollidierendeForm() {
            return formen.stream()
                    .filter(form -> form.getHitbox().kollidiertMit(getHitbox()))
                    .findAny();
        }

        private Optional<Form> getAusgewaehlteForm() {
            return getKollidierendeForm().filter(form -> steuerung.istLinksGedrueckt() && steuerung.istRechtsGedrueckt());
        }

        private void bewegen() {
            if (steuerung.istLinksGedrueckt() && x > 0) {
                x -= BEWEGUNGS_GESCHWINDIGKEIT;
            }
            if (steuerung.istRechtsGedrueckt() && x+SIZE < 1) {
                x += BEWEGUNGS_GESCHWINDIGKEIT;
            }
            if (steuerung.istObenGedrueckt() && y > 0) {
                y -= BEWEGUNGS_GESCHWINDIGKEIT;
            }
            if (steuerung.istUntenGedrueckt() && y+SIZE < 1) {
                y += BEWEGUNGS_GESCHWINDIGKEIT;
            }
        }

        private void drawText() {
            applet.textAlign(PConstants.CENTER, PConstants.BOTTOM);
            applet.textSize(TEXT_SIZE * applet.height);
            applet.fill(0);
            applet.text(spieler.name() + ": " + punkte, x * applet.width, (y-TEXT_SIZE) * applet.height);
        }

        private void drawPunkt() {
            applet.fill(0);
            applet.noStroke();
            applet.ellipseMode(PConstants.CORNER);
            applet.circle(x * applet.width, y * applet.height, Math.min(applet.width, applet.height) * SIZE);
        }

        private void draw() {
            bewegen();
            drawPunkt();
            drawText();
        }

        private void keyPressed() {
            steuerung.keyPressed();

            Optional<Form> ausgewaehlteForm = getAusgewaehlteForm();
            if (ausgewaehlteForm.isPresent()) {
                boolean richtig = ausgewaehlteForm.get() == unperfekteForm;
                punkte += richtig ? 1 : -1;
                if (richtig) {
                    formenErstellen();
                }
            }
        }

        private void keyReleased() {
            steuerung.keyReleased();
        }

        private Rechteck getHitbox() {
            return new Rechteck(x, y, SIZE, SIZE);
        }
    }
}
