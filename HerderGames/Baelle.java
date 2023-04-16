import processing.core.PApplet;
import processing.core.PConstants;

import java.util.*;

final class Baelle extends Spiel.Mehrspieler {
    private static final int BALL_DELAY = 30;

    private final Set<Ball> baelle = new HashSet<>();
    private int nextBall = BALL_DELAY;

    private final Set<Spieler> spieler = new HashSet<>();
    private final List<Spiel.Spieler.Id> rangliste = new ArrayList<>();

    Baelle(PApplet applet, Set<Spiel.Spieler> alleSpieler) {
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

        for (Ball ball : baelle) {
            ball.kollisionenUeberpruefen();
        }

        for (Ball ball : baelle) {
            ball.draw();
        }

        for (Spieler spieler : spieler) {
            spieler.draw();
        }

        if (spieler.isEmpty()) {
            return Optional.of(rangliste);
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
                    float minXEnterfnungZuSpieler = Math.abs(geschwindigkeitX * 30);
                    if (Math.abs(spieler.x - x) < minXEnterfnungZuSpieler) {
                        continue findPosition;
                    }

                    float minYEnterfnungZuSpieler = Math.abs(geschwindigkeitY * 30);
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
        private final Steuerung steuerung;
        private float x;
        private float y;

        private Spieler(Spiel.Spieler spieler) {
            this.spieler = spieler;
            steuerung = new Steuerung(applet, spieler.id);
            x = X_START + getXOffset();
            y = Y_START;
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
            x += steuerung.getXRichtung() * MOVE_SPEED;
            y += steuerung.getYRichtung() * MOVE_SPEED;

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
    }
}