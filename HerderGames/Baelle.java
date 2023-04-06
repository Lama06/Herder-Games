import processing.core.PApplet;
import processing.core.PConstants;

import java.util.HashSet;
import java.util.Set;

final class Baelle extends MiniSpiel {
    private static final int BALL_DELAY = 30;

    private final Set<Ball> baelle = new HashSet<>();
    private int nextBall = BALL_DELAY;

    private final Set<Spieler> spieler = new HashSet<>();

    Baelle(PApplet applet) {
        super(applet);
        spieler.add(new Spieler(Spieler.RADIUS*2, SpielerSteuerungType.WASD));
        spieler.add(new Spieler(Spieler.RADIUS*-2, SpielerSteuerungType.PFEILTASTEN));
    }

    @Override
    void draw() {
        applet.background(255);

        nextBall--;
        if (nextBall <= 0) {
            baelle.add(new Ball());
            nextBall = BALL_DELAY;
        }

        spieler.removeIf(Spieler::istTot);

        for (Ball ball : baelle) {
            ball.kollisionenUeberpruefen();
        }

        for (Ball ball : baelle) {
            ball.draw();
        }

        for (Spieler spieler : spieler) {
            spieler.draw();
        }
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

    private enum SpielerSteuerungType {
        WASD,
        PFEILTASTEN;

        private Spieler.Steuerung create(Spieler spieler) {
            switch (this) {
                case WASD:
                    return spieler.new WASDSteuerung();
                case PFEILTASTEN:
                    return spieler.new PfeilTastenSteuerung();
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    private final class Spieler {
        private static final float RADIUS = 0.02f;
        private static final float MOVE_SPEED = 0.003f;
        private static final float X_START = 0.5f;
        private static final float Y_START = 0.5f;

        private float x;
        private float y;
        private final Steuerung steuerung;

        private Spieler(float xOffset, SpielerSteuerungType steuerungType) {
            x = X_START+xOffset;
            y = Y_START;
            steuerung = steuerungType.create(this);
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

        private final class WASDSteuerung extends Steuerung {
            @Override
            boolean isLinks() {
                return applet.key == 'a';
            }

            @Override
            boolean isRechts() {
                return applet.key == 'd';
            }

            @Override
            boolean isOben() {
                return applet.key == 'w';
            }

            @Override
            boolean isUnten() {
                return applet.key == 's';
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
