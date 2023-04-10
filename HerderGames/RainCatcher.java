import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.util.*;

final class RainCatcher extends Spiel.Mehrspieler {
    static final Factory FACTORY = new Factory() {
        @Override
        Mehrspieler neuesSpiel(PApplet applet, Set<Spieler> spieler) {
            return new RainCatcher(applet, spieler);
        }
    };

    static void init(PApplet applet) {
        tropfenBild = applet.loadImage("raincatcher/tropfen", "jpg");
        tropfenNichtFangenBild = applet.loadImage("raincatcher/tropfen_nicht_fangen", "jpg");
    }

    private static final String PASSWORT = "Ich schwöre feierlich ich bin ein Tunichtgut";

    private static  PImage tropfenBild;
    private static PImage tropfenNichtFangenBild;

    private final List<Platform> platformen = new ArrayList<>();
    private final List<Spieler.Id> rangliste = new ArrayList<>();
    private final List<Tropfen> tropfen = new ArrayList<>();
    private int nextTropfen = Tropfen.DELAY;
    private final List<Partikel> partikel = new ArrayList<>();
    private final StringBuffer passwortInput = new StringBuffer();
    private boolean rumtreiber;

    private RainCatcher(PApplet applet, Set<Spieler> alleSpieler) {
        super(applet);
        for (Spieler spieler : alleSpieler) {
            platformen.add(new Platform(spieler));
        }
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

    private void spawnTropfen() {
        if (nextTropfen > 0) {
            nextTropfen--;
            return;
        }
        tropfen.add(new Tropfen());
        nextTropfen = Tropfen.DELAY;
    }

    private void drawTropfen() {
        Iterator<Tropfen> tropfenIterator = tropfen.iterator();
        while (tropfenIterator.hasNext()) {
            Tropfen tropfen = tropfenIterator.next();
            if (tropfen.untenRaus()) {
                tropfen.handleUntenRaus();
                tropfenIterator.remove();
                continue;
            }
            tropfen.draw();
        }
    }

    private void drawPlatformen() {
        Iterator<Platform> platformenIterator = platformen.iterator();
        while (platformenIterator.hasNext()) {
            Platform platform = platformenIterator.next();
            if (platform.gewonnen()) {
                rangliste.add(platform.spieler.id);
                platformenIterator.remove();
                continue;
            }
            platform.draw();
        }
    }

    @Override
    Optional<List<Spieler.Id>> draw() {
        applet.background(0);

        drawPartikel();
        spawnTropfen();
        drawTropfen();
        drawPlatformen();

        if (platformen.isEmpty()) {
            return Optional.of(rangliste);
        }

        return Optional.empty();
    }

    private void readPasswort() {
        if (applet.key == PConstants.CODED) {
            return;
        }

        passwortInput.append(applet.key);

        if (passwortInput.length() > PASSWORT.length()) {
           passwortInput.deleteCharAt(0);
        }

        if (passwortInput.toString().toLowerCase(Locale.ROOT).equals(PASSWORT.toLowerCase(Locale.ROOT))) {
            rumtreiber = true;

            PApplet.println("Die hochwohlgeborenen Herren Moony, Wurmschwanz, Tatze und Krone präsentieren stolz die Karte des Rumtreibers");

            for (float x = 0; x <= 1; x += 0.1f) {
                for (float y = 0; y <= 1; y += 0.1f) {
                    spawnPartikel(x, y);
                }
            }
        }
    }

    @Override
    void keyPressed() {
        readPasswort();

        for (Platform platform : platformen) {
            platform.keyPressed();
        }
    }

    @Override
    void keyReleased() {
        for (Platform platform : platformen) {
            platform.keyReleased();
        }
    }

    private void spawnPartikel(float x, float y) {
        for (int i = 0; i < 50; i++) {
            partikel.add(new Partikel(x, y));
        }
    }

    private final class Platform {
        private static final float BREITE = 0.2f;
        private static final float HOEHE = 0.05f;
        private static final float X_GESCHWINDIGKEIT = 0.003f;
        private static final float MIN_Y = 0.6f;
        private static final float MAX_Y = 1-HOEHE;
        private static final float Y_GESCHWINDIGKEIT = 0.0005f;
        private static final int ZIEL_PUNKTZAHL = 25;

        private final Spieler spieler;
        private final int farbe = applet.color((int) applet.random(255), (int) applet.random(255), (int) applet.random(255));
        private float x = 0.5f-BREITE/2;
        private float y;
        private float yGeschwindigkeit;
        private int punkte;
        private boolean links;
        private boolean rechts;

        private Platform(Spieler spieler) {
            this.spieler = spieler;
            y = getStartY();
            yGeschwindigkeit = getStartYGeschwindigkeit();
        }

        private float getStartY() {
            float abstand = (MAX_Y-MIN_Y) / 3;
            return MIN_Y+abstand*spieler.id.ordinal();
        }

        private float getStartYGeschwindigkeit() {
            if (spieler.id == Spieler.Id.SPIELER_1) {
                return -Y_GESCHWINDIGKEIT;
            }

            return Y_GESCHWINDIGKEIT;
        }

        private boolean gewonnen() {
            return punkte >= ZIEL_PUNKTZAHL;
        }

        private void draw() {
            moveX();
            moveY();
            tropfenFangen();
            drawPlatform();
            drawPunkte();
        }

        private void drawPlatform() {
            applet.rectMode(PConstants.CORNER);
            applet.fill(farbe);
            applet.noStroke();
            applet.rect(x * applet.width, y * applet.height, BREITE * applet.width, HOEHE*applet.height);
        }

        private void drawPunkte() {
            if (punkte <= 0) {
                return;
            }

            applet.textSize(applet.height * HOEHE);
            float alpha = ((float) punkte / ZIEL_PUNKTZAHL) * 255;
            applet.fill(applet.color(0, 0, 0, (int) alpha));
            applet.textAlign(PConstants.LEFT, PConstants.TOP);
            applet.text(Integer.toString(punkte), x * applet.width, y * applet.height);
        }

        private void tropfenFangen() {
            Iterator<Tropfen> tropfenIterator = tropfen.iterator();
            while (tropfenIterator.hasNext()) {
                Tropfen tropfen = tropfenIterator.next();

                if (tropfen.y+Tropfen.SIZE < y) {
                    continue;
                }

                if (tropfen.y > y+HOEHE) {
                    continue;
                }

                if (tropfen.x+Tropfen.SIZE < x) {
                    continue;
                }

                if (tropfen.x > x+BREITE) {
                    continue;
                }

                tropfenIterator.remove();

                if (tropfen.nichtFangen) {
                    punkte--;
                } else {
                    spawnPartikel(tropfen.x, tropfen.y);
                    punkte++;
                }
            }
        }

        private void moveY() {
            y += yGeschwindigkeit;
            if (y > MAX_Y) {
                yGeschwindigkeit = -Y_GESCHWINDIGKEIT;
            }
            if (y < MIN_Y) {
                yGeschwindigkeit = Y_GESCHWINDIGKEIT;
            }
        }

        private void moveX() {
            if (links && x > 0) {
                x -= X_GESCHWINDIGKEIT;
            }
            if (rechts && x+BREITE < 1) {
                x += X_GESCHWINDIGKEIT;
            }
        }

        private void keyPressed() {
            if (isLinksPressed()) {
                links = true;
            }
            if (isRechtsPressed()) {
                rechts = true;
            }
        }

        private void keyReleased() {
            if (isLinksPressed()) {
                links = false;
            }
            if (isRechtsPressed()) {
                rechts = false;
            }
        }

        private boolean isLinksPressed() {
            switch (spieler.id) {
                case SPIELER_1:
                    return applet.key == 'a';
                case SPIELER_2:
                    return applet.key == 'f';
                case SPIELER_3:
                    return applet.key == 'j';
                case SPIELER_4:
                    return applet.key == PConstants.CODED && applet.keyCode == PConstants.LEFT;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private boolean isRechtsPressed() {
            switch (spieler.id) {
                case SPIELER_1:
                    return applet.key == 'd';
                case SPIELER_2:
                    return applet.key == 'h';
                case SPIELER_3:
                    return applet.key == 'l';
                case SPIELER_4:
                    return applet.key == PConstants.CODED && applet.keyCode == PConstants.RIGHT;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    private final class Tropfen {
        private static final int DELAY = 60;
        private static final float SIZE = 0.03f;
        private static final float MIN_Y_GESCHWINDIGKEIT = 0.002f;
        private static final float MAX_Y_GESCHWINDIGKEIT = MIN_Y_GESCHWINDIGKEIT * 3;
        private static final float NICHT_FANGEN_WAHRSCHEINLICHKEIT = 0.2f;

        private final float x = applet.random(1-SIZE);
        private float y = 0;
        private final float yGeschwindigkeit = applet.random(MIN_Y_GESCHWINDIGKEIT, MAX_Y_GESCHWINDIGKEIT);
        private final boolean nichtFangen = applet.random(1) <= NICHT_FANGEN_WAHRSCHEINLICHKEIT;

        private void draw() {
            y += yGeschwindigkeit;

            if (rumtreiber) {
                drawFuerRumtreiber(); // Ignorieren Sie das am Besten einfach :)
            } else {
                drawFuerHerrHamdorf();
            }
        }

        private void drawFuerRumtreiber() {
            applet.imageMode(PConstants.CORNER);
            PImage image;
            if (nichtFangen) {
                image = tropfenNichtFangenBild;
            } else {
                image = tropfenBild;
            }
            float width = 0.1f;
            float height = 0.1f;
            applet.image(image, x * applet.width, y * applet.height, applet.width * width, applet.height * height);
        }

        private void drawFuerHerrHamdorf() {
            applet.ellipseMode(PConstants.CORNER);
            applet.noStroke();
            if (nichtFangen) {
                applet.fill(applet.color(255, 0, 0));
            } else {
                applet.fill(applet.color(0, 0, 255));
            }
            float size = Math.max(applet.width * SIZE, applet.height * SIZE);
            applet.circle(x * applet.width, y * applet.height, size);
        }

        private boolean untenRaus() {
            return y > 1;
        }

        private void handleUntenRaus() {
            if (nichtFangen) {
                spawnPartikel(x, y);
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
}
