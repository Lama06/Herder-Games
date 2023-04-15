import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.util.*;

final class PacmanSpiel extends Spiel.Mehrspieler {
    static void init(PApplet applet) {
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

    PacmanSpiel(PApplet applet, Set<Spieler> alleSpieler) {
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
    Optional<List<Spieler.Id>> draw() {
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
                rangliste.add(pacman.spieler.id);
            }
            rangliste.addAll(totePacmans);
            for (Geist geist : geister) {
                rangliste.add(geist.spieler.id);
            }
            return Optional.of(rangliste);
        }

        if (pacmans.isEmpty()) {
            List<Spieler.Id> rangliste = new ArrayList<>();
            for (Geist geist : geister) {
                rangliste.add(geist.spieler.id);
            }
            rangliste.addAll(totePacmans);
            return Optional.of(rangliste);
        }

        return Optional.empty();
    }

    @Override
    void keyPressed() {
        for (Pacman pacman : pacmans) {
            pacman.keyPressed();
        }

        for (Geist geist : geister) {
            geist.keyPressed();
        }
    }

    @Override
    void keyReleased() {
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
            steuerung = Steuerung.fuerSpieler(spiel.applet, spieler.id);
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
            if (spiel.applet.frameCount % (getAnimationSpeed()*2) >= getAnimationSpeed()) {
                return getImage1(steuerung.visuelleRichtung);
            } else {
                return getImage2(steuerung.visuelleRichtung);
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
            switch (spieler.id) {
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
                    spiel.totePacmans.add(pacman.spieler.id);
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

    private abstract static class Steuerung {
        private static Steuerung fuerSpieler(PApplet applet, Spieler.Id spieler) {
            switch (spieler) {
                case SPIELER_1:
                    return new PfeilTastenSteuerung(applet);
                case SPIELER_2:
                    return new TastenSteuerung(applet, 'a', 'd', 'w', 's');
                case SPIELER_3:
                    return new TastenSteuerung(applet, 'f', 'h', 't', 'g');
                case SPIELER_4:
                    return new TastenSteuerung(applet, 'j', 'l', 'i', 'k');
                default:
                    throw new IllegalArgumentException();
            }
        }

        final PApplet applet;
        final Set<Richtung> gedruekt = new HashSet<>();
        Richtung visuelleRichtung;

        Steuerung(PApplet applet) {
            this.applet = applet;
            visuelleRichtung = Richtung.OBEN;
        }

        abstract Optional<Richtung> getGedrueckteRichtung();

        void keyPressed() {
            Optional<Richtung> richtung = getGedrueckteRichtung();
            if (richtung.isEmpty()) {
                return;
            }
            gedruekt.add(richtung.get());
            this.visuelleRichtung = richtung.get();
        }

        void keyReleased() {
            Optional<Richtung> richtung = getGedrueckteRichtung();
            if (richtung.isEmpty()) {
                return;
            }
            gedruekt.remove(richtung.get());
            if (richtung.get() == visuelleRichtung && !gedruekt.isEmpty()) {
                visuelleRichtung = gedruekt.stream().findFirst().get();
            }
        }

        int getXRichtung() {
            int result = 0;
            for (Richtung richtung : gedruekt) {
                result += richtung.x;
            }
            return result;
        }

        int getYRichtung() {
            int result = 0;
            for (Richtung richtung : gedruekt) {
                result += richtung.y;
            }
            return result;
        }
    }

    private static final class TastenSteuerung extends Steuerung {
        private final char links;
        private final char rechts;
        private final char oben;
        private final char unten;

        private TastenSteuerung(PApplet applet, char links, char rechts, char oben, char unten) {
            super(applet);
            this.links = links;
            this.rechts = rechts;
            this.oben = oben;
            this.unten = unten;
        }

        @Override
        Optional<Richtung> getGedrueckteRichtung() {
            if (applet.key == links) {
                return Optional.of(Richtung.LINKS);
            }
            if (applet.key == rechts) {
                return Optional.of(Richtung.RECHTS);
            }
            if (applet.key == oben) {
                return Optional.of(Richtung.OBEN);
            }
            if (applet.key == unten) {
                return Optional.of(Richtung.UNTEN);
            }
            return Optional.empty();
        }
    }

    private static final class PfeilTastenSteuerung extends Steuerung {
        private PfeilTastenSteuerung(PApplet applet) {
            super(applet);
        }

        @Override
        Optional<Richtung> getGedrueckteRichtung() {
            if (applet.key != PConstants.CODED) {
                return Optional.empty();
            }

            if (applet.keyCode == PConstants.LEFT) {
                return Optional.of(Richtung.LINKS);
            }
            if (applet.keyCode == PConstants.RIGHT) {
                return Optional.of(Richtung.RECHTS);
            }
            if (applet.keyCode == PConstants.UP) {
                return Optional.of(Richtung.OBEN);
            }
            if (applet.keyCode == PConstants.DOWN) {
                return Optional.of(Richtung.UNTEN);
            }

            return Optional.empty();
        }
    }

    private enum Richtung {
        LINKS(-1, 0),
        RECHTS(1, 0),
        OBEN(0, -1),
        UNTEN(0, 1);

        private final int x;
        private final int y;

        Richtung(int x, int y) {
            this.x = x;
            this.y = y;
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

    private static final class Rechteck {
        private final float x;
        private final float y;
        private final float breite;
        private final float hoehe;

        private Rechteck(float x, float y, float breite, float hoehe) {
            this.x = x;
            this.y = y;
            this.breite = breite;
            this.hoehe = hoehe;
        }

        private boolean kollidiertMit(Rechteck anderes) {
            float thisMinX = x;
            float thisMaxX = x + breite;
            float anderesMinX = anderes.x;
            float anderesMaxX = anderes.x + anderes.breite;

            float thisMinY = y;
            float thisMaxY = y + hoehe;
            float anderesMinY = anderes.y;
            float anderesMaxY = anderes.y + anderes.hoehe;

            if (thisMaxX < anderesMinX || thisMinX > anderesMaxX) {
                return false;
            }

            if (thisMaxY < anderesMinY || thisMinY > anderesMaxY) {
                return false;
            }

            return true;
        }
    }
}
