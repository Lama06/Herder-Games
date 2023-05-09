package herdergames;

import herdergames.util.PartikelManager;
import herdergames.video.Video;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

import java.util.List;
import java.util.stream.IntStream;

final class VideosSkalierenScreen extends Screen {
    private final List<Frame> frames;
    private int naechsterFrameIndex = 0;

    private final PartikelManager partikelManager = new PartikelManager(applet);
    private final FortschrittsAnzeige fortschrittsAnzeige = new FortschrittsAnzeige();
    private final StatusText statusText = new StatusText();

    VideosSkalierenScreen(HerderGames herderGames) {
        super(herderGames);
        frames = Video.VIDEOS.stream()
                .flatMap(video -> IntStream.range(0, video.frames()).mapToObj(frame -> new Frame(video, frame)))
                .toList();
    }

    @Override
    void draw() {
        if (naechsterFrameIndex >= frames.size()) {
            herderGames.openScreen(new SpielAuswahlScreen(herderGames, 0));
            return;
        }

        applet.background(255);
        fortschrittsAnzeige.draw();
        statusText.draw();
        partikelManager.draw();
        partikelManager.spawnPartikel(
                (float) applet.mouseX / (float) applet.width,
                (float) applet.mouseY / (float) applet.height,
                1 + (int) (6f * ((float) naechsterFrameIndex / (float) frames.size()))
        );

        Frame frame = frames.get(naechsterFrameIndex++);

        if (frame.video().istSkaliert(applet, frame.frame())) {
            return;
        }

        PImage image = applet.loadImage(frame.video().getOriginalFramePath(frame.frame()));
        PGraphics graphics = applet.createGraphics(applet.width, applet.height);
        graphics.beginDraw();
        graphics.imageMode(PConstants.CORNER);
        graphics.image(image, 0, 0, applet.width, applet.height);
        graphics.endDraw();
        graphics.save(frame.video().getSkaliertFramePath(applet, frame.frame()));
    }

    @Override
    void keyPressed() {
        if (applet.key != 'w') {
            return;
        }

        herderGames.openScreen(new SpielAuswahlScreen(herderGames, 0));
    }

    private record Frame(Video video, int frame) { }

    private final class FortschrittsAnzeige {
        private static final float BREITE = 0.5f;
        private static final float HOEHE = 0.05f;
        private static final float X = 0.5f - BREITE/2;
        private static final float Y = 0.5f - HOEHE/2;
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
                    ((float) naechsterFrameIndex / (float) frames.size()) * BREITE * applet.width - STROKE_WEIGHT*2,
                    HOEHE * applet.height - STROKE_WEIGHT*2
            );
        }
    }

    private final class StatusText {
        private static final float TEXT_SIZE = 0.03f;
        private static final float X = 0.5f;
        private static final float Y = FortschrittsAnzeige.Y + FortschrittsAnzeige.HOEHE + TEXT_SIZE;

        private void draw() {
            Frame frame = frames.get(naechsterFrameIndex);

            applet.fill(0);
            applet.textAlign(PConstants.CENTER, PConstants.TOP);
            applet.textSize(TEXT_SIZE * applet.height);
            applet.text(
                    frame.video().getOriginalFramePath(frame.frame()) + " skalieren... (Überspringen mit W)",
                    X * applet.width,
                    Y * applet.height
            );
        }
    }
}
