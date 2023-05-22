package herdergames;

// Allgemeine Geld Beklauung

import processing.core.PConstants;
import processing.event.MouseEvent;

final class AGBScreen extends Screen {
    private static final String DATEI = "agb.txt";

    private static final String TITLE_TEXT = "Allgemeine Geschäfts Bedingungen";
    private static final float TITLE_Y = 0.04f;
    private static final float TITLE_TEXT_SIZE = 0.07f;

    private static final String HEADER_TEXT = "Zum Akzeptieren Maustaste drücken, zum Ablehnen bis nach unten scrollen";
    private static final float HEADER_Y = TITLE_Y + TITLE_TEXT_SIZE + 0.03f;
    private static final float HEADER_TEXT_SIZE = 0.04f;

    private static final float TEXT_Y = HEADER_Y + HEADER_TEXT_SIZE + 0.1f;
    private static final float TEXT_SIZE = 0.03f;
    private static final float TEXT_HOEHE = 1f - TEXT_Y;
    private static final int ANZAHL_ZEILEN_AUF_BILDSCHIRM = 1 + (int) (TEXT_HOEHE / TEXT_SIZE);

    private final String[] zeilen;
    private int zeile = 0;

    AGBScreen(HerderGames herderGames) {
        super(herderGames);
        zeilen = applet.loadStrings(DATEI);
    }

    private String getZeileText(int index) {
        return zeilen[index % zeilen.length];
    }

    @Override
    void draw() {
        applet.background(255);
        applet.fill(0);

        applet.textAlign(PConstants.CENTER, PConstants.TOP);

        applet.textSize(TITLE_TEXT_SIZE * applet.height);
        applet.text(TITLE_TEXT, applet.width / 2f, TITLE_Y * applet.height);

        applet.textSize(HEADER_TEXT_SIZE * applet.height);
        applet.text(HEADER_TEXT, applet.width / 2f, HEADER_Y * applet.height);

        for (int i = 0; i < ANZAHL_ZEILEN_AUF_BILDSCHIRM; i++) {
            applet.textSize(TEXT_SIZE * applet.height);
            applet.text(getZeileText(zeile + i), applet.width / 2f, (TEXT_Y + TEXT_SIZE * i) * applet.height);
        }
    }

    @Override
    void keyPressed() {
        if (applet.key != PConstants.CODED) {
            return;
        }

        switch (applet.keyCode) {
            case PConstants.UP -> {
                if (zeile != 0) {
                    zeile--;
                }
            }
            case PConstants.DOWN -> zeile++;
        }
    }

    @Override
    void mousePressed() {
        herderGames.openScreen(new LadeScreen(herderGames));
    }

    @Override
    void mouseWheel(MouseEvent event) {
        zeile = Math.max(zeile + event.getCount(), 0);
    }
}
