package herdergames.pacman;

import herdergames.spiel.Spieler;
import herdergames.util.Rechteck;
import processing.core.PImage;

import java.util.Iterator;

final class Pacman extends SpielerGesteuert {
    private static final float GESCHWINDIGKEIT = 1f;
    private static final int ANIMATION_SPEED = 20;
    private static final int SIZE = 14;
    private static final int START_X = 108;
    private static final int START_Y = 136;

    Pacman(PacmanSpiel spiel, Spieler spieler) {
        super(spiel, spieler);
    }

    @Override
    protected float getStartX() {
        return START_X;
    }

    @Override
    protected float getStartY() {
        return START_Y;
    }

    @Override
    protected float getGeschwindigkeit() {
        return GESCHWINDIGKEIT;
    }

    @Override
    protected float getSize() {
        return SIZE;
    }

    @Override
    protected PImage getImage1(Richtung richtung) {
        return Grafik.fuerRichtung1(richtung).image;
    }

    @Override
    protected PImage getImage2(Richtung richtung) {
        return Grafik.fuerRichtung2(richtung).image;
    }

    @Override
    protected int getAnimationSpeed() {
        return ANIMATION_SPEED;
    }

    @Override
    void draw() {
        super.draw();
        punkteEssen();
        superPunkteEssen();
    }

    private void punkteEssen() {
        Rechteck hitbox = getHitbox();
        Iterator<Punkt> punkteIterator = spiel.punkte.iterator();
        while (punkteIterator.hasNext()) {
            Punkt punkt = punkteIterator.next();
            if (!punkt.getHitbox().kollidiertMit(hitbox)) {
                continue;
            }
            punkteIterator.remove();
        }
    }

    private void superPunkteEssen() {
        Rechteck hitbox = getHitbox();
        Iterator<SuperPunkt> superPunkteIterator = spiel.superPunkte.iterator();
        while (superPunkteIterator.hasNext()) {
            SuperPunkt punkt = superPunkteIterator.next();
            if (!punkt.getHitbox().kollidiertMit(hitbox)) {
                continue;
            }
            superPunkteIterator.remove();
            spiel.superModus = true;
            spiel.superModusVerbleibendeZeit = PacmanSpiel.SUPER_MODUS_ZEIT;
        }
    }

    private enum Grafik {
        RECHTS_1(0, 0),
        RECHTS_2(1, 0),
        LINKS_1(0, 1),
        LINKS_2(1, 1),
        OBEN_1(0, 2),
        OBEN_2(1, 2),
        UNTEN_1(0, 3),
        UNTEN_2(1, 3);

        private static Grafik fuerRichtung1(Richtung richtung) {
            return switch (richtung) {
                case RECHTS -> RECHTS_1;
                case LINKS -> LINKS_1;
                case OBEN -> OBEN_1;
                case UNTEN -> UNTEN_1;
            };
        }

        private static Grafik fuerRichtung2(Richtung richtung) {
            return switch (richtung) {
                case RECHTS -> RECHTS_2;
                case LINKS -> LINKS_2;
                case OBEN -> OBEN_2;
                case UNTEN -> UNTEN_2;
            };
        }

        private static final int SPRITE_SHEET_X_START = 14;
        private static final int SPRITE_SHEET_Y_START = 51;
        private static final int SIZE = 16;

        private final PImage image;

        Grafik(int spriteSheetXIndex, int spriteSheetYIndex) {
            image = PacmanSpiel.spriteSheet.get(
                    SPRITE_SHEET_X_START + spriteSheetXIndex * SIZE,
                    SPRITE_SHEET_Y_START + spriteSheetYIndex * SIZE,
                    Pacman.SIZE,
                    Pacman.SIZE
            );
        }
    }
}
