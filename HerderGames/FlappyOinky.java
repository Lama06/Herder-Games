import processing.core.PApplet;
import processing.core.PImage;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BooleanSupplier;

final class FlappyOinky extends MiniSpiel {
    private static PImage hindernis;
    private static PImage oinky;

    static void init(PApplet applet) {
        hindernis = applet.loadImage("flappyoinky/obstacle.png");
        oinky = applet.loadImage("flappyoinky/oinky.png");
    }

    private static final int HINDERNIS_DELAY = 170;

    private final Set<Oinky> oinkys = new HashSet<>();

    private int nextHinderniss = 20;
    private final Set<Hindernis> hindernisse = new HashSet<>();

    private int punkte;

    FlappyOinky(PApplet applet) {
        super(applet);
        oinkys.add(new Oinky(() -> applet.key == 'w', -Oinky.SIZE));
        oinkys.add(new Oinky(() -> applet.keyCode == PApplet.UP, Oinky.SIZE));
    }

    @Override
    void draw() {
        nextHinderniss--;
        if (nextHinderniss <= 0) {
            hindernisse.add(new Hindernis());
            nextHinderniss = HINDERNIS_DELAY;
            punkte++;
        }

        applet.background(173, 216, 230);

        applet.textAlign(PApplet.CENTER);
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
                continue;
            }
            oinky.draw();
        }
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
        private static final float SIZE = 0.05f;
        private static final float X = 0.5f - SIZE/2;
        private static final float Y_START = 0.5f - SIZE/2;
        private static final float GESCHWINDIGKEIT_Y_NACH_SPRUNG = -0.01f;
        private static final float GESCHWINDIGKEIT_Y_START = GESCHWINDIGKEIT_Y_NACH_SPRUNG*2;
        private static final float BESCHLEUNIGUNG_Y = 0.001f;
        private static final float MAX_DREHUNG = 30f;
        private static final float MAX_DREHUNG_AENDERUNG = 2f;

        private final BooleanSupplier keyTest;
        private final float x;
        private float y = Y_START;
        private float geschwindigkeitY = GESCHWINDIGKEIT_Y_START;
        private float drehung = 0;
        private boolean tot = false;

        private Oinky(BooleanSupplier keyTest, float xOffset) {
            x = X + xOffset;
            this.keyTest = keyTest;
        }

        private void keyPressed() {
            if (tot) {
                return;
            }

            if (!keyTest.getAsBoolean()) {
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

            applet.pushMatrix();
            Rechteck rechteck = getRechteck();
            applet.translate(rechteck.x * applet.width, rechteck.y * applet.height);
            applet.rotate(PApplet.radians(drehung));
            applet.imageMode(PApplet.CORNER);
            float size = Math.max(rechteck.breite * applet.width, rechteck.hoehe * applet.height);
            applet.image(oinky, 0, 0, size, size);
            applet.popMatrix();
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
