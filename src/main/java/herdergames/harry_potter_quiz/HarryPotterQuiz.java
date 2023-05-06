package herdergames.harry_potter_quiz;

import herdergames.spiel.MehrspielerSpiel;
import herdergames.util.PartikelManager;
import herdergames.util.Steuerung;
import processing.core.PApplet;
import processing.core.PConstants;

import java.util.*;

public final class HarryPotterQuiz extends MehrspielerSpiel {
    public static void init(PApplet applet) {
        fragen = Collections.unmodifiableList(FrageDaten.loadFragen(applet, "harrypotter/fragen.txt"));
    }

    private static final int ZEIT_PRO_FRAGE = 8*60;
    private static final int ANZAHL_FRAGEN = 20;

    private static List<FrageDaten> fragen;

    private final Timer timer = new Timer();
    private final Frage frage = new Frage();
    private final List<Antwort> antworten = new ArrayList<>();
    private final List<Spieler> alleSpieler = new ArrayList<>();
    private final PartikelManager partikelManager = new PartikelManager(applet);
    private final List<FrageDaten> verbleibendeFragen;
    private FrageDaten aktuelleFrage;
    private List<String> antwortenReihenfolge;
    private int verbleibendeZeit;

    public HarryPotterQuiz(PApplet applet, Set<herdergames.spiel.Spieler> alleSpieler) {
        super(applet);
        verbleibendeFragen = new ArrayList<>(fragen);
        Collections.shuffle(verbleibendeFragen);
        while (verbleibendeFragen.size() != ANZAHL_FRAGEN) {
            verbleibendeFragen.remove(0);
        }

        for (herdergames.spiel.Spieler spieler : alleSpieler) {
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

    @Override
    public Optional<List<herdergames.spiel.Spieler.Id>> draw() {
        applet.background(applet.color(255));

        verbleibendeZeit--;
        if (verbleibendeZeit <= 0) {
            Antwort richtigeAntwort = antworten.get(antwortenReihenfolge.indexOf(aktuelleFrage.richtigeAntwort));
            for (Spieler spieler : alleSpieler) {
                if (richtigeAntwort.isInside(spieler.x, spieler.y)) {
                    spieler.punkte++;
                    partikelManager.spawnPartikel(spieler.x+Spieler.SIZE/2, spieler.y+Spieler.SIZE/2);
                }
            }
            if (verbleibendeFragen.isEmpty()) {
                List<herdergames.spiel.Spieler.Id> rangliste = alleSpieler
                        .stream()
                        .sorted(Collections.reverseOrder(Comparator.comparingInt(spieler -> spieler.punkte)))
                        .map(spieler -> spieler.spieler.id())
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
        partikelManager.draw();

        return Optional.empty();
    }

    @Override
    public void keyPressed() {
        for (Spieler spieler : alleSpieler) {
            spieler.keyPressed();
        }
    }

    @Override
    public void keyReleased() {
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

        private final herdergames.spiel.Spieler spieler;
        private final Steuerung steuerung;
        private final int farbe = applet.color(applet.choice(255), applet.choice(255), applet.choice(255));
        private float x = START_X;
        private float y = START_Y;
        private int punkte;

        private Spieler(herdergames.spiel.Spieler spieler) {
            this.spieler = spieler;
            steuerung = new Steuerung(applet, spieler.id());
        }

        private void draw() {
            applet.textAlign(PConstants.CENTER, PConstants.BOTTOM);
            applet.textSize(TEXT_SIZE * applet.height);
            applet.fill(farbe);
            applet.text(spieler.name(), (x+SIZE/2) * applet.width, (y-TEXT_SIZE) * applet.height);

            applet.ellipseMode(PConstants.CORNER);
            applet.fill(farbe);
            applet.stroke(applet.color(0));
            applet.strokeWeight(2);
            float size = Math.max(SIZE * applet.width, SIZE * applet.height);
            applet.circle(x * applet.width, y * applet.height, size);

            x += steuerung.getXRichtung() * MOVE_SPEED;
            y += steuerung.getYRichtung() * MOVE_SPEED;
        }

        private void keyPressed() {
            steuerung.keyPressed();
        }

        private void keyReleased() {
            steuerung.keyReleased();
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
