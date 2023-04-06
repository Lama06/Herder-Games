import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.util.*;

final class FlappyOinky extends Spiel.Mehrspieler {
    static void init(PApplet applet) {
        hindernis = applet.loadImage("flappyoinky/obstacle.png");
        oinky = applet.loadImage("flappyoinky/oinky.png");
    }

    static final Spiel.Mehrspieler.Factory FACTORY = new Factory("Flappy Oinky") {
        @Override
        public Mehrspieler neuesSpiel(PApplet applet, Set<Spieler> spieler) {
            return new FlappyOinky(applet, spieler);
        }
    };

    private static final int HINDERNIS_DELAY = 170;

    private static PImage hindernis;
    private static PImage oinky;

    private final List<Spieler.Id> rangliste = new ArrayList<>();
    private final Set<Oinky> oinkys = new HashSet<>();
    private int nextHinderniss = 20;
    private final Set<Hindernis> hindernisse = new HashSet<>();
    private int punkte;

    private FlappyOinky(PApplet applet, Set<Spieler> alleSpieler) {
        super(applet);
        for (Spieler spieler : alleSpieler) {
            oinkys.add(new Oinky(spieler));
        }
    }

    @Override
    Optional<List<Spieler.Id>> draw() {
        nextHinderniss--;
        if (nextHinderniss <= 0) {
            hindernisse.add(new Hindernis());
            nextHinderniss = HINDERNIS_DELAY;
            punkte++;
        }

        applet.background(173, 216, 230);

        applet.textAlign(PApplet.CENTER);
        applet.textSize(30);
        applet.text(punkte, (float) applet.width/2, 50);


        Iterator<Hindernis> hindernisIterator = hindernisse.iterator();
        while (hindernisIterator.hasNext()) {
            Hindernis hindernis = hindernisIterator.next();
            if (hindernis.istWeg()) {
                hindernisIterator.remove();
            }
            hindernis.draw();
        }

        Iterator<Oinky> oinkyIterator = oinkys.iterator();
        while (oinkyIterator.hasNext()) {
            Oinky oinky = oinkyIterator.next();
            if (oinky.istRaus()) {
                oinkyIterator.remove();
                rangliste.add(0, oinky.spieler.id);
            }
        }

        if (oinkys.isEmpty()) {
            return Optional.of(rangliste);
        }

        for (Oinky oinky : oinkys) {
            oinky.draw();
        }

        return Optional.empty();
    }

    @Override
    void keyPressed() {
        for (Oinky oinky : oinkys) {
            oinky.keyPressed();
        }
    }

    private static final class Rechteck {
        private final float x;
        private final float y;
        private final float breite;
        private final float hoehe;

        private Rechteck(float x, float y, float breite, float hoehe) {
            this.x = x;
            this.y = y;
            this.breite = breite;
            this.hoehe = hoehe;
        }

        private boolean kollidiertMit(Rechteck anderes) {
            float thisMinX = x;
            float thisMaxX = x + breite;
            float anderesMinX = anderes.x;
            float anderesMaxX = anderes.x + anderes.breite;

            float thisMinY = y;
            float thisMaxY = y + hoehe;
            float anderesMinY = anderes.y;
            float anderesMaxY = anderes.y + anderes.hoehe;

            if (thisMaxX < anderesMinX || thisMinX > anderesMaxX) {
                return false;
            }

            if (thisMaxY < anderesMinY || thisMinY > anderesMaxY) {
                return false;
            }

            return true;
        }
    }

    private final class Oinky {
        private static final int HELP_TEXT_MAX_TIME = 120;
        private static final float SIZE = 0.05f;
        private static final float X = 0.5f - SIZE/2;
        private static final float Y_START = 0.5f - SIZE/2;
        private static final float GESCHWINDIGKEIT_Y_NACH_SPRUNG = -0.01f;
        private static final float GESCHWINDIGKEIT_Y_START = GESCHWINDIGKEIT_Y_NACH_SPRUNG*2;
        private static final float BESCHLEUNIGUNG_Y = 0.001f;
        private static final float MAX_DREHUNG = 30f;
        private static final float MAX_DREHUNG_AENDERUNG = 2f;

        private final Spieler spieler;
        private final float x;
        private float y = Y_START;
        private float geschwindigkeitY = GESCHWINDIGKEIT_Y_START;
        private float drehung = 0;
        private int helpTextTime;
        private boolean tot = false;

        private Oinky(Spieler spieler) {
            this.spieler = spieler;
            x = getXPosition();
        }

        private boolean isJumpKeyPressed() {
            switch (spieler.id) {
                case SPIELER_1:
                    return applet.key == 'w';
                case SPIELER_2:
                    return applet.key == ' ';
                case SPIELER_3:
                    return applet.key == PConstants.ENTER;
                case SPIELER_4:
                    return applet.keyCode == PConstants.UP;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private String getJumpKeyName() {
            switch (spieler.id) {
                case SPIELER_1:
                    return "W";
                case SPIELER_2:
                    return "Leertaste";
                case SPIELER_3:
                    return "Enter";
                case SPIELER_4:
                    return "Pfeiltaste hoch";
                default:
                    throw new IllegalArgumentException();
            }
        }

        private float getXPosition() {
            switch (spieler.id) {
                case SPIELER_1:
                    return X - SIZE*3;
                case SPIELER_2:
                    return X - SIZE;
                case SPIELER_3:
                    return X + SIZE;
                case SPIELER_4:
                    return X + SIZE*3;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private void keyPressed() {
            if (tot) {
                return;
            }

            if (!isJumpKeyPressed()) {
                return;
            }

            geschwindigkeitY = GESCHWINDIGKEIT_Y_NACH_SPRUNG;
        }

        private void draw() {
            for (Hindernis hindernis : hindernisse) {
                if (kollidiertMit(hindernis)) {
                    tot = true;
                    break;
                }
            }

            geschwindigkeitY += BESCHLEUNIGUNG_Y;
            y += geschwindigkeitY;

            float targetDrehung = geschwindigkeitY > 0 ? MAX_DREHUNG : -MAX_DREHUNG;
            if (targetDrehung > drehung) {
                float drehungAenderung = targetDrehung - drehung;
                if (drehungAenderung > MAX_DREHUNG_AENDERUNG) {
                    drehungAenderung = MAX_DREHUNG_AENDERUNG;
                }
                drehung += drehungAenderung;
            } else if (targetDrehung < drehung) {
                float drehungAenderung = drehung - targetDrehung;
                if (drehungAenderung > MAX_DREHUNG_AENDERUNG) {
                    drehungAenderung = MAX_DREHUNG_AENDERUNG;
                }
                drehung -= drehungAenderung;
            }

            Rechteck rechteck = getRechteck();

            applet.pushMatrix();
            applet.translate(rechteck.x * applet.width, rechteck.y * applet.height);
            applet.rotate(PApplet.radians(drehung));
            applet.imageMode(PApplet.CORNER);
            float size = Math.max(rechteck.breite * applet.width, rechteck.hoehe * applet.height);
            applet.image(oinky, 0, 0, size, size);
            applet.popMatrix();

            if (helpTextTime <= HELP_TEXT_MAX_TIME) {
                applet.textAlign(PConstants.CENTER);
                applet.textSize(30);
                applet.text(
                        "%s: %s".formatted(spieler.name, getJumpKeyName()),
                        (rechteck.x + SIZE/2) * applet.width,
                        (rechteck.y - SIZE) * applet.height
                );
                helpTextTime++;
            }
        }

        private boolean istRaus() {
            return y+SIZE < 0 || y > 1;
        }

        private boolean kollidiertMit(Hindernis hindernis) {
            for (Rechteck rechteck : hindernis.getRechtecke()) {
                if (rechteck.kollidiertMit(getRechteck())) {
                    return true;
                }
            }

            return false;
        }

        private Rechteck getRechteck() {
            return new Rechteck(x, y, SIZE, SIZE);
        }
    }

    private final class Hindernis {
        private static final float BREITE = 0.05f;
        private static final float LUECKE_HOEHE = 0.3f;
        private static final float X_START = 1f;
        private static final float X_GESCHWINDIGKEIT = -0.003f;

        private final float obererTeilHoehe;
        private final float untererTeilHoehe;
        private float x;

        private Hindernis() {
            obererTeilHoehe = applet.random(1 - LUECKE_HOEHE);
            untererTeilHoehe = 1 - LUECKE_HOEHE - obererTeilHoehe;
            x = X_START;
        }

        private void drawTeil(Rechteck rechteck) {
            float imageScaleFactor = (rechteck.breite * applet.width) / hindernis.width;
            float scaledImageWidth = hindernis.width * imageScaleFactor;
            float scaledImageHeight = hindernis.height * imageScaleFactor;

            for (
                    float hindernisY = rechteck.y * applet.height;
                    hindernisY < (rechteck.y + rechteck.hoehe) * applet.height;
                    hindernisY += scaledImageHeight
            ) {
                applet.imageMode(PApplet.CORNER);
                applet.image(hindernis, rechteck.x * applet.width, hindernisY, scaledImageWidth, scaledImageHeight);
            }
        }

        private void draw() {
            x += X_GESCHWINDIGKEIT;

            for (Rechteck rechteck : getRechtecke()) {
                drawTeil(rechteck);
            }
        }

        private boolean istWeg() {
            return x+BREITE < 0;
        }

        private Set<Rechteck> getRechtecke() {
            return Set.of(
                    new Rechteck(x, 0, BREITE, obererTeilHoehe),
                    new Rechteck(x, obererTeilHoehe + LUECKE_HOEHE, BREITE, untererTeilHoehe)
            );
        }
    }
}
