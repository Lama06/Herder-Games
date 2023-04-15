import processing.core.PApplet;
import processing.core.PConstants;

import java.util.*;

final class HarryPotterQuiz extends Spiel.Mehrspieler {
    static void init(PApplet applet) {
        fragen = Collections.unmodifiableList(FrageDaten.loadFragen(applet, "harrypotter/fragen.txt"));
    }

    private static final int ZEIT_PRO_FRAGE = 8*60;
    private static final int ANZAHL_FRAGEN = 20;

    private static List<FrageDaten> fragen;

    private final Timer timer = new Timer();
    private final Frage frage = new Frage();
    private final List<Antwort> antworten = new ArrayList<>();
    private final List<Spieler> alleSpieler = new ArrayList<>();
    private final List<Partikel> partikel = new ArrayList<>();
    private final List<FrageDaten> verbleibendeFragen;
    private FrageDaten aktuelleFrage;
    private List<String> antwortenReihenfolge;
    private int verbleibendeZeit;

    HarryPotterQuiz(PApplet applet, Set<Spiel.Spieler> alleSpieler) {
        super(applet);
        verbleibendeFragen = new ArrayList<>(fragen);
        Collections.shuffle(verbleibendeFragen);
        while (verbleibendeFragen.size() != ANZAHL_FRAGEN) {
            verbleibendeFragen.remove(0);
        }

        for (Spiel.Spieler spieler : alleSpieler) {
            this.alleSpieler.add(new Spieler(spieler));
        }
        for (int i = 0; i < FrageDaten.ANTWORTEN; i++) {
            antworten.add(new Antwort(i));
        }
        naechsteFrage();
    }

    private void naechsteFrage() {
        verbleibendeZeit = ZEIT_PRO_FRAGE;
        aktuelleFrage = verbleibendeFragen.remove(0);
        antwortenReihenfolge = new ArrayList<>(aktuelleFrage.falscheAntworten);
        antwortenReihenfolge.add(aktuelleFrage.richtigeAntwort);
        Collections.shuffle(antwortenReihenfolge);
    }

    private void drawPartikel() {
        Iterator<Partikel> partikelIterator = partikel.iterator();
        while (partikelIterator.hasNext()) {
            Partikel partikel = partikelIterator.next();
            if (partikel.istUnsichtbar()) {
                partikelIterator.remove();
                continue;
            }
            partikel.draw();
        }
    }

    @Override
    Optional<List<Spiel.Spieler.Id>> draw() {
        applet.background(applet.color(255));

        verbleibendeZeit--;
        if (verbleibendeZeit <= 0) {
            Antwort richtigeAntwort = antworten.get(antwortenReihenfolge.indexOf(aktuelleFrage.richtigeAntwort));
            for (Spieler spieler : alleSpieler) {
                if (richtigeAntwort.isInside(spieler.x, spieler.y)) {
                    spieler.punkte++;
                    spawnPartikel(spieler.x+Spieler.SIZE/2, spieler.y+Spieler.SIZE/2);
                }
            }
            if (verbleibendeFragen.isEmpty()) {
                List<Spiel.Spieler.Id> rangliste = alleSpieler
                        .stream()
                        .sorted(Collections.reverseOrder(Comparator.comparingInt(spieler -> spieler.punkte)))
                        .map(spieler -> spieler.spieler.id)
                        .toList();
                return Optional.of(rangliste);
            }

            naechsteFrage();
        }

        timer.draw();
        frage.draw();
        for (Antwort antwort : antworten) {
            antwort.draw();
        }
        for (Spieler spieler : alleSpieler) {
            spieler.draw();
        }
        drawPartikel();

        return Optional.empty();
    }

    private void spawnPartikel(float x, float y) {
        for (int i = 0; i < 150; i++) {
            partikel.add(new Partikel(x, y));
        }
    }

    @Override
    void keyPressed() {
        for (Spieler spieler : alleSpieler) {
            spieler.keyPressed();
        }
    }

    @Override
    void keyReleased() {
        for (Spieler spieler : alleSpieler) {
            spieler.keyReleased();
        }
    }

    private final class Timer {
        private static final float HOEHE = 0.1f;
        private static final float TEXT_SIZE = HOEHE*(4f/5f);
        private static final float X = 0.5f;
        private static final float Y = HOEHE/2;

        private void draw() {
            applet.textAlign(PConstants.CENTER, PConstants.CENTER);
            applet.textSize(TEXT_SIZE * applet.height);
            applet.fill(applet.color(0));
            applet.text(Integer.toString(verbleibendeZeit / 60), X * applet.width, Y * applet.height);
        }
    }

    private final class Frage {
        private static final float HOEHE = 0.3f;
        private static final float TEXT_SIZE = 0.03f;
        private static final float X = 0.5f;
        private static final float Y = Timer.HOEHE + HOEHE/2;

        private void draw() {
            applet.textAlign(PConstants.CENTER, PConstants.CENTER);
            applet.textSize(TEXT_SIZE * applet.height);
            applet.fill(applet.color(0));
            applet.text(aktuelleFrage.frage, X * applet.width, Y * applet.height);
        }
    }

    private final class Antwort {
        private static final float HOEHE = 1f - Timer.HOEHE - Frage.HOEHE;
        private static final float TEXT_SIZE = 0.02f;
        private static final float BREITE = 1f / FrageDaten.ANTWORTEN;
        private static final float Y = Timer.HOEHE + Frage.HOEHE;
        private static final float TEXT_Y = Y + HOEHE/2;

        private final int index;

        private Antwort(int index) {
            this.index = index;
        }

        private float getX() {
            return index*BREITE;
        }

        private float getTextX() {
            return getX()+BREITE/2;
        }

        private void draw() {
            applet.textAlign(PConstants.CENTER, PConstants.CENTER);
            applet.textSize(TEXT_SIZE * applet.height);
            applet.fill(applet.color(0));
            applet.text(antwortenReihenfolge.get(index), getTextX()*applet.width, TEXT_Y*applet.height);
        }

        private boolean isInside(float x, float y) {
            float xStart = getX();
            float xEnde = xStart + BREITE;
            float yStart = Y;
            float yEnde = yStart+HOEHE;
            return x >= xStart && x <= xEnde && y >= yStart && y <= yEnde;
        }
    }

    private final class Spieler {
        private static final float MOVE_SPEED = 0.005f;
        private static final float SIZE = 0.05f;
        private static final float TEXT_SIZE = SIZE/2;
        private static final float START_X = 0.5f;
        private static final float START_Y = 0.5f;

        private final Spiel.Spieler spieler;
        private final Steuerung steuerung;
        private final int farbe = applet.color(applet.choice(255), applet.choice(255), applet.choice(255));
        private float x = START_X;
        private float y = START_Y;
        private int punkte;

        private Spieler(Spiel.Spieler spieler) {
            this.spieler = spieler;
            steuerung = getSteuerung();
        }

        private Steuerung getSteuerung() {
            switch (spieler.id) {
                case SPIELER_1:
                    return new PfeilTastenSteuerung();
                case SPIELER_2:
                    return new TastenSteuerung('a', 'd', 'w', 's');
                case SPIELER_3:
                    return new TastenSteuerung('f', 'h', 't', 'g');
                case SPIELER_4:
                    return new TastenSteuerung('j', 'l', 'i', 'k');
                default:
                    throw new IllegalArgumentException();
            }
        }

        private void draw() {
            applet.textAlign(PConstants.CENTER, PConstants.BOTTOM);
            applet.textSize(TEXT_SIZE * applet.height);
            applet.fill(farbe);
            applet.text(spieler.name, (x+SIZE/2) * applet.width, (y-TEXT_SIZE) * applet.height);

            applet.ellipseMode(PConstants.CORNER);
            applet.fill(farbe);
            applet.stroke(applet.color(0));
            applet.strokeWeight(2);
            float size = Math.max(SIZE * applet.width, SIZE * applet.height);
            applet.circle(x * applet.width, y * applet.height, size);

            steuerung.draw();
        }

        private void keyPressed() {
            steuerung.keyPressed();
        }

        private void keyReleased() {
            steuerung.keyReleased();
        }

        private abstract class Steuerung {
            private boolean links;
            private boolean rechts;
            private boolean oben;
            private boolean unten;

            private void draw() {
                if (links && x > 0) {
                    x -= MOVE_SPEED;
                }
                if (rechts && x < 1-SIZE) {
                    x += MOVE_SPEED;
                }
                if (oben && y > 0) {
                    y -= MOVE_SPEED;
                }
                if (unten && y < 1-SIZE) {
                    y += MOVE_SPEED;
                }
            }

            private void keyPressed() {
                if (isLinks()) {
                    links = true;
                }
                if (isRechts()) {
                    rechts = true;
                }
                if (isOben()) {
                    oben = true;
                }
                if (isUnten()) {
                    unten = true;
                }
            }

            private void keyReleased() {
                if (isLinks()) {
                    links = false;
                }
                if (isRechts()) {
                    rechts = false;
                }
                if (isOben()) {
                    oben = false;
                }
                if (isUnten()) {
                    unten = false;
                }
            }

            abstract boolean isLinks();

            abstract boolean isRechts();

            abstract boolean isOben();

            abstract boolean isUnten();
        }

        private final class TastenSteuerung extends Steuerung {
            private final char links;
            private final char rechts;
            private final char oben;
            private final char unten;

            private TastenSteuerung(char links, char rechts, char oben, char unten) {
                this.links = links;
                this.rechts = rechts;
                this.oben = oben;
                this.unten = unten;
            }

            @Override
            boolean isLinks() {
                return applet.key == links;
            }

            @Override
            boolean isRechts() {
                return applet.key == rechts;
            }

            @Override
            boolean isOben() {
                return applet.key == oben;
            }

            @Override
            boolean isUnten() {
                return applet.key == unten;
            }
        }

        private final class PfeilTastenSteuerung extends Steuerung {
            @Override
            boolean isLinks() {
                return applet.key == PConstants.CODED && applet.keyCode == PConstants.LEFT;
            }

            @Override
            boolean isRechts() {
                return applet.key == PConstants.CODED && applet.keyCode == PConstants.RIGHT;
            }

            @Override
            boolean isOben() {
                return applet.key == PConstants.CODED && applet.keyCode == PConstants.UP;
            }

            @Override
            boolean isUnten() {
                return applet.key == PConstants.CODED && applet.keyCode == PConstants.DOWN;
            }
        }
    }

    private final class Partikel {
        private static final float SIZE = 0.002f;
        private static final float MAX_GESCHWINDIGKEIT = 0.002f;
        private static final float TRANSPARENZ_AENDERUNG = -3f;

        private float x;
        private float y;
        private final float xGeschwindigkeit = applet.random(-MAX_GESCHWINDIGKEIT, MAX_GESCHWINDIGKEIT);
        private final float yGeschwindigkeit = applet.random(-MAX_GESCHWINDIGKEIT, MAX_GESCHWINDIGKEIT);
        private final int farbe = applet.color((int) applet.random(255), (int) applet.random(255), (int) applet.random(255));
        private float transparenz = 255;

        private Partikel(float x, float y) {
            this.x = x;
            this.y = y;
        }

        private boolean istUnsichtbar() {
            return transparenz <= 0;
        }

        private void draw() {
            x += xGeschwindigkeit;
            y += yGeschwindigkeit;
            transparenz += TRANSPARENZ_AENDERUNG;

            applet.rectMode(PConstants.CENTER);
            applet.noStroke();
            applet.fill(farbe);
            float size = Math.max(applet.width * SIZE, applet.height * SIZE);
            applet.rect(x * applet.width, y * applet.height, size, size);
        }
    }

    private static final class FrageDaten {
        private static final int ANTWORTEN = 4;

        private static List<FrageDaten> loadFragen(PApplet applet, String path) {
            String[] zeilen = applet.loadStrings(path);
            List<FrageDaten> result = new ArrayList<>();
            for (int zeile = 0; zeile < zeilen.length;) {
                String frage = zeilen[zeile++];
                String richtigeAntwort = zeilen[zeile++];
                List<String> falscheAntworten = new ArrayList<>();
                while (true) {
                    if (zeile >= zeilen.length) {
                        break;
                    }
                    String falscheAntwort = zeilen[zeile++];
                    if (falscheAntwort.isEmpty()) {
                        break;
                    }
                    falscheAntworten.add(falscheAntwort);
                }
                result.add(new FrageDaten(frage, richtigeAntwort, falscheAntworten));
            }
            return result;
        }

        private final String frage;
        private final String richtigeAntwort;
        private final List<String> falscheAntworten;

        private FrageDaten(String frage, String richtigeAntwort, List<String> falscheAntworten) {
            if (falscheAntworten.size() != ANTWORTEN-1) {
                throw new IllegalArgumentException();
            }

            this.frage = Objects.requireNonNull(frage);
            this.richtigeAntwort = Objects.requireNonNull(richtigeAntwort);
            this.falscheAntworten = List.copyOf(falscheAntworten);
        }
    }
}
