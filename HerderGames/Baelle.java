import processing.core.PApplet;
import processing.core.PConstants;

import java.util.*;

final class Baelle extends Spiel.Mehrspieler {
    static final Spiel.Mehrspieler.Factory FACTORY = new Factory("Bälle") {
        @Override
        Mehrspieler neuesSpiel(PApplet applet, Set<Spiel.Spieler> spieler) {
            return new Baelle(applet, spieler);
        }
    };

    private static final int BALL_DELAY = 30;

    private final Set<Ball> baelle = new HashSet<>();
    private int nextBall = BALL_DELAY;

    private final Set<Spieler> spieler = new HashSet<>();
    private final List<Spiel.Spieler.Id> rangliste = new ArrayList<>();

    private Baelle(PApplet applet, Set<Spiel.Spieler> alleSpieler) {
        super(applet);
        for (Spiel.Spieler spieler : alleSpieler) {
            this.spieler.add(new Spieler(spieler));
        }
    }

    @Override
    Optional<List<Spiel.Spieler.Id>> draw() {
        applet.background(255);

        nextBall--;
        if (nextBall <= 0) {
            baelle.add(new Ball());
            nextBall = BALL_DELAY;
        }

        Iterator<Spieler> spielerIterator = spieler.iterator();
        while (spielerIterator.hasNext()) {
            Spieler spieler = spielerIterator.next();
            if (spieler.istTot()) {
                spielerIterator.remove();
                rangliste.add(0, spieler.spieler.id);
            }
        }

        if (spieler.isEmpty()) {
            return Optional.of(rangliste);
        }

        for (Ball ball : baelle) {
            ball.kollisionenUeberpruefen();
        }

        for (Ball ball : baelle) {
            ball.draw();
        }

        for (Spieler spieler : spieler) {
            spieler.draw();
        }

        return Optional.empty();
    }

    @Override
    void keyPressed() {
        for (Spieler spieler : spieler) {
            spieler.keyPressed();
        }
    }

    @Override
    void keyReleased() {
        for (Spieler spieler : spieler) {
            spieler.keyReleased();
        }
    }

    private static final class Kreis {
        private final float mittelpunktX;
        private final float mittelpunktY;
        private final float radius;

        private Kreis(float mittelpunktX, float mittelpunktY, float radius) {
            this.mittelpunktX = mittelpunktX;
            this.mittelpunktY = mittelpunktY;
            this.radius = radius;
        }

        private boolean kollidiertMit(Kreis kreis) {
            return PApplet.dist(mittelpunktX, mittelpunktY, kreis.mittelpunktX, kreis.mittelpunktY) <= radius + kreis.radius;
        }

        private boolean istWegVomBildschirm() {
            return mittelpunktX-radius <= 0 || mittelpunktX+radius >= 1 || mittelpunktY-radius <= 0 || mittelpunktY+radius >= 1;
        }
    }

    private final class Ball {
        private static final float MIN_RADIUS = 0.01f;
        private static final float MAX_RADIUS = 0.02f;
        private static final float MIN_GESCHWINDIGKEIT = 0.001f;
        private static final float MAX_GESCHWINDIGKEIT = 0.004f;

        private float x;
        private float y;
        private float geschwindigkeitX;
        private float geschwindigkeitY;
        private final float radius;
        private final int color;

        private Ball() {
            radius = applet.random(MIN_RADIUS, MAX_RADIUS);
            geschwindigkeitX = applet.random(MIN_GESCHWINDIGKEIT, MAX_GESCHWINDIGKEIT);
            geschwindigkeitY = applet.random(MIN_GESCHWINDIGKEIT, MAX_GESCHWINDIGKEIT);
            findPosition(radius, geschwindigkeitX, geschwindigkeitY);
            color = applet.color(applet.random(255), applet.random(255), applet.random(255));
        }

        private void findPosition(float radius, float geschwindigkeitX, float geschwindigkeitY) {
        findPosition:
            while (true) {
                x = applet.random(radius*2, 1-radius*2);
                y = applet.random(radius*2, 1-radius*2);

                Kreis kreis = new Kreis(x, y, radius);

                for (Spieler spieler : spieler) {
                    float minXEnterfnungZuSpieler = Math.abs(geschwindigkeitX * applet.frameRate);
                    if (Math.abs(spieler.x - x) < minXEnterfnungZuSpieler) {
                        continue findPosition;
                    }

                    float minYEnterfnungZuSpieler = Math.abs(geschwindigkeitY * applet.frameRate);
                    if (Math.abs(spieler.y - y) < minYEnterfnungZuSpieler) {
                        continue findPosition;
                    }
                }

                for (Ball ball : baelle) {
                    if (ball.getKreis().kollidiertMit(kreis)) {
                        continue findPosition;
                    }
                }

                break;
            }
        }

        private boolean kollidiertMitAnderemBall() {
            for (Ball other : baelle) {
                if (other == this) {
                    continue;
                }

                if (getKreis().kollidiertMit(other.getKreis())) {
                    return true;
                }
            }

            return false;
        }

        private void kollisionenUeberpruefen() {
            if (kollidiertMitAnderemBall() || getKreis().istWegVomBildschirm()) {
                geschwindigkeitX *= -1;
                geschwindigkeitY *= -1;
            }
        }

        private void draw() {
            x += geschwindigkeitX;
            y += geschwindigkeitY;
            applet.fill(color);
            applet.ellipseMode(PConstants.CENTER);
            applet.circle(x*applet.width, y*applet.height, Math.max(radius*2*applet.width, radius*2*applet.height));
        }

        private Kreis getKreis() {
            return new Kreis(x, y, radius);
        }
    }

    private final class Spieler {
        private static final float RADIUS = 0.02f;
        private static final float MOVE_SPEED = 0.003f;
        private static final float X_START = 0.5f;
        private static final float Y_START = 0.5f;

        private final Spiel.Spieler spieler;
        private float x;
        private float y;
        private final Steuerung steuerung;

        private Spieler(Spiel.Spieler spieler) {
            this.spieler = spieler;
            x = X_START + getXOffset();
            y = Y_START;
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

        private float getXOffset() {
            switch (spieler.id) {
                case SPIELER_1:
                    return -RADIUS*6;
                case SPIELER_2:
                    return -RADIUS*2;
                case SPIELER_3:
                    return RADIUS*2;
                case SPIELER_4:
                    return RADIUS*6;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private void draw() {
            steuerung.draw();

            applet.fill(applet.color(255, 0, 0));
            applet.ellipseMode(PConstants.CENTER);
            applet.circle(x*applet.width, y*applet.height, Math.max(RADIUS*2*applet.width, RADIUS*2*applet.height));
        }

        private void keyPressed() {
            steuerung.keyPressed();
        }

        private void keyReleased() {
            steuerung.keyReleased();
        }

        private boolean kollidiertMitBall() {
            for (Ball ball : baelle) {
                if (ball.getKreis().kollidiertMit(getKreis())) {
                    return true;
                }
            }

            return false;
        }

        private boolean istTot() {
            return kollidiertMitBall() || getKreis().istWegVomBildschirm();
        }

        private Kreis getKreis() {
            return new Kreis(x, y, RADIUS);
        }

        private abstract class Steuerung {
            private boolean links;
            private boolean rechts;
            private boolean oben;
            private boolean unten;

            private void draw() {
                if (links) {
                    x -= MOVE_SPEED;
                }
                if (rechts) {
                    x += MOVE_SPEED;
                }
                if (oben) {
                    y -= MOVE_SPEED;
                }
                if (unten) {
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
                return applet.keyCode == PConstants.LEFT;
            }

            @Override
            boolean isRechts() {
                return applet.keyCode == PConstants.RIGHT;
            }

            @Override
            boolean isOben() {
                return applet.keyCode == PConstants.UP;
            }

            @Override
            boolean isUnten() {
                return applet.keyCode == PConstants.DOWN;
            }
        }
    }
}