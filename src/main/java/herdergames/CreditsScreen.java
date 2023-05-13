package herdergames;

import herdergames.util.ImageUtil;
import herdergames.video.Video;
import processing.core.PConstants;
import processing.core.PImage;

final class CreditsScreen extends Screen {
    private static final String CREDITS_FILE = "titlescreen/credits.txt";
    private static final float TEXT_SIZE = 0.05f;
    private static final float TEXT_ABSTAND = TEXT_SIZE / 2;
    private static final float SCROLL_GESCHWINDIGKEIT = -0.001f;

    private final int currentFrame;
    private final PImage backgroundImage;
    private final String[] creditsZeilen;
    private float y = 1f;

    CreditsScreen(HerderGames herderGames, int currentFrame) {
        super(herderGames);
        this.currentFrame = currentFrame;
        backgroundImage = applet.loadImage(Video.LOOP_VIDEO.getFramePath(currentFrame));
        creditsZeilen = applet.loadStrings(CREDITS_FILE);
    }

    private float getLaenge() {
        return creditsZeilen.length * (TEXT_SIZE + TEXT_ABSTAND);
    }

    @Override
    void draw() {
        if (y <= -getLaenge()) {
            herderGames.openScreen(new SpielAuswahlScreen(herderGames, currentFrame));
            return;
        }

        y += SCROLL_GESCHWINDIGKEIT * (applet.keyPressed && applet.key == ' ' ? 2 : 1);

        ImageUtil.imageVollbildZeichnen(applet, backgroundImage);

        applet.textAlign(PConstants.CENTER, PConstants.TOP);
        applet.textSize(TEXT_SIZE * (float) applet.height);

        float y = this.y;
        for (String zeile : creditsZeilen) {
            applet.text(zeile, (float) applet.width / 2f, y * (float) applet.height);
            y += TEXT_SIZE + TEXT_ABSTAND;
        }
    }

    @Override
    void keyPressed() {
        if (applet.key != PConstants.ESC) {
            return;
        }

        applet.key = 0;

        herderGames.openScreen(new SpielAuswahlScreen(herderGames, currentFrame));
    }

    @Override
    void mousePressed() {
        herderGames.openScreen(new SpielAuswahlScreen(herderGames, currentFrame));
    }
}
