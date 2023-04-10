import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.util.*;

final class TitleScreen {
    private static final Video LOOP_VIDEO = new Video("titlescreen/loop", 504);

    private static final SpielUebergang UEBERGANG_1 = new SpielUebergang(new Video("titlescreen/uebergaenge/1", 351), LOOP_VIDEO.frames-1);

    private static final List<SpielDaten> SPIELE = List.of(
            new SpielDaten(
                    "Dame",
                    Dame.SPIELER_GEGEN_SPIELER_FACTORY,
                    UEBERGANG_1
            ),
            new SpielDaten(
                    "Dame AI",
                    Dame.SPIELER_GEGEN_AI_FACTORY,
                    UEBERGANG_1
            ),

            new SpielDaten(
                    "Vier Gewinnt",
                    VierGewinnt.SPIELER_GEGEN_SPIELER_FACTORY,
                    UEBERGANG_1
            ),
            new SpielDaten(
                    "Vier Gewinnt AI",
                    VierGewinnt.SPIELER_GEGEN_AI_FACTORY,
                    UEBERGANG_1
            ),

            new SpielDaten(
                    "Schach",
                    Schach.SPIELER_GEGEN_SPIELER_FACTORY,
                    UEBERGANG_1
            ),
            new SpielDaten(
                    "Schach AI",
                    Schach.SPIELER_GEGEN_AI_FACTORY,
                    UEBERGANG_1
            ),

            new SpielDaten(
                    "Tic Tac Toe",
                    TicTacToe.SPIELER_GEGEN_SPIELER_FACTORY,
                    UEBERGANG_1
            ),
            new SpielDaten(
                    "Tic Tac Toe AI",
                    TicTacToe.SPIELER_GEGEN_AI_FACTORY,
                    UEBERGANG_1
            ),

            new SpielDaten(
                    "Flappy Oinky",
                    FlappyOinky.FACTORY,
                    UEBERGANG_1
            ),
            new SpielDaten(
                    "Bälle",
                    Baelle.FACTORY,
                    UEBERGANG_1
            ),
            new SpielDaten(
                    "Snake",
                    Snake.FACTORY,
                    UEBERGANG_1
            ),
            new SpielDaten(
                    "Tetris",
                    Tetris.FACTORY,
                    UEBERGANG_1
            ),
            new SpielDaten(
                    "Stapeln",
                    Stapeln.FACTORY,
                    UEBERGANG_1
            ),
            new SpielDaten(
                    "Rain Catcher",
                    RainCatcher.FACTORY,
                    UEBERGANG_1
            )
    );

    private final PApplet applet;
    private final Map<Spiel.Spieler.Id, SpielerDaten> alleSpielerDaten = new HashMap<>();
    private State currentState;

    TitleScreen(PApplet applet) {
        this.applet = applet;

        for (Spiel.Spieler.Id spielerId : Spiel.Spieler.Id.values()) {
            alleSpielerDaten.put(spielerId, new SpielerDaten(spielerId));
        }
    }

    void settings() {
        applet.size(applet.displayWidth, applet.displayHeight, PConstants.JAVA2D);
        applet.fullScreen();
    }

    void setup() {
        Schach.init(applet);
        FlappyOinky.init(applet);
        Snake.init(applet);

        currentState = new SpielAuswahlState(0);
    }

    void draw() {
        applet.pushStyle();
        try {
            currentState.draw();
        } catch (RuntimeException e) {
            e.printStackTrace();
            currentState = new SpielAuswahlState(0);
        } finally {
            applet.popStyle();
        }
    }

    void mousePressed() {
        try {
            currentState.mousePressed();
        } catch (RuntimeException e) {
            e.printStackTrace();
            currentState = new SpielAuswahlState(0);
        }
    }

    void keyPressed() {
        try {
            currentState.keyPressed();
        } catch (RuntimeException e) {
            e.printStackTrace();
            currentState = new SpielAuswahlState(0);
        }
    }

    void keyReleased() {
        try {
            currentState.keyReleased();
        } catch (RuntimeException e) {
            e.printStackTrace();
            currentState = new SpielAuswahlState(0);
        }
    }

    private List<SpielerDaten> getAktivierteSpielerDaten() {
        return alleSpielerDaten.values().stream().filter(spieler -> spieler.aktiviert).toList();
    }

    private static final class SpielerDaten {
        private final Spiel.Spieler.Id id;
        private String name;
        private int punkte = 0;
        private boolean aktiviert;

        private SpielerDaten(Spiel.Spieler.Id id) {
            this.id = id;
            name = id.toString();
        }

        private Spiel.Spieler convert() {
            return new Spiel.Spieler(id, name, punkte);
        }
    }

    private static final class SpielDaten {
        private final String name;
        private final Spiel.Factory factory;
        private final SpielUebergang uebergang;

        public SpielDaten(String name, Spiel.Factory factory, SpielUebergang uebergang) {
            this.name = name;
            this.factory = factory;
            this.uebergang = uebergang;
        }
    }

    private static final class SpielUebergang {
        private final Video video;
        private final int frame;

        private SpielUebergang(Video video, int frame) {
            if (frame < 0 || frame >= LOOP_VIDEO.frames) {
                throw new IllegalArgumentException();
            }

            this.video = video;
            this.frame = frame;
        }
    }

    private abstract static class State {
        abstract void draw();

        void keyPressed() { }

        void keyReleased() { }

        void mousePressed() { }
    }

    private final class SpielAuswahlState extends State {
        private final LoopVideoPlayer video;

        private boolean uiEnabled;
        private final Set<SpielKnopf> spielKnoepfe = new HashSet<>();
        private final Set<SpielerStatus> spielerStatuse = new HashSet<>();

        private SpielAuswahlState(int startFrame) {
            video = new LoopVideoPlayer(LOOP_VIDEO, 0.5f, startFrame);

            int zeile = SpielKnopf.ZEILEN - (SPIELE.size()/SpielKnopf.SPALTEN) - 1;
            int spalte = 0;
            for (SpielDaten spiel : SPIELE) {
                spielKnoepfe.add(new SpielKnopf(spiel, zeile, spalte));
                spalte++;
                if (spalte >= SpielKnopf.SPALTEN) {
                    zeile++;
                    spalte = 0;
                }
            }

            for (Spiel.Spieler.Id spielerId : Spiel.Spieler.Id.values()) {
                spielerStatuse.add(new SpielerStatus(spielerId));
            }
        }

        @Override
        void draw() {
            applet.background(0);

            video.draw();

            for (SpielerStatus spielerStatus : spielerStatuse) {
                spielerStatus.draw();
            }

            for (SpielKnopf spielKnopf : spielKnoepfe) {
                spielKnopf.draw();
            }
        }

        @Override
        void mousePressed() {
            for (SpielKnopf spielKnopf : spielKnoepfe) {
                spielKnopf.mousePressed();
            }

            for (SpielerStatus spielerStatus : spielerStatuse) {
                spielerStatus.mousePressed();
            }
        }

        private void toggleUi() {
            if (applet.key != ' ') {
                return;
            }

            uiEnabled = !uiEnabled;
        }

        private void neuInstalisieren() {
            if (applet.key != 'x') {
                return;
            }

            applet.link("https://www.youtube.com/watch?v=0NXLKOfXkoI");
        }

        @Override
        void keyPressed() {
            for (SpielerStatus spielerStatus : spielerStatuse) {
                if (spielerStatus.keyPressed()) {
                    return;
                }
            }

            neuInstalisieren();

            toggleUi();
        }

        private final class SpielKnopf {
            private static final int ZEILEN = 10;
            private static final int SPALTEN = 6;
            private static final float HEIGHT = 1f / ZEILEN;
            private static final float WIDTH = 1f / SPALTEN;
            private static final float INNER_HEIGHT = HEIGHT * (2f / 3f);
            private static final float INNER_WIDTH = WIDTH * (2f / 3f);
            private static final float MARGIN_X = (WIDTH - INNER_WIDTH) / 2;
            private static final float MARGIN_Y = (HEIGHT - INNER_HEIGHT) / 2;
            private static final float MAX_SPEED = 0.015f;

            private final SpielDaten spiel;
            private final int zeile;
            private final int spalte;

            private float currentX;
            private float currentY;

            private SpielKnopf(SpielDaten spiel, int zeile, int spalte) {
                this.spiel = spiel;
                this.zeile = zeile;
                this.spalte = spalte;
                currentX = getOffScreenXPosition();
                currentY = getOffScreenYPosition();
            }

            private void move() {
                float distanceX = Math.abs(currentX - getTargetXPosition());
                float velocityX = Math.min(MAX_SPEED, distanceX);
                if (currentX < getTargetXPosition()) {
                    currentX += velocityX;
                } else {
                    currentX -= velocityX;
                }

                float distanceY = Math.abs(currentY - getTargetYPosition());
                float velocityY = Math.min(MAX_SPEED, distanceY);
                if (currentY < getTargetYPosition()) {
                    currentY += velocityY;
                } else {
                    currentY -= velocityY;
                }
            }

            private boolean isHovered() {
                float breite = INNER_WIDTH * applet.width;
                float hoehe = INNER_HEIGHT * applet.height;
                float xStart = currentX * applet.width;
                float xEnde = xStart + breite;
                float yStart = currentY * applet.height;
                float yEnde = yStart + hoehe;
                return applet.mouseX >= xStart && applet.mouseX <= xEnde && applet.mouseY >= yStart && applet.mouseY <= yEnde;
            }

            private void mousePressed() {
                if (!isHovered()) {
                    return;
                }

                currentState = new UebergangZuUebergangZuSpielState(spiel, video);
            }

            private void draw() {
                move();

                applet.rectMode(PConstants.CORNER);
                applet.fill(getFillColor());
                applet.rect(
                        currentX * applet.width,
                        currentY * applet.height,
                        INNER_WIDTH * applet.width,
                        INNER_HEIGHT * applet.height
                );

                applet.textAlign(PConstants.CENTER, PConstants.CENTER);
                applet.fill(applet.color(0));
                applet.text(
                        spiel.name,
                        (currentX + INNER_WIDTH/2) * applet.width,
                        (currentY + INNER_HEIGHT/2) * applet.height
                );
            }

            private float getOffScreenXPosition() {
                if (spalte < SPALTEN/2) {
                    return -WIDTH;
                }
                return 1;
            }

            private float getTargetXPosition() {
                if (!uiEnabled) {
                    return getOffScreenXPosition();
                }

                return spalte * WIDTH + MARGIN_X;
            }

            private float getOffScreenYPosition() {
                if (zeile < ZEILEN/2) {
                    return -HEIGHT;
                }
                return 1;
            }

            private float getTargetYPosition() {
                if (!uiEnabled) {
                    return getOffScreenYPosition();
                }

                return zeile * HEIGHT + MARGIN_Y;
            }

            private int getFillColor() {
                if (isHovered()) {
                    return applet.color(255, 130, 41);
                }
                return applet.color(255);
            }
        }

        private final class SpielerStatus {
            private static final float HEIGHT = 0.02f;
            private static final float MARGIN = 0.02f;

            private final Spiel.Spieler.Id spielerId;

            private SpielerStatus(Spiel.Spieler.Id spielerId) {
                this.spielerId = spielerId;
            }

            private boolean isHovered() {
                float xStart = getXPosition() * applet.width;
                float yStart = getYPosition() * applet.height;
                float height = HEIGHT * applet.height;
                float width = getWidth();
                if (getTextAlignX() == PConstants.RIGHT) {
                    xStart -= width;
                }
                float xEnd = xStart + width;
                float yEnd = yStart + height;

                return applet.mouseX >= xStart && applet.mouseX <= xEnd && applet.mouseY >= yStart && applet.mouseY <= yEnd;
            }

            private void draw() {
                if (!uiEnabled) {
                    return;
                }

                float x = getXPosition() * applet.width;
                float y = getYPosition() * applet.height;

                applet.fill(getTextColor());
                applet.textSize(HEIGHT * applet.height);
                applet.textAlign(getTextAlignX(), PConstants.TOP);
                applet.text(getText(), x, y);
            }

            private void namenSchreiben() {
                if (applet.key == PConstants.CODED) {
                    return;
                }
                if (applet.key == PConstants.TAB) {
                    return;
                }
                if (applet.key == PConstants.ENTER) {
                    return;
                }
                if (applet.key == PConstants.RETURN) {
                    return;
                }
                if (applet.key == PConstants.BACKSPACE) {
                    return;
                }

                SpielerDaten spielerDaten = alleSpielerDaten.get(spielerId);
                String neuerName = spielerDaten.name + applet.key;
                spielerDaten.name = neuerName;
            }

            private void namenLoeschen() {
                if (applet.key != PConstants.BACKSPACE) {
                    return;
                }

                SpielerDaten spielerDaten = alleSpielerDaten.get(spielerId);
                if (spielerDaten.name.isEmpty()) {
                    return;
                }
                String neuerName = spielerDaten.name.substring(0, spielerDaten.name.length()-1);
                spielerDaten.name = neuerName;
            }

            private boolean keyPressed() {
                if (!isHovered() || !uiEnabled) {
                    return false;
                }

                namenSchreiben();
                namenLoeschen();

                return true;
            }

            private void spielerAktivierenDeaktivieren() {
                SpielerDaten spielerDaten = alleSpielerDaten.get(spielerId);
                spielerDaten.aktiviert = !spielerDaten.aktiviert;
            }

            private void mousePressed() {
                if (!isHovered() || !uiEnabled) {
                    return;
                }

                spielerAktivierenDeaktivieren();
            }

            private int getTextAlignX() {
                switch (spielerId) {
                    case SPIELER_1:
                    case SPIELER_2:
                        return PConstants.LEFT;
                    case SPIELER_3:
                    case SPIELER_4:
                        return PConstants.RIGHT;
                    default:
                        throw new IllegalArgumentException();
                }
            }

            private float getXPosition() {
                switch (spielerId) {
                    case SPIELER_1:
                    case SPIELER_2:
                        return MARGIN;
                    case SPIELER_3:
                    case SPIELER_4:
                        return 1-MARGIN;
                    default:
                        throw new IllegalArgumentException();
                }
            }

            private float getYPosition() {
                switch (spielerId) {
                    case SPIELER_1:
                    case SPIELER_3:
                        return MARGIN;
                    case SPIELER_2:
                    case SPIELER_4:
                        return HEIGHT+MARGIN*2;
                    default:
                        throw new IllegalArgumentException();
                }
            }

            private String getText() {
                SpielerDaten spielerDaten = alleSpielerDaten.get(spielerId);
                return spielerDaten.name + ": " + spielerDaten.punkte;
            }

            private int getTextColor() {
                if (isHovered()) {
                    return applet.color(255, 130, 41);
                }
                SpielerDaten spielerDaten = alleSpielerDaten.get(spielerId);
                if (spielerDaten.aktiviert) {
                    return applet.color(0, 255, 0);
                }
                return applet.color(255);
            }

            private float getWidth() {
                applet.textSize(HEIGHT * applet.height);
                return applet.textWidth(getText());
            }
        }
    }

    private final class UebergangZuUebergangZuSpielState extends State {
        private final SpielDaten spiel;
        private final EinmalVideoPlayer video;

        private UebergangZuUebergangZuSpielState(SpielDaten spiel, LoopVideoPlayer loopVideoPlayer) {
            this.spiel = spiel;
            video = new EinmalVideoPlayer(loopVideoPlayer, 2f, spiel.uebergang.frame);
        }

        @Override
        void mousePressed() {
            currentState = new SpielState(spiel);
        }

        @Override
        void draw() {
            applet.background(0);

            video.draw();

            if (video.finished) {
                currentState = new UebergangZuSpielState(spiel);
            }
        }
    }

    private final class UebergangZuSpielState extends State {
        private final SpielDaten spiel;
        private final EinmalVideoPlayer video;

        private UebergangZuSpielState(SpielDaten spiel) {
            this.spiel = spiel;
            video = new EinmalVideoPlayer(spiel.uebergang.video, 0.5f);
        }

        @Override
        void mousePressed() {
            currentState = new SpielState(spiel);
        }

        @Override
        void draw() {
            applet.background(0);

            video.draw();

            if (video.finished) {
                currentState = new SpielState(spiel);
            }
        }
    }

    private final class SpielState extends State {
        private final SpielDaten spielDaten;
        private final Spiel spiel;

        private SpielState(SpielDaten spielDaten) {
            this.spielDaten = spielDaten;

            List<SpielerDaten> aktivierteSpielerDaten = getAktivierteSpielerDaten();

            if (spielDaten.factory instanceof Spiel.Einzelspieler.Factory) {
                Spiel.Einzelspieler.Factory einzelspielerFactory = (Spiel.Einzelspieler.Factory) spielDaten.factory;
                if (aktivierteSpielerDaten.size() != 1) {
                    SpielerDaten spieler1Daten = alleSpielerDaten.get(Spiel.Spieler.Id.SPIELER_1);
                    spieler1Daten.aktiviert = true;
                    spiel = einzelspielerFactory.neuesSpiel(applet, spieler1Daten.convert());
                    return;
                }
                Spiel.Spieler spieler = aktivierteSpielerDaten.get(0).convert();
                spiel = einzelspielerFactory.neuesSpiel(applet, spieler);
            } else if (spielDaten.factory instanceof Spiel.SpielerGegenSpieler.Factory) {
                Spiel.SpielerGegenSpieler.Factory spielerGegenSpielerFactory = (Spiel.SpielerGegenSpieler.Factory) spielDaten.factory;
                if (aktivierteSpielerDaten.size() != 2) {
                    SpielerDaten spieler1Daten = alleSpielerDaten.get(Spiel.Spieler.Id.SPIELER_1);
                    spieler1Daten.aktiviert = true;
                    SpielerDaten spieler2Daten = alleSpielerDaten.get(Spiel.Spieler.Id.SPIELER_2);
                    spieler2Daten.aktiviert = true;
                    spiel = spielerGegenSpielerFactory.neuesSpiel(applet, spieler1Daten.convert(), spieler2Daten.convert());
                    return;
                }
                Spiel.Spieler spieler1 = aktivierteSpielerDaten.get(0).convert();
                Spiel.Spieler spieler2 = aktivierteSpielerDaten.get(1).convert();
                spiel = spielerGegenSpielerFactory.neuesSpiel(applet, spieler1, spieler2);
            } else {
                Spiel.Mehrspieler.Factory mehrspielerFactory = (Spiel.Mehrspieler.Factory) spielDaten.factory;
                if (aktivierteSpielerDaten.isEmpty()) {
                    SpielerDaten spieler1Daten = alleSpielerDaten.get(Spiel.Spieler.Id.SPIELER_1);
                    spieler1Daten.aktiviert = true;
                    SpielerDaten spieler2Daten = alleSpielerDaten.get(Spiel.Spieler.Id.SPIELER_2);
                    spieler2Daten.aktiviert = true;
                    spiel = mehrspielerFactory.neuesSpiel(applet, Set.of(spieler1Daten.convert(), spieler2Daten.convert()));
                    return;
                }
                Set<Spiel.Spieler> aktivierteSpieler = new HashSet<>();
                for (SpielerDaten aktivierterSpielerDaten : aktivierteSpielerDaten) {
                    aktivierteSpieler.add(aktivierterSpielerDaten.convert());
                }
                spiel = mehrspielerFactory.neuesSpiel(applet, aktivierteSpieler);
            }
        }

        @Override
        void keyPressed() {
            if (applet.key == PConstants.ESC) {
                applet.key = 0;
                currentState = new UebergangVonSpielState(spielDaten);
                return;
            }

            if (applet.key == PConstants.DELETE) {
                currentState = new SpielState(spielDaten);
                return;
            }

            spiel.keyPressed();
        }

        @Override
        void keyReleased() {
            spiel.keyReleased();
        }

        @Override
        void mousePressed() {
            spiel.mousePressed();
        }

        private void drawEinzelspielerSpiel() {
            Spiel.Einzelspieler einzelspielerSpiel = (Spiel.Einzelspieler) spiel;
            Optional<Spiel.Einzelspieler.Ergebnis> ergebnis = einzelspielerSpiel.draw();
            if (ergebnis.isEmpty()) {
                return;
            }

            currentState = new SpielBeendetState(spielDaten);

            if (getAktivierteSpielerDaten().isEmpty()) {
                throw new IllegalStateException();
            }
            SpielerDaten spielerDaten = getAktivierteSpielerDaten().get(0);

            int punkte;
            switch (ergebnis.get()) {
                case GEWONNEN:
                    punkte = 1;
                    break;
                case UNENTSCHIEDEN:
                    punkte = 0;
                    break;
                case VERLOREN:
                    punkte = -1;
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            spielerDaten.punkte += punkte;
        }

        private void drawSpielerGegenSpielerSpiel() {
            Spiel.SpielerGegenSpieler spielerGegenSpielerSpiel = (Spiel.SpielerGegenSpieler) spiel;
            Optional<Optional<Spiel.Spieler.Id>> result = spielerGegenSpielerSpiel.draw();
            if (result.isEmpty()) {
                return;
            }

            currentState = new SpielBeendetState(spielDaten);

            Optional<Spiel.Spieler.Id> gewinnerId = result.get();
            if (gewinnerId.isEmpty()) {
                return;
            }
            SpielerDaten gewinnerSpielerDaten = alleSpielerDaten.get(gewinnerId.get());
            gewinnerSpielerDaten.punkte++;
        }

        private void drawMehrspielerSpiel() {
            Spiel.Mehrspieler mehspielerSpiel = (Spiel.Mehrspieler) spiel;
            Optional<List<Spiel.Spieler.Id>> rangliste = mehspielerSpiel.draw();
            if (rangliste.isEmpty()) {
                return;
            }

            currentState = new SpielBeendetState(spielDaten);

            int punkte = rangliste.get().size() - 1;
            for (int i = 0; i < rangliste.get().size(); i++, punkte--) {
                Spiel.Spieler.Id spielerId = rangliste.get().get(i);
                SpielerDaten spielerDaten = alleSpielerDaten.get(spielerId);
                spielerDaten.punkte += punkte;
            }
        }

        @Override
        void draw() {
            if (spiel instanceof Spiel.Einzelspieler) {
                drawEinzelspielerSpiel();
            } else if (spiel instanceof Spiel.SpielerGegenSpieler) {
                drawSpielerGegenSpielerSpiel();
            } else {
                drawMehrspielerSpiel();
            }
        }
    }

    private final class SpielBeendetState extends State {
        private final SpielDaten spiel;

        private SpielBeendetState(SpielDaten spiel) {
            this.spiel = spiel;

            applet.fill(applet.color(255, 0, 0));
            applet.textAlign(PConstants.CENTER, PConstants.CENTER);
            applet.textSize((float) applet.height / 20);
            applet.text("Game Over", (float) applet.width / 2, (float) applet.height / 2);
        }

        @Override
        void draw() { }

        @Override
        void keyPressed() {
            if (applet.key == ' ' || applet.key == PConstants.ENTER || applet.key == PConstants.RETURN) {
                currentState = new SpielState(spiel);
                return;
            }

            if (applet.key == PConstants.ESC) {
                applet.key = 0;
                currentState = new UebergangVonSpielState(spiel);
            }
        }
    }

    private final class UebergangVonSpielState extends State {
        private final SpielDaten spiel;
        private final EinmalVideoPlayer video;

        private UebergangVonSpielState(SpielDaten spiel) {
            this.spiel = spiel;
            video = new EinmalVideoPlayer(spiel.uebergang.video, 0.5f, spiel.uebergang.video.frames-1, 0);
        }

        @Override
        void mousePressed() {
            currentState = new SpielAuswahlState(spiel.uebergang.frame);
        }

        @Override
        void draw() {
            applet.background(0);

            video.draw();

            if (video.finished) {
                currentState = new SpielAuswahlState(spiel.uebergang.frame);
            }
        }
    }

    private static final class Video {
        private final String path;
        private final int frames;

        private Video(String path, int frames) {
            this.path = path;
            this.frames = frames;
        }

        private String getFrameFileName(int frame) {
            String fileName = Integer.toString(frame+1);
            if (fileName.length() == 1) {
                fileName = "000" + fileName;
            } else if (fileName.length() == 2) {
                fileName = "00" + fileName;
            } else if (fileName.length() == 3) {
                fileName = "0" + fileName;
            }
            return path + "/" + fileName + ".png";
        }
    }

    private final class LoopVideoPlayer {
        private final float speed;
        private final PImage[] frames;
        private float currentFrame;

        private LoopVideoPlayer(Video video, float speed, int startFrame) {
            if (startFrame < 0 || startFrame >= video.frames) {
                throw new IllegalArgumentException();
            }

            this.speed = speed;

            frames = new PImage[video.frames];
            for (int frame = startFrame; frame < video.frames; frame++) {
                frames[frame] = applet.requestImage(video.getFrameFileName(frame));
            }
            for (int frame = 0; frame < startFrame; frame++) {
                frames[frame] = applet.requestImage(video.getFrameFileName(frame));
            }

            currentFrame = startFrame;
        }

        private void draw() {
            PImage frame = frames[(int) currentFrame];
            if (frame.width == 0) {
                return;
            }
            applet.image(frame, 0, 0, applet.width, applet.height);

            currentFrame += speed;
            if (currentFrame >= frames.length) {
                currentFrame = 0;
            }
        }

        private int getCurrentFrame() {
            return (int) currentFrame;
        }
    }

    private final class EinmalVideoPlayer {
        private final float speed;
        private final int destination;
        private final PImage[] frames;
        private float currentFrame;
        private boolean finished;

        private EinmalVideoPlayer(Video video, float speed, int startFrame, int destination) {
            if (startFrame < 0 || startFrame >= video.frames) {
                throw new IllegalArgumentException();
            }

            if (destination < 0 || destination >= video.frames) {
                throw new IllegalArgumentException();
            }

            this.speed = speed;
            this.destination = destination;

            frames = new PImage[video.frames];

            if (startFrame < destination) {
                for (int frame = startFrame; frame <= destination; frame++) {
                    frames[frame] = applet.requestImage(video.getFrameFileName(frame));
                }
            } else {
                for (int frame = startFrame; frame >= destination; frame--) {
                    frames[frame] = applet.requestImage(video.getFrameFileName(frame));
                }
            }

            currentFrame = startFrame;
        }

        private EinmalVideoPlayer(Video video, float speed) {
            this(video, speed, 0, video.frames-1);
        }

        private EinmalVideoPlayer(LoopVideoPlayer loopVideoPlayer, float speed, int destination) {
            frames = loopVideoPlayer.frames;

            if (destination < 0 || destination >= frames.length) {
                throw new IllegalArgumentException();
            }

            this.speed = speed;
            this.destination = destination;
            currentFrame = loopVideoPlayer.getCurrentFrame();
        }

        private void draw() {
            PImage frame = frames[(int) currentFrame];
            if (frame.width == 0) {
                return;
            }

            applet.image(frame, 0, 0, applet.width, applet.height);

            if (finished) {
                return;
            }

            if (currentFrame < destination) {
                currentFrame += speed;
                if (currentFrame >= destination) {
                    currentFrame = destination;
                    finished = true;
                }
            } else if (currentFrame > destination) {
                currentFrame -= speed;
                if (currentFrame <= destination) {
                    currentFrame = destination;
                    finished = true;
                }
            }
        }
    }
}
