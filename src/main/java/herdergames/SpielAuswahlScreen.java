package herdergames;

import herdergames.spiel.SpielDaten;
import herdergames.spiel.Spieler;
import herdergames.video.LoopVideoPlayer;
import herdergames.video.Video;
import processing.core.PConstants;
import processing.core.PImage;

import java.util.HashSet;
import java.util.Set;

final class SpielAuswahlScreen extends Screen {
    private final LoopVideoPlayer video;

    private boolean uiEnabled;
    private final SpaceDrueckenHinweis spaceDrueckenHinweis = new SpaceDrueckenHinweis();
    private final CreditsHinweis creditsHinweis = new CreditsHinweis();
    private final Set<SpielKnopf> spielKnoepfe = new HashSet<>();
    private final Set<SpielerStatus> spielerStatuse = new HashSet<>();

    {
        int zeile = SpielKnopf.ZEILEN - (SpielDaten.SPIELE.size() / SpielKnopf.SPALTEN) - 1;
        int spalte = 0;
        for (SpielDaten spiel : SpielDaten.SPIELE) {
            spielKnoepfe.add(new SpielKnopf(spiel, zeile, spalte));
            spalte++;
            if (spalte >= SpielKnopf.SPALTEN) {
                zeile++;
                spalte = 0;
            }
        }

        for (Spieler.Id spielerId : Spieler.Id.values()) {
            spielerStatuse.add(new SpielerStatus(spielerId));
        }
    }

    SpielAuswahlScreen(HerderGames herderGames, int startFrame) {
        super(herderGames);
        video = new LoopVideoPlayer(applet, Video.LOOP_VIDEO, startFrame);
    }

    SpielAuswahlScreen(HerderGames herderGames, PImage[] frames) {
        super(herderGames);
        video = new LoopVideoPlayer(applet, Video.LOOP_VIDEO, frames, 0);
    }

    @Override
    void draw() {
        applet.background(0);

        video.draw();

        spaceDrueckenHinweis.draw();
        creditsHinweis.draw();

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

    private void neuInstalisieren() { // :)
        if (applet.key != 'x') {
            return;
        }

        applet.link("https://www.youtube.com/watch?v=0NXLKOfXkoI");
    }

    private void creditsOeffnen() {
        if (applet.key != 'c') {
            return;
        }

        herderGames.openScreen(new CreditsScreen(herderGames, video.getCurrentFrame()));
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

        creditsOeffnen();
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

            herderGames.openScreen(new UebergangZuUebergangZuSpielScreen(herderGames, spiel, video));
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
                    spiel.name(),
                    (currentX + INNER_WIDTH / 2) * applet.width,
                    (currentY + INNER_HEIGHT / 2) * applet.height
            );
        }

        private float getOffScreenXPosition() {
            if (spalte < SPALTEN / 2) {
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
            if (zeile < ZEILEN / 2) {
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

        private final Spieler.Id spielerId;

        private SpielerStatus(Spieler.Id spielerId) {
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

            SpielerDaten spielerDaten = herderGames.getSpielerDaten().get(spielerId);
            String neuerName = spielerDaten.name + applet.key;
            spielerDaten.name = neuerName;
        }

        private void namenLoeschen() {
            if (applet.key != PConstants.BACKSPACE) {
                return;
            }

            SpielerDaten spielerDaten = herderGames.getSpielerDaten().get(spielerId);
            if (spielerDaten.name.isEmpty()) {
                return;
            }
            String neuerName = spielerDaten.name.substring(0, spielerDaten.name.length() - 1);
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
            SpielerDaten spielerDaten = herderGames.getSpielerDaten().get(spielerId);
            spielerDaten.aktiviert = !spielerDaten.aktiviert;
        }

        private void mousePressed() {
            if (!isHovered() || !uiEnabled) {
                return;
            }

            spielerAktivierenDeaktivieren();
        }

        private int getTextAlignX() {
            return switch (spielerId) {
                case SPIELER_1, SPIELER_2 -> PConstants.LEFT;
                case SPIELER_3, SPIELER_4 -> PConstants.RIGHT;
            };
        }

        private float getXPosition() {
            return switch (spielerId) {
                case SPIELER_1, SPIELER_2 -> MARGIN;
                case SPIELER_3, SPIELER_4 -> 1 - MARGIN;
            };
        }

        private float getYPosition() {
            return switch (spielerId) {
                case SPIELER_1, SPIELER_3 -> MARGIN;
                case SPIELER_2, SPIELER_4 -> HEIGHT + MARGIN * 2;
            };
        }

        private String getText() {
            SpielerDaten spielerDaten = herderGames.getSpielerDaten().get(spielerId);
            return spielerDaten.name + ": " + spielerDaten.punkte;
        }

        private int getTextColor() {
            if (isHovered()) {
                return applet.color(255, 130, 41);
            }
            SpielerDaten spielerDaten = herderGames.getSpielerDaten().get(spielerId);
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

    private final class SpaceDrueckenHinweis {
        private static final String TEXT = "Starten: Space Drücken";
        private static final float TEXT_SIZE = 0.04f;
        private static final float ALPHA_AENDERUNG = 1.8f;

        private float alpha = 0;
        private float alphaAenderung = ALPHA_AENDERUNG;

        private void draw() {
            if (uiEnabled) {
                alpha = 0;
                alphaAenderung = ALPHA_AENDERUNG;
                return;
            }

            alpha += alphaAenderung;

            if (alpha > 255) {
                alphaAenderung = -ALPHA_AENDERUNG;
            } else if (alpha < 0) {
                alphaAenderung = ALPHA_AENDERUNG;
            }

            applet.textAlign(PConstants.CENTER, PConstants.BOTTOM);
            applet.textSize(TEXT_SIZE * (float) applet.height);
            applet.fill(applet.color(255, alpha));
            applet.text(TEXT, (float) applet.width / 2, applet.height);
        }
    }

    private final class CreditsHinweis {
        private static final String TEXT = "Credits: C Drücken";
        private static final float TEXT_SIZE = 0.04f;
        private static final float GESCHWINDIGKEIT = 0.001f;

        private float x = 1f;

        private float getTargetX() {
            applet.textSize(TEXT_SIZE * (float) applet.height);
            float breite = applet.textWidth(TEXT) / applet.width;
            return 1f - breite;
        }

        private void draw() {
            if (uiEnabled) {
                x = 1f;
                return;
            }

            if (x > getTargetX()) {
                float diff = getTargetX() - x;
                if (Math.abs(diff) > GESCHWINDIGKEIT) {
                    diff = -GESCHWINDIGKEIT;
                }
                x += diff;
            }

            applet.textAlign(PConstants.LEFT, PConstants.BOTTOM);
            applet.textSize(TEXT_SIZE * (float) applet.height);
            applet.fill(applet.color(255));
            applet.text(TEXT, x * (float) applet.width, applet.height);
        }
    }
}
