package herdergames.pacman;

import herdergames.spiel.Spieler;
import herdergames.util.Rechteck;
import herdergames.util.Steuerung;
import processing.core.PConstants;
import processing.core.PImage;

abstract class SpielerGesteuert {
    protected final PacmanSpiel spiel;
    protected final Spieler spieler;
    protected final Steuerung steuerung;
    protected float x = getStartX();
    protected float y = getStartY();

    protected SpielerGesteuert(PacmanSpiel spiel, Spieler spieler) {
        this.spiel = spiel;
        this.spieler = spieler;
        steuerung = new Steuerung(spiel.applet, spieler.id());
    }

    protected abstract float getStartX();

    protected abstract float getStartY();

    protected boolean canMove() {
        return true;
    }

    protected abstract float getGeschwindigkeit();

    protected abstract float getSize();

    protected abstract PImage getImage1(Richtung richtung);

    protected abstract PImage getImage2(Richtung richtung);

    protected abstract int getAnimationSpeed();

    private PImage getImage() {
        Richtung visuelleRichtung = Richtung.vonSteuerungRichtung(steuerung.getZuletztGedrueckt().orElse(Steuerung.Richtung.OBEN));

        if (spiel.applet.frameCount % (getAnimationSpeed() * 2) >= getAnimationSpeed()) {
            return getImage1(visuelleRichtung);
        } else {
            return getImage2(visuelleRichtung);
        }
    }

    void draw() {
        move();

        spiel.applet.imageMode(PConstants.CORNER);
        spiel.applet.image(getImage(), x, y, getSize(), getSize());
    }

    void move() {
        if (!canMove()) {
            return;
        }

        boolean kollisionXVorher = kollidiertMitMauer();
        float xVorher = x;
        x += getGeschwindigkeit() * steuerung.getXRichtung();
        if (!kollisionXVorher && kollidiertMitMauer()) {
            x = xVorher;
        }

        boolean kollisionYVorher = kollidiertMitMauer();
        float yVorher = y;
        y += getGeschwindigkeit() * steuerung.getYRichtung();
        if (!kollisionYVorher && kollidiertMitMauer()) {
            y = yVorher;
        }
    }

    private boolean kollidiertMitMauer() {
        Rechteck hitbox = getHitbox();
        for (Rechteck mauer : PacmanSpiel.mauerPositionen) {
            if (mauer.kollidiertMit(hitbox)) {
                return true;
            }
        }

        return false;
    }

    void keyPressed() {
        steuerung.keyPressed();
    }

    void keyReleased() {
        steuerung.keyReleased();
    }

    Rechteck getHitbox() {
        return new Rechteck(x, y, getSize(), getSize());
    }
}
