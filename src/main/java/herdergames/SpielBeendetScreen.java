package herdergames;

import herdergames.spiel.SpielDaten;
import processing.core.PConstants;

final class SpielBeendetScreen extends Screen {
    private final SpielDaten spiel;

    SpielBeendetScreen(HerderGames herderGames, SpielDaten spiel) {
        super(herderGames);
        this.spiel = spiel;

        applet.fill(applet.color(255, 0, 0));
        applet.textAlign(PConstants.CENTER, PConstants.CENTER);
        applet.textSize((float) applet.height / 20);
        applet.text("Game Over", (float) applet.width / 2, (float) applet.height / 2);
    }

    @Override
    void draw() {
    }

    @Override
    void keyPressed() {
        if (applet.key == ' ' || applet.key == PConstants.ENTER || applet.key == PConstants.RETURN) {
            herderGames.openScreen(new SpielScreen(herderGames, spiel));
            return;
        }

        if (applet.key == PConstants.ESC) {
            applet.key = 0;
            herderGames.openScreen(new UebergangVonSpielScreen(herderGames, spiel));
        }
    }
}
