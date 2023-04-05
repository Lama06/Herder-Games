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

    private static final int HINDERNIS_DELAY = 200;

    private final Set<Oinky> oinkys = new HashSet<>();

    private int nextHinderniss = 20;
    private final Set<Hindernis> hindernisse = new HashSet<>();

    private int punkte;

    FlappyOinky(PApplet applet) {
        super(applet);
        // Hier height verwenden, weil die Größe der Oinkys auch mit height berechnet wird
        oinkys.add(new Oinky(() -> applet.key == 'w', applet.width/2-applet.height/15));
        oinkys.add(new Oinky(() -> applet.keyCode == PApplet.UP, applet.width/2+applet.height/15));
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
        private static final float MAX_DREHUNG_AENDERUNG = 2f;

        private final BooleanSupplier keyTest;
        private final float size;
        private final float x;
        private float y;
        private float geschwindigkeitY = 0;
        private final float beschleunigungY;
        private final float geschwindigkeitYNachSprung;
        private float drehung = 0;
        private boolean tot = false;

        private Oinky(BooleanSupplier keyTest, int x) {
            this.keyTest = keyTest;
            size = (float) applet.height / 10;
            this.x = x;
            y = (float) applet.height / 2;
            beschleunigungY = (float) applet.height / 700;
            geschwindigkeitYNachSprung = (float) applet.height / -60;
        }

        private void keyPressed() {
            if (tot) {
                return;
            }

            if (!keyTest.getAsBoolean()) {
                return;
            }

            geschwindigkeitY = geschwindigkeitYNachSprung;
        }

        private void draw() {
            for (Hindernis hindernis : hindernisse) {
                if (kollidiertMit(hindernis)) {
                    tot = true;
                    break;
                }
            }

            geschwindigkeitY += beschleunigungY;
            y += geschwindigkeitY;

            float targetDrehung = geschwindigkeitY > 0 ? 30 : -30;
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
            applet.translate(rechteck.x, rechteck.y);
            applet.rotate(PApplet.radians(drehung));
            applet.imageMode(PApplet.CORNER);
            applet.image(oinky, 0, 0, rechteck.breite, rechteck.hoehe);
            applet.popMatrix();
        }

        private boolean istRaus() {
            return y+size < 0 || y > applet.height;
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
            return new Rechteck(x, y, size, size);
        }
    }

    private final class Hindernis {
        private final float breite;
        private final float luecke;
        private final float obereHoehe;
        private final float untereHoehe;
        private float x;
        private final float xGeschwindigkeit;

        private Hindernis() {
            breite = (float) applet.width / 20;
            luecke = (float) applet.height / 2.7f;
            obereHoehe = applet.random(applet.height - luecke);
            untereHoehe = applet.height - luecke - obereHoehe;
            x = applet.width;
            xGeschwindigkeit = (float) applet.width / -600;
        }

        private void drawTeil(Rechteck rechteck) {
            float scaleFactor = rechteck.breite / hindernis.width;
            float scaledHindernisWidth = hindernis.width * scaleFactor;
            float scaledHindernisHeight = hindernis.height * scaleFactor;

            for (float hindernisY = rechteck.y; hindernisY < rechteck.y + rechteck.hoehe; hindernisY += scaledHindernisHeight) {
                applet.imageMode(PApplet.CORNER);
                applet.image(hindernis, rechteck.x, hindernisY, scaledHindernisWidth, scaledHindernisHeight);
            }
        }

        private void draw() {
            x += xGeschwindigkeit;

            for (Rechteck rechteck : getRechtecke()) {
                drawTeil(rechteck);
            }
        }

        private boolean istWeg() {
            return x+breite < 0;
        }

        private Set<Rechteck> getRechtecke() {
            return Set.of(new Rechteck(x, 0, breite, obereHoehe), new Rechteck(x, obereHoehe+luecke, breite, untereHoehe));
        }
    }
}
