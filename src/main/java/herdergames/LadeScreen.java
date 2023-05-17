package herdergames;

import herdergames.util.PartikelManager;
import herdergames.video.Video;
import processing.core.PConstants;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;

final class LadeScreen extends Screen {
    private final List<PImage> frames = new ArrayList<>();
    private final PartikelManager partikelManager = new PartikelManager(applet);
    private final FortschrittsAnzeige fortschrittsAnzeige = new FortschrittsAnzeige();
    private final StatusText statusText = new StatusText();

    LadeScreen(HerderGames herderGames) {
        super(herderGames);
    }

    @Override
    void draw() {
        if (frames.size() == Video.LOOP_VIDEO.frames()) {
            herderGames.openScreen(new SpielAuswahlScreen(herderGames, frames.toArray(PImage[]::new)));
            return;
        }

        applet.background(255);
        fortschrittsAnzeige.draw();
        statusText.draw();
        partikelManager.draw();
        partikelManager.spawnPartikel(
                (float) applet.mouseX / (float) applet.width,
                (float) applet.mouseY / (float) applet.height,
                1 + (int) (6f * ((float) frames.size() / (float) Video.LOOP_VIDEO.frames()))
        );

        PImage frame = applet.loadImage(Video.LOOP_VIDEO.getFramePath(frames.size()));
        frames.add(frame);
    }

    @Override
    void keyPressed() {
        if (applet.key != 'w') {
            return;
        }

        herderGames.openScreen(new SpielAuswahlScreen(herderGames, 0));
    }

    private final class FortschrittsAnzeige {
        private static final float BREITE = 0.5f;
        private static final float HOEHE = 0.05f;
        private static final float X = 0.5f - BREITE / 2;
        private static final float Y = 0.5f - HOEHE / 2;
        private static final int STROKE_WEIGHT = 2;

        private void draw() {
            applet.strokeWeight(2);
            applet.stroke(0);
            applet.rectMode(PConstants.CORNER);
            applet.fill(255);
            applet.rect(X * applet.width, Y * applet.height, BREITE * applet.width, HOEHE * applet.height);

            applet.noStroke();
            applet.fill(0, 255, 0);
            applet.rect(
                    X * applet.width + STROKE_WEIGHT,
                    Y * applet.height + STROKE_WEIGHT,
                    ((float) frames.size() / (float) Video.LOOP_VIDEO.frames()) * BREITE * applet.width - STROKE_WEIGHT*2,
                    HOEHE * applet.height - STROKE_WEIGHT*2
            );
        }
    }

    private final class StatusText {
        private static final float TEXT_SIZE = 0.03f;
        private static final float X = 0.5f;
        private static final float Y = FortschrittsAnzeige.Y + FortschrittsAnzeige.HOEHE + TEXT_SIZE;

        private void draw() {
            applet.fill(0);
            applet.textAlign(PConstants.CENTER, PConstants.TOP);
            applet.textSize(TEXT_SIZE * applet.height);
            applet.text(
                    Video.LOOP_VIDEO.getFramePath(frames.size()) + " laden. " + (int) applet.frameRate + "FPS. Überspringen mit W (Nicht empfohlen).",
                    X * applet.width,
                    Y * applet.height
            );
        }
    }
}
