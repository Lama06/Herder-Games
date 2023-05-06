package herdergames;

import herdergames.spiel.SpielDaten;
import herdergames.video.EinmalVideoPlayer;

final class UebergangZuSpielScreen extends Screen {
    private final SpielDaten spiel;
    private final EinmalVideoPlayer video;

    UebergangZuSpielScreen(HerderGames herderGames, SpielDaten spiel) {
        super(herderGames);
        this.spiel = spiel;
        video = new EinmalVideoPlayer(applet, spiel.uebergang().video(), 0.5f);
    }

    @Override
    void mousePressed() {
        herderGames.openScreen(new SpielScreen(herderGames, spiel));
    }

    @Override
    void draw() {
        applet.background(0);

        video.draw();

        if (video.istFertig()) {
            herderGames.openScreen(new SpielScreen(herderGames, spiel));
        }
    }
}
