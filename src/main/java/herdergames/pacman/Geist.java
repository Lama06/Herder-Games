package herdergames.pacman;

import herdergames.spiel.Spieler;
import herdergames.util.Rechteck;
import processing.core.PImage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

final class Geist extends SpielerGesteuert {
    private static final int ANIMATION_SPEED = 30;
    private static final float GESCHWINDIGKEIT = 1f;
    private static final int SIZE = 14;
    private static final int START_X = 108;
    private static final int START_Y = 116;

    private final Name name;

    Geist(PacmanSpiel spiel, Spieler spieler) {
        super(spiel, spieler);
        name = getName();
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
    protected boolean canMove() {
        return !spiel.superModus;
    }

    private Name getName() {
        return switch (spieler.id()) {
            case SPIELER_1 -> Name.INKY;
            case SPIELER_2 -> Name.BLINKY;
            case SPIELER_3 -> Name.PINKY;
            case SPIELER_4 -> Name.CLYDE;
        };
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
        return name.grafiken.get(Grafik.fuerRichtung1(richtung));
    }

    @Override
    protected PImage getImage2(Richtung richtung) {
        return name.grafiken.get(Grafik.fuerRichtung2(richtung));
    }

    @Override
    protected int getAnimationSpeed() {
        return ANIMATION_SPEED;
    }

    @Override
    void draw() {
        super.draw();
        pacmanFressen();
    }

    private void pacmanFressen() {
        Rechteck hitbox = getHitbox();
        Iterator<Pacman> pacmanIterator = spiel.pacmans.iterator();
        while (pacmanIterator.hasNext()) {
            Pacman pacman = pacmanIterator.next();
            if (pacman.getHitbox().kollidiertMit(hitbox)) {
                pacmanIterator.remove();
                spiel.totePacmans.add(pacman.spieler.id());
            }
        }
    }

    private enum Grafik {
        RECHTS_1(0),
        RECHTS_2(1),
        LINKS_1(2),
        LINKS_2(3),
        OBEN_1(4),
        OBEN_2(5),
        UNTEN_1(6),
        UNTEN_2(7);

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

        private static final int SIZE = 16;

        private final int spriteSheetIndex;

        Grafik(int spriteSheetIndex) {
            this.spriteSheetIndex = spriteSheetIndex;
        }

        private int getXOffset() {
            return spriteSheetIndex * SIZE;
        }
    }

    private enum Name {
        INKY(14, 129),
        BLINKY(14, 180),
        PINKY(14, 231),
        CLYDE(14, 282);

        private final Map<Grafik, PImage> grafiken;

        Name(int spriteSheetX, int spriteSheetY) {
            Map<Grafik, PImage> grafiken = new HashMap<>();
            for (Grafik grafik : Grafik.values()) {
                grafiken.put(grafik, PacmanSpiel.spriteSheet.get(
                        spriteSheetX + grafik.getXOffset() + 1,
                        spriteSheetY + 1,
                        Geist.SIZE,
                        Geist.SIZE
                ));
            }
            this.grafiken = Collections.unmodifiableMap(grafiken);
        }
    }
}
