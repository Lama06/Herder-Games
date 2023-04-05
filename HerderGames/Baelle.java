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
        spieler.add(new Spieler((float) applet.width/50, SpielerSteuerungType.WASD));
        spieler.add(new Spieler((float) applet.width/50, SpielerSteuerungType.PFEILTASTEN));
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
    }

    private static float getMinBallRadius(PApplet applet) {
        return (float) applet.width / 100;
    }

    private static float getMaxBallRadius(PApplet applet) {
        return (float) applet.width / 50;
    }

    private static float getMinBallGeschwindigkeit(PApplet applet) {
        return (float) applet.width / 1000;
    }

    private static float getMaxBallGeschwindigkeit(PApplet applet) {
        return (float) applet.width / 250;
    }

    private static float getNeuerBallMinEntfernungZuSpieler(PApplet applet, float geschwindigkeit) {
        return geschwindigkeit * applet.frameRate; // ca 1 Sekunde Reaktionszeit
    }

    private final class Ball {
        private float x;
        private float y;
        private float geschwindigkeitX;
        private float geschwindigkeitY;
        private final float radius;
        private final int color;

        private Ball() {
            radius = applet.random(getMinBallRadius(applet), getMaxBallRadius(applet));
            geschwindigkeitX = applet.random(getMinBallGeschwindigkeit(applet), getMaxBallGeschwindigkeit(applet));
            geschwindigkeitY = applet.random(getMinBallGeschwindigkeit(applet), getMaxBallGeschwindigkeit(applet));
            float[] position = findPosition(radius, geschwindigkeitX, geschwindigkeitY);
            x = position[0];
            y = position[1];
            color = applet.color(applet.random(255), applet.random(255), applet.random(255));
        }

        private float[] findPosition(float radius, float geschwindigkeitX, float geschwindigkeitY) {
            float x, y;
        findPosition:
            while (true) {
                x = applet.random(radius*2, applet.width-radius*2);
                y = applet.random(radius*2, applet.height-radius*2);

                Kreis kreis = new Kreis(x, y, radius);

                for (Spieler spieler : spieler) {
                    if (spieler.getKreis().kollidiertMit(kreis)) {
                        continue findPosition;
                    }

                    if (Math.abs(spieler.x - x) < getNeuerBallMinEntfernungZuSpieler(applet, geschwindigkeitX)) {
                        continue findPosition;
                    }

                    if (Math.abs(spieler.y - y) < getNeuerBallMinEntfernungZuSpieler(applet, geschwindigkeitY)) {
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
            return new float[] {x, y};
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

        private boolean istWegVomBildschirm() {
            return x < 0 || x > applet.width || y < 0 || y > applet.height;
        }

        private void kollisionenUeberpruefen() {
            if (kollidiertMitAnderemBall() || istWegVomBildschirm()) {
                geschwindigkeitX *= -1;
                geschwindigkeitY *= -1;
            }
        }

        private void draw() {
            x += geschwindigkeitX;
            y += geschwindigkeitY;
            applet.fill(color);
            applet.ellipseMode(PConstants.CENTER);
            applet.circle(x, y, radius*2);
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

    private static int getSpielerColor(PApplet applet) {
        return applet.color(255, 0, 0);
    }

    private static float getSpielerRadius(PApplet applet) {
        return (float) applet.width / 50;
    }

    private static float getSpielerMoveSpeed(PApplet applet) {
        return (float) applet.width / 300;
    }

    private final class Spieler {
        private float x;
        private float y;
        private final Steuerung steuerung;

        private Spieler(float xOffset, SpielerSteuerungType steuerungType) {
            this.x = (float) applet.width / 2 + xOffset;
            y = (float) applet.height / 2;
            steuerung = steuerungType.create(this);
        }

        private void draw() {
            steuerung.draw();

            applet.fill(getSpielerColor(applet));
            applet.ellipseMode(PConstants.CENTER);
            applet.circle(x, y, getSpielerRadius(applet));
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

        private boolean istWegVomBildschirm() {
            return x < 0 || x > applet.width || y < 0 || y > applet.height;
        }

        private boolean istTot() {
            return kollidiertMitBall() || istWegVomBildschirm();
        }

        private Kreis getKreis() {
            return new Kreis(x, y, getSpielerRadius(applet));
        }

        private abstract class Steuerung {
            private boolean links;
            private boolean rechts;
            private boolean oben;
            private boolean unten;

            private void draw() {
                if (links) {
                    x -= getSpielerMoveSpeed(applet);
                }
                if (rechts) {
                    x += getSpielerMoveSpeed(applet);
                }
                if (oben) {
                    y -= getSpielerMoveSpeed(applet);
                }
                if (unten) {
                    y += getSpielerMoveSpeed(applet);
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
