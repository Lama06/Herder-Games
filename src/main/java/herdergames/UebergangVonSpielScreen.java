package herdergames;

import herdergames.spiel.SpielDaten;
import herdergames.video.EinmalVideoPlayer;

final class UebergangVonSpielScreen extends Screen {
    private final SpielDaten spiel;
    private final EinmalVideoPlayer video;

    UebergangVonSpielScreen(HerderGames herderGames, SpielDaten spiel) {
        super(herderGames);
        this.spiel = spiel;
        video = new EinmalVideoPlayer(applet, spiel.uebergang().video(), 1, spiel.uebergang().video().frames() - 1, 0);
    }

    @Override
    void mousePressed() {
        herderGames.openScreen(new SpielAuswahlScreen(herderGames, spiel.uebergang().frame()));
    }

    @Override
    void draw() {
        applet.background(0);

        video.draw();

        if (video.istFertig()) {
            herderGames.openScreen(new SpielAuswahlScreen(herderGames, spiel.uebergang().frame()));
        }
    }
}
