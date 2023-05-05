package herdergames.pacman;

import herdergames.spiel.Spiel;
import herdergames.spiel.Spieler;
import herdergames.util.Rechteck;
import herdergames.util.Steuerung;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.util.*;

public final class PacmanSpiel extends Spiel.Mehrspieler {
    public static void init(PApplet applet) {
        spriteSheet = applet.loadImage("pacman/pacman.png");

        welt = spriteSheet.get(208, 225, 229, 252);

        welt.loadPixels();

        Set<Position> punktePositionen = new HashSet<>();
        Set<Position> superPunktePositionen = new HashSet<>();
        int punktFarbe = applet.color(255, 183, 174);
        int superPunktFarbe = applet.color(255, 242, 0);
        for (int x = 0; x < welt.width; x++) {
            for (int y = 0; y < welt.height; y++) {
                int pixelFarbe = welt.pixels[y*welt.width+x];
                if (pixelFarbe != punktFarbe && pixelFarbe != superPunktFarbe) {
                    continue;
                }

                welt.pixels[y*welt.width+x] = applet.color(0);
                welt.pixels[(y+1)*welt.width+x] = applet.color(0);
                welt.pixels[y*welt.width+x+1] = applet.color(0);
                welt.pixels[(y+1)*welt.width+x+1] = applet.color(0);

                if (pixelFarbe == punktFarbe) {
                    punktePositionen.add(new Position(x, y));
                } else {
                    superPunktePositionen.add(new Position(x, y));
                }
            }
        }
        PacmanSpiel.punktePositionen = Collections.unmodifiableSet(punktePositionen);
        PacmanSpiel.superPunktePositionen = Collections.unmodifiableSet(superPunktePositionen);

        welt.updatePixels();

        Set<Rechteck> mauerPositionen = new HashSet<>();
        int mauerFarbe = applet.color(33, 33, 255);
        for (int x = 0; x < welt.width; x++) {
            for (int y = 0; y < welt.height; y++) {
                if (welt.pixels[y*welt.width+x] != mauerFarbe) {
                    continue;
                }

                mauerPositionen.add(new Rechteck(x, y, 1, 1));
            }
        }
        PacmanSpiel.mauerPositionen = Collections.unmodifiableSet(mauerPositionen);
    }

    private static final int SUPER_MODUS_ZEIT = 300;

    private static PImage spriteSheet;
    private static PImage welt;
    private static Set<Position> punktePositionen;
    private static Set<Position> superPunktePositionen;
    private static Set<Rechteck> mauerPositionen;

    private final List<Pacman> pacmans = new ArrayList<>();
    private final List<Spieler.Id> totePacmans = new ArrayList<>();
    private final List<Geist> geister = new ArrayList<>();
    private final List<Punkt> punkte = new ArrayList<>();
    private final List<SuperPunkt> superPunkte = new ArrayList<>();
    private boolean superModus;
    private int superModusVerbleibendeZeit;

    public PacmanSpiel(PApplet applet, Set<Spieler> alleSpieler) {
        super(applet);

        List<Spieler> spielerListe = new ArrayList<>(alleSpieler);
        int anzahlPacmans = 1;
        if (spielerListe.size() == 4) {
            anzahlPacmans = 2;
        }
        for (int i = 0; i < anzahlPacmans; i++) {
            Spieler spieler = spielerListe.remove(applet.choice(spielerListe.size()));
            pacmans.add(new Pacman(this, spieler));
        }
        for (Spieler spieler : spielerListe) {
            geister.add(new Geist(this, spieler));
        }

        for (Position punktPosition : punktePositionen) {
            punkte.add(new Punkt(this, punktPosition.x, punktPosition.y));
        }
        for (Position superPunktPosition : superPunktePositionen) {
            superPunkte.add(new SuperPunkt(this, superPunktPosition.x, superPunktPosition.y));
        }
    }

    private float getScale() {
        return Math.min((float) applet.width / (float) welt.width, (float) applet.height / (float) welt.height);
    }

    private float getWidth() {
        return getScale() * welt.width;
    }

    private float getHeight() {
        return getScale() * welt.height;
    }

    private float getAbstandHorizontal() {
        return ((float) applet.width - getWidth()) / 2;
    }

    private float getAbstandVertikal() {
        return ((float) applet.height - getHeight()) / 2;
    }

    private void tickSuperModus() {
        if (!superModus) {
            return;
        }

        superModusVerbleibendeZeit--;
        if (superModusVerbleibendeZeit <= 0) {
            superModus = false;
        }
    }

    @Override
    public Optional<List<Spieler.Id>> draw() {
        tickSuperModus();

        applet.background(0);

        applet.translate(getAbstandHorizontal(), getAbstandVertikal());
        applet.scale(getScale());

        applet.imageMode(PConstants.CORNER);
        applet.image(welt, 0, 0);

        for (Punkt punkt : punkte) {
            punkt.draw();
        }
        for (SuperPunkt superPunkt : superPunkte) {
            superPunkt.draw();
        }
        for (Pacman pacman : pacmans) {
            pacman.draw();
        }
        for (Geist geist : geister) {
            geist.draw();
        }

        if (punkte.isEmpty()) {
            List<Spieler.Id> rangliste = new ArrayList<>();
            for (Pacman pacman : pacmans) {
                rangliste.add(pacman.spieler.id());
            }
            rangliste.addAll(totePacmans);
            for (Geist geist : geister) {
                rangliste.add(geist.spieler.id());
            }
            return Optional.of(rangliste);
        }

        if (pacmans.isEmpty()) {
            List<Spieler.Id> rangliste = new ArrayList<>();
            for (Geist geist : geister) {
                rangliste.add(geist.spieler.id());
            }
            rangliste.addAll(totePacmans);
            return Optional.of(rangliste);
        }

        return Optional.empty();
    }

    @Override
    public void keyPressed() {
        for (Pacman pacman : pacmans) {
            pacman.keyPressed();
        }

        for (Geist geist : geister) {
            geist.keyPressed();
        }
    }

    @Override
    public void keyReleased() {
        for (Pacman pacman : pacmans) {
            pacman.keyReleased();
        }

        for (Geist geist : geister) {
            geist.keyReleased();
        }
    }

    private static abstract class SpielerGesteuert {
        final PacmanSpiel spiel;
        final Spieler spieler;
        final Steuerung steuerung;
        float x = getStartX();
        float y = getStartY();

        SpielerGesteuert(PacmanSpiel spiel, Spieler spieler) {
            this.spiel = spiel;
            this.spieler = spieler;
            steuerung = new Steuerung(spiel.applet, spieler.id());
        }

        abstract float getStartX();

        abstract float getStartY();

        boolean canMove() {
            return true;
        }

        abstract float getGeschwindigkeit();

        abstract float getSize();

        abstract PImage getImage1(Richtung richtung);

        abstract PImage getImage2(Richtung richtung);

        abstract int getAnimationSpeed();

        PImage getImage() {
            Richtung visuelleRichtung = Richtung.vonSteuerungRichtung(steuerung.getZuletztGedrueckt().orElse(Steuerung.Richtung.OBEN));

            if (spiel.applet.frameCount % (getAnimationSpeed()*2) >= getAnimationSpeed()) {
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

        boolean kollidiertMitMauer() {
            Rechteck hitbox = getHitbox();
            for (Rechteck mauer : mauerPositionen) {
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

    private static final class Pacman extends SpielerGesteuert {
        private static final float GESCHWINDIGKEIT = 1f;
        private static final int ANIMATION_SPEED = 20;
        private static final int SIZE = 14;
        private static final int START_X = 108;
        private static final int START_Y = 136;

        private Pacman(PacmanSpiel spiel, Spieler spieler) {
            super(spiel, spieler);
        }

        @Override
        float getStartX() {
            return START_X;
        }

        @Override
        float getStartY() {
            return START_Y;
        }

        @Override
        float getGeschwindigkeit() {
            return GESCHWINDIGKEIT;
        }

        @Override
        float getSize() {
            return SIZE;
        }

        @Override
        PImage getImage1(Richtung richtung) {
            return Grafik.fuerRichtung1(richtung).image;
        }

        @Override
        PImage getImage2(Richtung richtung) {
            return Grafik.fuerRichtung2(richtung).image;
        }

        @Override
        int getAnimationSpeed() {
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
                spiel.superModusVerbleibendeZeit = SUPER_MODUS_ZEIT;
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
                switch (richtung) {
                    case RECHTS:
                        return RECHTS_1;
                    case LINKS:
                        return LINKS_1;
                    case OBEN:
                        return OBEN_1;
                    case UNTEN:
                        return UNTEN_1;
                    default:
                        throw new IllegalArgumentException();
                }
            }

            private static Grafik fuerRichtung2(Richtung richtung) {
                switch (richtung) {
                    case RECHTS:
                        return RECHTS_2;
                    case LINKS:
                        return LINKS_2;
                    case OBEN:
                        return OBEN_2;
                    case UNTEN:
                        return UNTEN_2;
                    default:
                        throw new IllegalArgumentException();
                }
            }

            private static final int SPRITE_SHEET_X_START = 14;
            private static final int SPRITE_SHEET_Y_START = 51;
            private static final int SIZE = 16;

            private final PImage image;

            Grafik(int spriteSheetXIndex, int spriteSheetYIndex) {
                image = spriteSheet.get(
                        SPRITE_SHEET_X_START+spriteSheetXIndex*SIZE,
                        SPRITE_SHEET_Y_START+spriteSheetYIndex*SIZE,
                        Pacman.SIZE,
                        Pacman.SIZE
                );
            }
        }
    }

    private static final class Punkt {
        private static final int SIZE = 2;

        private final PacmanSpiel spiel;
        private final int x;
        private final int y;

        private Punkt(PacmanSpiel spiel, int x, int y) {
            this.spiel = spiel;
            this.x = x;
            this.y = y;
        }

        private void draw() {
            spiel.applet.rectMode(PConstants.CORNER);
            spiel.applet.noStroke();
            spiel.applet.fill(spiel.applet.color(255, 183, 174));
            spiel.applet.rect(x, y, SIZE, SIZE);
        }

        private Rechteck getHitbox() {
            return new Rechteck(x, y, SIZE, SIZE);
        }
    }

    private static final class SuperPunkt {
        private static final int SIZE = 2;

        private final PacmanSpiel spiel;
        private final int x;
        private final int y;

        private SuperPunkt(PacmanSpiel spiel, int x, int y) {
            this.spiel = spiel;
            this.x = x;
            this.y = y;
        }

        private void draw() {
            spiel.applet.rectMode(PConstants.CORNER);
            spiel.applet.noStroke();
            spiel.applet.fill(spiel.applet.color(255, 0, 0));
            spiel.applet.rect(x, y, SIZE, SIZE);
        }

        private Rechteck getHitbox() {
            return new Rechteck(x, y, SIZE, SIZE);
        }
    }

    private static final class Geist extends SpielerGesteuert {
        private static final int ANIMATION_SPEED = 30;
        private static final float GESCHWINDIGKEIT = 1f;
        private static final int SIZE = 14;
        private static final int START_X = 108;
        private static final int START_Y = 116;

        private final Name name;

        private Geist(PacmanSpiel spiel, Spieler spieler) {
            super(spiel, spieler);
            name = getName();
        }

        @Override
        float getStartX() {
            return START_X;
        }

        @Override
        float getStartY() {
            return START_Y;
        }

        @Override
        boolean canMove() {
            return !spiel.superModus;
        }

        private Name getName() {
            switch (spieler.id()) {
                case SPIELER_1:
                    return Name.INKY;
                case SPIELER_2:
                    return Name.BLINKY;
                case SPIELER_3:
                    return Name.PINKY;
                case SPIELER_4:
                    return Name.CLYDE;
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        float getGeschwindigkeit() {
            return GESCHWINDIGKEIT;
        }

        @Override
        float getSize() {
            return SIZE;
        }

        @Override
        PImage getImage1(Richtung richtung) {
            return name.grafiken.get(Grafik.fuerRichtung1(richtung));
        }

        @Override
        PImage getImage2(Richtung richtung) {
            return name.grafiken.get(Grafik.fuerRichtung2(richtung));
        }

        @Override
        int getAnimationSpeed() {
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
                switch (richtung) {
                    case RECHTS:
                        return RECHTS_1;
                    case LINKS:
                        return LINKS_1;
                    case OBEN:
                        return OBEN_1;
                    case UNTEN:
                        return UNTEN_1;
                    default:
                        throw new IllegalArgumentException();
                }
            }

            private static Grafik fuerRichtung2(Richtung richtung) {
                switch (richtung) {
                    case RECHTS:
                        return RECHTS_2;
                    case LINKS:
                        return LINKS_2;
                    case OBEN:
                        return OBEN_2;
                    case UNTEN:
                        return UNTEN_2;
                    default:
                        throw new IllegalArgumentException();
                }
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
                    grafiken.put(grafik, spriteSheet.get(
                            spriteSheetX + grafik.getXOffset()+1,
                            spriteSheetY+1,
                            Geist.SIZE,
                            Geist.SIZE
                    ));
                }
                this.grafiken = Collections.unmodifiableMap(grafiken);
            }
        }
    }

    private enum Richtung {
        LINKS,
        RECHTS,
        OBEN,
        UNTEN;

        private static Richtung vonSteuerungRichtung(Steuerung.Richtung richtung) {
            switch (richtung) {
                case LINKS:
                    return LINKS;
                case RECHTS:
                    return RECHTS;
                case OBEN:
                    return OBEN;
                case UNTEN:
                    return UNTEN;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    private static final class Position {
        private final int x;
        private final int y;

        private Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return x == position.x && y == position.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
