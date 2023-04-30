import processing.core.PApplet;
import processing.core.PConstants;

import java.util.*;

final class BreakOut extends Spiel.Mehrspieler {
    private final List<Welt> welten = new ArrayList<>();

    BreakOut(PApplet applet, Set<Spieler> alleSpieler) {
        super(applet);
        List<Spieler> spielerSortiert = alleSpieler.stream().sorted(Comparator.comparing(spieler -> spieler.id)).toList();
        for (Spieler spieler : spielerSortiert) {
            welten.add(new Welt(this, spieler));
        }
    }

    @Override
    Optional<List<Spieler.Id>> draw() {
        applet.background(0);

        for (Welt welt : welten) {
            welt.draw();
        }

        return Optional.empty();
    }

    @Override
    void keyPressed() {
        welten.forEach(Welt::keyPressed);
    }

    @Override
    void keyReleased() {
        welten.forEach(Welt::keyReleased);
    }

    private static final class Welt {
        private final BreakOut spiel;
        private final PApplet applet;
        private final Spieler spieler;
        private final List<Entity> entities = new ArrayList<>();

        private Welt(BreakOut spiel, Spieler spieler) {
            this.spiel = spiel;
            this.spieler = spieler;
            applet = spiel.applet;

            entities.add(new Platform(this));
            entities.add(new Ball(this));

            boolean geradeReihe = true;
            for (float y = 0; y+Stein.HOEHE < 0.4f; y += Stein.HOEHE + Stein.ABSTAND_Y, geradeReihe = !geradeReihe) {
                for (float x = geradeReihe ? Stein.ABSTAND_X : 0; x+Stein.BREITE < 1; x += Stein.BREITE + Stein.ABSTAND_X) {
                    entities.add(new Stein(this, x, y));
                }
            }
        }

        private void draw() {
            List<Runnable> nachDrawCallbacks = new ArrayList<>();
            for (Entity entity : entities) {
                nachDrawCallbacks.addAll(entity.draw());
            }
            for (Runnable nachDrawCallback : nachDrawCallbacks) {
                nachDrawCallback.run();
            }
        }

        private void keyPressed() {
            entities.forEach(Entity::keyPressed);
        }

        private void keyReleased() {
            entities.forEach(Entity::keyReleased);
        }

        private int getIndex() {
            return spiel.welten.indexOf(this);
        }

        private float getBreite() {
            return 1f / spiel.welten.size();
        }

        private float getXStart() {
            return getIndex() * getBreite();
        }
    }

    private static abstract class Entity {
        final Welt welt;
        final PApplet applet;

        private Entity(Welt welt) {
            this.welt = welt;
            applet = welt.applet;
        }

        abstract List<Runnable> draw();

        void keyPressed() { }

        void keyReleased() { }
    }

    private static final class Ball extends Entity {
        private static final float SIZE = 0.02f;
        private static final float GESCHWINDIGKEIT_Y = 0.006f;
        private static final float MAX_GESCHWINDIGKEIT_X = 0.004f;

        private float geschwindigkeitY = GESCHWINDIGKEIT_Y;
        private float geschwindigkeitX = 0;
        private float x = 0.5f;
        private float y = 0.5f;

        private Ball(Welt welt) {
            super(welt);
        }

        private boolean kollidiertMitRandObenUnten() {
            return y <= 0 || y+SIZE >= 1;
        }

        private boolean kollidiertMitRandLinksRechts() {
            return x <= 0 || x+SIZE >= 1;
        }

        private boolean kollidiertMitRand() {
            return kollidiertMitRandObenUnten() || kollidiertMitRandLinksRechts();
        }

        private Optional<Platform> getKollidierendePlatform() {
            return welt.entities.stream()
                    .filter(entity -> entity instanceof Platform)
                    .map(entity -> ((Platform) entity))
                    .filter(platform -> platform.getRechteck().kollidiertMit(getRechteck()))
                    .findAny();
        }

        private Optional<Stein> getKollidierendenStein() {
            return welt.entities.stream()
                    .filter(entity -> entity instanceof Stein)
                    .map(entity -> ((Stein) entity))
                    .filter(stein -> stein.getRechteck().kollidiertMit(getRechteck()))
                    .findAny();
        }

        private void vonRandAbprallen() {
            if (kollidiertMitRandObenUnten()) {
                geschwindigkeitY *= -1;
            }
            if (kollidiertMitRandLinksRechts()) {
                geschwindigkeitX *= -1;
            }
        }

        private void vonPlatformAbprallen(Platform platform) {
            Rechteck platformRechteck = platform.getRechteck();

            float platformXMitte = platformRechteck.getXMitte();
            float neueGeschwindigkeitX = MAX_GESCHWINDIGKEIT_X * (Math.abs(getRechteck().getXMitte() - platformXMitte) / (platformRechteck.breite/2));
            if (getRechteck().getXMitte() < platformXMitte) {
                neueGeschwindigkeitX *= -1;
            }

            geschwindigkeitX = neueGeschwindigkeitX;
            geschwindigkeitY *= -1;
        }

        private List<Runnable> vonSteinAbprallen(Stein stein) {
            geschwindigkeitY *= -1;

            return List.of(() -> welt.entities.remove(stein));
        }

        private List<Runnable> bewegen() {
            boolean kollidiertMitRandVorher = kollidiertMitRand();
            Optional<Platform> kollidierendePlatformVorher = getKollidierendePlatform();
            Optional<Stein> kollidierenderSteinVorher = getKollidierendenStein();

            x += geschwindigkeitX;
            y += geschwindigkeitY;

            boolean kollidiertMitRandNachher = kollidiertMitRand();
            Optional<Platform> kollidierendePlatformNachher = getKollidierendePlatform();
            Optional<Stein> kollidierenderSteinNachher = getKollidierendenStein();

            if (!kollidiertMitRandVorher && kollidiertMitRandNachher) {
                vonRandAbprallen();
            }

            if (kollidierendePlatformVorher.isEmpty() && kollidierendePlatformNachher.isPresent()) {
                vonPlatformAbprallen(kollidierendePlatformNachher.get());
            }

            if (kollidierenderSteinVorher.isEmpty() && kollidierenderSteinNachher.isPresent()) {
                return vonSteinAbprallen(kollidierenderSteinNachher.get());
            }

            return Collections.emptyList();
        }

        @Override
        List<Runnable> draw() {
            applet.ellipseMode(PConstants.CORNER);
            applet.fill(applet.color(255, 0, 0));
            float size = Math.min(SIZE * welt.getBreite() * applet.width, SIZE * applet.height);
            applet.circle(
                    (welt.getXStart() + x*welt.getBreite()) * applet.width,
                    y * applet.height,
                    size
            );

            return bewegen();
        }

        private Rechteck getRechteck() {
            return new Rechteck(x, y, SIZE, SIZE);
        }
    }

    private static final class Platform extends Entity {
        private static final float BREITE = 0.1f;
        private static final float HOEHE = 0.02f;
        private static final float BEWEGUNGS_GESCHWINDIGKEIT = 0.006f;
        private static final float Y = 1-HOEHE*2;

        private final Steuerung steuerung;
        private float x = 0.5f-BREITE/2;

        private Platform(Welt welt) {
            super(welt);
            steuerung = new Steuerung(applet, welt.spieler.id);
        }

        private void bewegen() {
            if (steuerung.istLinksGedrueckt() && x > 0) {
                x -= BEWEGUNGS_GESCHWINDIGKEIT;
            }
            if (steuerung.istRechtsGedrueckt() && x < 1) {
                x += BEWEGUNGS_GESCHWINDIGKEIT;
            }
        }

        @Override
        List<Runnable> draw() {
            bewegen();

            applet.rectMode(PConstants.CORNER);
            applet.fill(applet.color(0, 0, 255));
            applet.rect(
                    (welt.getXStart() + x*welt.getBreite()) * applet.width,
                    Y * applet.height,
                    BREITE * welt.getBreite() * applet.width,
                    HOEHE * applet.height
            );

            return Collections.emptyList();
        }

        @Override
        void keyPressed() {
            steuerung.keyPressed();
        }

        @Override
        void keyReleased() {
            steuerung.keyReleased();
        }

        private Rechteck getRechteck() {
            return new Rechteck(x, Y, BREITE, HOEHE);
        }
    }

    private static final class Stein extends Entity {
        private static final float BREITE = 0.05f;
        private static final float HOEHE = 0.03f;
        private static final float ABSTAND_X = BREITE / 2;
        private static final float ABSTAND_Y = HOEHE / 2;

        private final float x;
        private final float y;

        private Stein(Welt welt, float x, float y) {
            super(welt);
            this.x = x;
            this.y = y;
        }

        @Override
        List<Runnable> draw() {
            applet.rectMode(PConstants.CORNER);
            applet.fill(applet.color(0, 0, 255));
            applet.rect(
                    (welt.getXStart() + x*welt.getBreite()) * applet.width,
                    y * applet.height,
                    BREITE * welt.getBreite() * applet.width,
                    HOEHE * applet.height
            );

            return Collections.emptyList();
        }

        private Rechteck getRechteck() {
            return new Rechteck(x, y, BREITE, HOEHE);
        }
    }
}
