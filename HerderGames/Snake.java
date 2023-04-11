import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.util.*;
import java.util.List;

final class Snake extends Spiel.Mehrspieler {
    static final Spiel.Mehrspieler.Factory FACTORY = new Factory() {
        @Override
        Mehrspieler neuesSpiel(PApplet applet, Set<Spieler> spieler) {
            return new Snake(applet, spieler);
        }
    };

    private static PImage apfel;

    private static PImage kopfOben;
    private static PImage kopfUnten;
    private static PImage kopfLinks;
    private static PImage kopfRechts;

    private static PImage schlangeHorizontal;
    private static PImage schlangeVertikal;
    private static PImage schlangeObenLinks;
    private static PImage schlangeObenRechts;
    private static PImage schlangeUntenLinks;
    private static PImage schlangeUntenRechts;

    private static PImage endeOben;
    private static PImage endeUnten;
    private static PImage endeLinks;
    private static PImage endeRechts;

    static void init(PApplet applet) {
        apfel = applet.loadImage("snake/apfel.png");

        kopfOben = applet.loadImage("snake/kopf_oben.png");
        kopfUnten = applet.loadImage("snake/kopf_unten.png");
        kopfLinks = applet.loadImage("snake/kopf_links.png");
        kopfRechts = applet.loadImage("snake/kopf_rechts.png");

        schlangeHorizontal = applet.loadImage("snake/schlange_horizontal.png");
        schlangeVertikal = applet.loadImage("snake/schlange_vertikal.png");
        schlangeObenLinks = applet.loadImage("snake/schlange_oben_links.png");
        schlangeObenRechts = applet.loadImage("snake/schlange_oben_rechts.png");
        schlangeUntenLinks = applet.loadImage("snake/schlange_unten_links.png");
        schlangeUntenRechts = applet.loadImage("snake/schlange_unten_rechts.png");

        endeOben = applet.loadImage("snake/ende_oben.png");
        endeUnten = applet.loadImage("snake/ende_unten.png");
        endeLinks = applet.loadImage("snake/ende_links.png");
        endeRechts = applet.loadImage("snake/ende_rechts.png");
    }

    private static final int SIZE = 30;
    private static final int APFEL_SPAWN_DELAY = 150;

    private final Set<SchlangeKopf> schlangen = new HashSet<>();
    private final Set<Apfel> aepfel = new HashSet<>();
    private int nextApfel = APFEL_SPAWN_DELAY;
    private final List<Spieler.Id> rangliste = new ArrayList<>();

    private Snake(PApplet applet, Set<Spieler> alleSpieler) {
        super(applet);

        for (Spieler spieler : alleSpieler) {
            schlangen.add(new SchlangeKopf(spieler));
        }
    }

    private void spawnApfel() {
        if (nextApfel > 0) {
            nextApfel--;
            return;
        }

        aepfel.add(new Apfel());

        nextApfel = APFEL_SPAWN_DELAY;
    }

    private void drawSpielbrettBackground() {
        float spielBrettSize = getSpielBrettSize();
        float abstandHorizontal = getAbstandHorizontal();
        float abstandVertikal = getAbstandVertikal();

        applet.rectMode(PConstants.CORNER);
        applet.fill(applet.color(0, 204, 0));
        applet.noStroke();
        applet.rect(abstandHorizontal, abstandVertikal, spielBrettSize, spielBrettSize);
    }

    @Override
    Optional<List<Spieler.Id>> draw() {
        applet.background(0);
        drawSpielbrettBackground();

        spawnApfel();
        for (Apfel apfel : aepfel) {
            apfel.draw();
        }

        Iterator<SchlangeKopf> schlangenIterator = schlangen.iterator();
        while (schlangenIterator.hasNext()) {
            SchlangeKopf schlange = schlangenIterator.next();
            if (schlange.istTot()) {
                schlangenIterator.remove();
                rangliste.add(0, schlange.spieler.id);
                continue;
            }
            schlange.draw();
        }

        if (schlangen.isEmpty()) {
            return Optional.of(rangliste);
        }

        return Optional.empty();
    }

    @Override
    void keyPressed() {
        for (SchlangeKopf schlangeKopf : schlangen) {
            schlangeKopf.keyPressed();
        }
    }

    private float getSpielBrettSize() {
        return Math.min(applet.width, applet.height);
    }

    private float getAbstandHorizontal() {
        return (applet.width - getSpielBrettSize()) / 2;
    }

    private float getAbstandVertikal() {
        return (applet.height - getSpielBrettSize()) / 2;
    }

    private float getFeldSize() {
        return getSpielBrettSize() / SIZE;
    }

    private enum Richtung {
        Links(-1, 0),
        Rechts(1, 0),
        Oben(0, -1),
        Unten(0, 1);

        private static Richtung mitVerschiebung(int x, int y) {
            for (Richtung value : values()) {
                if (value.xVerschiebung == x && value.yVerschiebung == y) {
                    return value;
                }
            }
            new Exception().printStackTrace();
            throw new IllegalArgumentException();
        }

        private final int xVerschiebung;
        private final int yVerschiebung;

        Richtung(int xVerschiebung, int yVerschiebung) {
            this.xVerschiebung = xVerschiebung;
            this.yVerschiebung = yVerschiebung;
        }

        private Richtung getGegenueber() {
            return mitVerschiebung(-xVerschiebung, -yVerschiebung);
        }
    }

    private final class Apfel {
        private int x;
        private int y;

        private Apfel() {
            findPosition();
        }

        private void findPosition() {
            findPosition:
            while (true) {
                x = (int) applet.random(SIZE);
                y = (int) applet.random(SIZE);

                for (Apfel apfel : aepfel) {
                    if (apfel.x == x && apfel.y == y) {
                        continue findPosition;
                    }
                }

                for (SchlangeKopf schlange : schlangen) {
                    if (schlange.checkKollision(x, y)) {
                        continue findPosition;
                    }
                }

                break;
            }
        }

        private void draw() {
            float abstandHorizontal = getAbstandHorizontal();
            float abstandVertikal = getAbstandVertikal();
            float feldSize = getFeldSize();

            float screenX = abstandHorizontal + x * feldSize;
            float screenY = abstandVertikal + y * feldSize;

            applet.imageMode(PConstants.CORNER);
            applet.image(apfel, screenX, screenY, feldSize, feldSize);
        }
    }

    private abstract static class SchlangeTeil {
        int x;
        int y;

        abstract void draw();

        abstract void moveTo(int x, int y);

        abstract boolean checkKollision(int x, int y);
    }

    private final class SchlangeKopf extends SchlangeTeil {
        private static final int BEWEGUNG_DELAY = 15;

        private final Spieler spieler;

        private SchlangeTeil dahinter;

        private Richtung bewegungsRichtung = Richtung.Unten;
        private int naechsteBewegungDelay = BEWEGUNG_DELAY;
        private boolean beiNaechsterBewegungWachsen = false;

        private SchlangeKopf(Spieler spieler) {
            this.spieler = spieler;
            x = getStartX();

            SchlangeEnde ende = new SchlangeEnde(x, -2);

            SchlangeKoerper koerper = new SchlangeKoerper(x, -1, this, ende);
            ende.davor = koerper;

            dahinter = koerper;
        }

        private int getStartX() {
            switch (spieler.id) {
                case SPIELER_1:
                    return 0;
                case SPIELER_2:
                    return SIZE / 3;
                case SPIELER_3:
                    return (int) (SIZE * (2f/3f));
                case SPIELER_4:
                    return SIZE-1;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private boolean istWegVomBildschirm() {
            return x < 0 || x >= SIZE || y < 0 || y >= SIZE;
        }

        private boolean kollidiertMitAndererSchlange() {
            for (SchlangeKopf schlange : schlangen) {
                if (schlange == this) {
                    continue;
                }

                if (schlange.checkKollision(x, y)) {
                    return true;
                }
            }

            return false;
        }

        private boolean kollidiertMitSichSelbst() {
            return dahinter.checkKollision(x, y);
        }

        private boolean istTot() {
            return istWegVomBildschirm() || kollidiertMitAndererSchlange() || kollidiertMitSichSelbst();
        }

        @Override
        boolean checkKollision(int x, int y) {
            return (this.x == x && this.y == y) || dahinter.checkKollision(x, y);
        }

        @Override
        void moveTo(int x, int y) {
            throw new UnsupportedOperationException();
        }

        private void move() {
            if (naechsteBewegungDelay > 0) {
                naechsteBewegungDelay--;
                return;
            }
            naechsteBewegungDelay = BEWEGUNG_DELAY;

            int oldX = x;
            int oldY = y;
            x += bewegungsRichtung.xVerschiebung;
            y += bewegungsRichtung.yVerschiebung;

            if (beiNaechsterBewegungWachsen) {
                SchlangeKoerper neuerKoerper = new SchlangeKoerper(oldX, oldY, this, dahinter);
                if (dahinter instanceof SchlangeKoerper) {
                    ((SchlangeKoerper) dahinter).davor = neuerKoerper;
                } else if (dahinter instanceof SchlangeEnde) {
                    ((SchlangeEnde) dahinter).davor = neuerKoerper;
                }
                dahinter = neuerKoerper;
                beiNaechsterBewegungWachsen = false;
            } else {
                dahinter.moveTo(oldX, oldY);
            }
        }

        private void apfelEssen() {
            Iterator<Apfel> apfelIterator = aepfel.iterator();
            while (apfelIterator.hasNext()) {
                Apfel apfel = apfelIterator.next();
                if (checkKollision(apfel.x, apfel.y)) {
                    beiNaechsterBewegungWachsen = true;
                    apfelIterator.remove();
                }
            }
        }

        private Richtung getBlickRichtung() {
            int xOffset = x - dahinter.x;
            int yOffset = y - dahinter.y;
            return Richtung.mitVerschiebung(xOffset, yOffset);
        }

        private PImage getImage() {
            switch (getBlickRichtung()) {
                case Unten:
                    return kopfUnten;
                case Oben:
                    return kopfOben;
                case Links:
                    return kopfLinks;
                case Rechts:
                    return kopfRechts;
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        void draw() {
            move();

            apfelEssen();

            float abstandHorizontal = getAbstandHorizontal();
            float abstandVertikal = getAbstandVertikal();
            float feldSize = getFeldSize();

            float screenX = abstandHorizontal + x * feldSize;
            float screenY = abstandVertikal + y * feldSize;

            applet.imageMode(PConstants.CORNER);
            applet.image(getImage(), screenX, screenY, feldSize, feldSize);

            dahinter.draw();
        }

        private Optional<Richtung> getNeueBewegungsRichtungInput() {
            switch (spieler.id) {
                case SPIELER_1:
                    switch (applet.key) {
                        case 'w':
                            return Optional.of(Richtung.Oben);
                        case 'a':
                            return Optional.of(Richtung.Links);
                        case 's':
                            return Optional.of(Richtung.Unten);
                        case 'd':
                            return Optional.of(Richtung.Rechts);
                        default:
                            return Optional.empty();
                    }
                case SPIELER_2:
                    switch (applet.key) {
                        case 't':
                            return Optional.of(Richtung.Oben);
                        case 'f':
                            return Optional.of(Richtung.Links);
                        case 'g':
                            return Optional.of(Richtung.Unten);
                        case 'h':
                            return Optional.of(Richtung.Rechts);
                        default:
                            return Optional.empty();
                    }
                case SPIELER_3:
                    switch (applet.key) {
                        case 'i':
                            return Optional.of(Richtung.Oben);
                        case 'j':
                            return Optional.of(Richtung.Links);
                        case 'k':
                            return Optional.of(Richtung.Unten);
                        case 'l':
                            return Optional.of(Richtung.Rechts);
                        default:
                            return Optional.empty();
                    }
                case SPIELER_4:
                    if (applet.key != PConstants.CODED) {
                        return Optional.empty();
                    }

                    switch (applet.keyCode) {
                        case PConstants.UP:
                            return Optional.of(Richtung.Oben);
                        case PConstants.LEFT:
                            return Optional.of(Richtung.Links);
                        case PConstants.DOWN:
                            return Optional.of(Richtung.Unten);
                        case PConstants.RIGHT:
                            return Optional.of(Richtung.Rechts);
                        default:
                            return Optional.empty();
                    }
                default:
                    throw new IllegalArgumentException();
            }
        }

        private boolean isValidNeueBewegungsRichtung(Richtung neueRichtung) {
            return neueRichtung != getBlickRichtung().getGegenueber();
        }

        private void keyPressed() {
            getNeueBewegungsRichtungInput().ifPresent(neueRichtung -> {
                if (!isValidNeueBewegungsRichtung(neueRichtung)) {
                    return;
                }

                this.bewegungsRichtung = neueRichtung;
            });
        }
    }

    private final class SchlangeKoerper extends SchlangeTeil {
        private SchlangeTeil davor;
        private SchlangeTeil dahinter;

        private SchlangeKoerper(int x, int y, SchlangeTeil davor, SchlangeTeil dahinter) {
            this.x = x;
            this.y = y;
            this.davor = davor;
            this.dahinter = dahinter;
        }

        @Override
        boolean checkKollision(int x, int y) {
            return (this.x == x && this.y == y) || dahinter.checkKollision(x, y);
        }

        @Override
        void moveTo(int x, int y) {
            int oldX = this.x;
            int oldY = this.y;

            this.x = x;
            this.y = y;

            dahinter.moveTo(oldX, oldY);
        }

        private Richtung getRichtungZuDavor() {
            int xOffset = davor.x - x;
            int yOffset = davor.y - y;
            return Richtung.mitVerschiebung(xOffset, yOffset);
        }

        private Richtung getRichtungZuDahinter() {
            int xOffset = dahinter.x - x;
            int yOffset = dahinter.y - y;
            return Richtung.mitVerschiebung(xOffset, yOffset);
        }

        private PImage getImage() {
            Map<Set<Richtung>, PImage> images = Map.of(
                    Set.of(Richtung.Links, Richtung.Rechts), schlangeHorizontal,
                    Set.of(Richtung.Oben, Richtung.Unten), schlangeVertikal,
                    Set.of(Richtung.Oben, Richtung.Links), schlangeObenLinks,
                    Set.of(Richtung.Oben, Richtung.Rechts), schlangeObenRechts,
                    Set.of(Richtung.Unten, Richtung.Links), schlangeUntenLinks,
                    Set.of(Richtung.Unten, Richtung.Rechts), schlangeUntenRechts
            );

            return images.get(Set.of(getRichtungZuDavor(), getRichtungZuDahinter()));
        }

        @Override
        void draw() {
            dahinter.draw();

            float abstandHorizontal = getAbstandHorizontal();
            float abstandVertikal = getAbstandVertikal();
            float feldSize = getFeldSize();

            float screenX = abstandHorizontal + x * feldSize;
            float screenY = abstandVertikal + y * feldSize;

            applet.imageMode(PConstants.CORNER);
            applet.image(getImage(), screenX, screenY, feldSize, feldSize);
        }
    }

    private final class SchlangeEnde extends SchlangeTeil {
        private SchlangeTeil davor;

        private SchlangeEnde(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        boolean checkKollision(int x, int y) {
            return this.x == x && this.y == y;
        }

        @Override
        void moveTo(int x, int y) {
            this.x = x;
            this.y = y;
        }

        private Richtung getRichtungVonDavor() {
            int xOffset = x - davor.x;
            int yOffset = y - davor.y;
            return Richtung.mitVerschiebung(xOffset, yOffset);
        }

        private PImage getImage() {
            switch (getRichtungVonDavor()) {
                case Unten:
                    return endeUnten;
                case Oben:
                    return endeOben;
                case Links:
                    return endeLinks;
                case Rechts:
                    return endeRechts;
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        void draw() {
            float abstandHorizontal = getAbstandHorizontal();
            float abstandVertikal = getAbstandVertikal();
            float feldSize = getFeldSize();

            float screenX = abstandHorizontal + x * feldSize;
            float screenY = abstandVertikal + y * feldSize;

            applet.imageMode(PConstants.CORNER);
            applet.image(getImage(), screenX, screenY, feldSize, feldSize);
        }
    }
}
