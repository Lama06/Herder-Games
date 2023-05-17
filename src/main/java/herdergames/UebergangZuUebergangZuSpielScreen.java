package herdergames;

import herdergames.spiel.SpielDaten;
import herdergames.video.EinmalVideoPlayer;
import herdergames.video.LoopVideoPlayer;

final class UebergangZuUebergangZuSpielScreen extends Screen {
    private final SpielDaten spiel;
    private final EinmalVideoPlayer video;

    UebergangZuUebergangZuSpielScreen(HerderGames herderGames, SpielDaten spiel, LoopVideoPlayer loopVideoPlayer) {
        super(herderGames);
        this.spiel = spiel;
        video = new EinmalVideoPlayer(loopVideoPlayer, 4f, spiel.uebergang().frame());
    }

    @Override
    void mousePressed() {
        herderGames.openScreen(new SpielScreen(herderGames, spiel));
    }

    @Override
    void keyPressed() {
        if (applet.key != ' ') {
            return;
        }

        herderGames.openScreen(new SpielScreen(herderGames, spiel));
    }

    @Override
    void draw() {
        applet.background(0);

        video.draw();

        if (video.istFertig()) {
            herderGames.openScreen(new UebergangZuSpielScreen(herderGames, spiel));
        }
    }
}
