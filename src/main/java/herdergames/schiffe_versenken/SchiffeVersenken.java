package herdergames.schiffe_versenken;

import herdergames.spiel.Spieler;
import herdergames.spiel.SpielerGegenSpielerSpiel;
import processing.core.PApplet;

import java.util.Optional;

public final class SchiffeVersenken extends SpielerGegenSpielerSpiel {
    final Spieler spieler1;
    final Spieler spieler2;
    Optional<SpielBrett> spieler1SpielBrett = Optional.empty();
    Optional<SpielBrett> spieler2SpielBrett = Optional.empty();
    private Screen currentScreen;

    public SchiffeVersenken(PApplet applet, Spieler spieler1, Spieler spieler2) {
        super(applet);
        this.spieler1 = spieler1;
        this.spieler2 = spieler2;
        currentScreen = new WegGuckenScreen(
                this,
                spieler2,
                () -> new SchiffeSetzenScreen(
                        this,
                        spieler1,
                        () -> new WegGuckenScreen(
                                this,
                                spieler1,
                                () -> new SchiffeSetzenScreen(
                                        this,
                                        spieler2,
                                        () -> new WegGuckenScreen(
                                                this,
                                                spieler2,
                                                () -> new ZugMachenScreen(
                                                        this,
                                                        spieler1
                                                )
                                        )
                                )
                        )
                )
        );
    }

    @Override
    public Optional<Optional<Spieler.Id>> draw() {
        applet.background(255);

        currentScreen.draw();

        if (spieler1SpielBrett.isPresent() && spieler1SpielBrett.get().hatVerloren()) {
            return Optional.of(Optional.of(spieler2.id()));
        }
        if (spieler2SpielBrett.isPresent() && spieler2SpielBrett.get().hatVerloren()) {
            return Optional.of(Optional.of(spieler1.id()));
        }

        return Optional.empty();
    }

    @Override
    public void mousePressed() {
        currentScreen.mousePressed();
    }

    @Override
    public void keyPressed() {
        currentScreen.keyPressed();
    }

    @Override
    public void keyReleased() {
        currentScreen.keyReleased();
    }

    void openScreen(Screen neuerScreen) {
        currentScreen = neuerScreen;
    }
}
