package herdergames.pong;

import herdergames.spiel.SpielerGegenSpielerSpiel;
import herdergames.util.PartikelManager;
import processing.core.PApplet;
import processing.core.PConstants;

import java.util.Optional;

public final class Pong extends SpielerGegenSpielerSpiel {
    private final herdergames.spiel.Spieler spieler1;
    private final herdergames.spiel.Spieler spieler2;
    private final PartikelManager partikelManager = new PartikelManager(applet);
    private final Ball ball = new Ball(applet);
    private final Spieler p1 = new Spieler(applet, 20);
    private final Spieler p2 = new Spieler(applet, applet.width - 70);

    private boolean wPressed = false;
    private boolean sPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;

    private int punkteP1 = 0;
    private int punkteP2 = 0;

    public Pong(PApplet applet, herdergames.spiel.Spieler spieler1, herdergames.spiel.Spieler spieler2) {
        super(applet);
        this.spieler1 = spieler1;
        this.spieler2 = spieler2;
    }

    @Override
    public Optional<Optional<herdergames.spiel.Spieler.Id>> draw() {
        if (punkteP1 < 5 && punkteP2 < 5) { // Wenn kein spieler gewonnen hat
            spielerBewegen();
            rand();
            kollision();

            applet.background(0);

            applet.fill(255);
            applet.strokeWeight(5);
            applet.stroke(255);
            applet.line(applet.width/2f, 0, applet.width/2f, applet.height);

            applet.noStroke();
            applet.textSize(50);
            applet.text(punkteP1, applet.width/4f-1, 50);
            applet.text(punkteP2, applet.width/4f*3-1, 50);

            applet.ellipse(ball.xPos, ball.yPos, ball.durchmesser, ball.durchmesser);

            ball.xPos += ball.xSpeed;
            ball.yPos += ball.ySpeed;

            applet.rect(p1.xPos, p1.yPos, p1.breite, p1.hoehe);
            applet.rect(p2.xPos, p2.yPos, p2.breite, p2.hoehe);

            partikelManager.draw();
        } else if (punkteP1 == 5) { // Wenn ein spieler gewonnnen hat
            return Optional.of(Optional.of(spieler1.id()));
        } else if (punkteP2 == 5) {
            return Optional.of(Optional.of(spieler2.id()));
        }

        return Optional.empty();
    }

    @Override
    public void keyPressed() {
        if (applet.key == 'w') {
            wPressed = true;
        } else if (applet.key == 's') {
            sPressed = true;
        } else if (applet.keyCode == PConstants.UP) {
            upPressed = true;
        } else if (applet.keyCode == PConstants.DOWN) {
            downPressed = true;
        }
    }

    @Override
    public void keyReleased() {
        if (applet.key == 'w') {
            wPressed = false;
        } else if (applet.key == 's') {
            sPressed = false;
        } else if (applet.keyCode == PConstants.UP) {
            upPressed = false;
        } else if (applet.keyCode == PConstants.DOWN) {
            downPressed = false;
        }
    }

    private void spielerBewegen() {
        if (wPressed) {
            p1.yPos = p1.yPos - 7;
        }
        if (sPressed) {
            p1.yPos = p1.yPos + 7;
        }
        if (p1.yPos < 0) {
            p1.yPos += 10;
        } else if (p1.yPos + p1.hoehe > applet.height) {
            p1.yPos -= 10;
        }

        if (upPressed) {
            p2.yPos = p2.yPos - 7;
        }
        if (downPressed) {
            p2.yPos = p2.yPos + 7;
        }
        if (p2.yPos < 0) {
            p2.yPos += 10;
        } else if (p2.yPos + p2.hoehe > applet.height) {
            p2.yPos -= 10;
        }
    }

    private void rand() { // Kollision zwischen Ball und dem Bildschirmrand
        if (ball.xPos + ball.durchmesser >= applet.width) {
            punkteP1++;
            ball.xPos = applet.width/2;
            ball.yPos = applet.height/2;
            ball.xSpeed = -10;
        } else if (ball.yPos + ball.durchmesser/2 >= applet.height) {
            ball.ySpeed *= -1;
        } else if (ball.xPos - ball.durchmesser/2 <= 0) {
            punkteP2++;
            ball.xPos = applet.width/2;
            ball.yPos = applet.height/2;
            ball.xSpeed = 10;
        } else if (ball.yPos - ball.durchmesser/2 <= 0) {
            ball.ySpeed *= -1;
        }
    }

    private void kollision() { // Kollision zwischen Ball und Spieler
        if (ball.xPos - ball.durchmesser/2 >= p1.xPos && ball.xPos - ball.durchmesser/2 <= p1.xPos + p1.breite) {
            if (ball.yPos <= p1.yPos + p1.hoehe && ball.yPos + ball.durchmesser/2 >= p1.yPos) {
                ball.xSpeed *= -1;
                ball.xSpeed += ball.xSpeed/5;
                partikelManager.spawnPartikel((float) ball.xPos / applet.width, (float) ball.yPos / applet.height);
            }
        }
        if (ball.xPos + ball.durchmesser/2 >= p2.xPos && ball.xPos + ball.durchmesser/2 <= p2.xPos + p2.breite) {
            if (ball.yPos <= p2.yPos + p2.hoehe && ball.yPos + ball.durchmesser/2 >= p2.yPos) {
                ball.xSpeed *= -1;
                ball.xSpeed += ball.xSpeed/5;
                partikelManager.spawnPartikel((float) ball.xPos / applet.width, (float) ball.yPos / applet.height);
            }
        }
    }
}
