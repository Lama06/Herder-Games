package herdergames.schiffe_versenken;

import herdergames.spiel.Spieler;
import processing.core.PConstants;

import java.util.function.Supplier;

final class WegGuckenScreen extends Screen {
    private static final int ZEIT = 7 * 60;
    private static final float TEXT_X = 0.5f;
    private static final float TEXT_Y = 0.5f;
    private static final float TEXT_SIZE = 0.05f;

    private final Spieler spieler;
    private final Supplier<Screen> naechsterScreenSupplier;
    private int verbleibendeZeit = ZEIT;

    WegGuckenScreen(SchiffeVersenken spiel, Spieler spieler, Supplier<Screen> naechsterScreenSupplier) {
        super(spiel);
        this.spieler = spieler;
        this.naechsterScreenSupplier = naechsterScreenSupplier;
    }

    @Override
    void keyPressed() {
        if (applet.key != ' ') {
            return;
        }

        spiel.openScreen(naechsterScreenSupplier.get());
    }

    @Override
    void draw() {
        if (verbleibendeZeit-- <= 0) {
            spiel.openScreen(naechsterScreenSupplier.get());
            return;
        }

        applet.textAlign(PConstants.CENTER, PConstants.CENTER);
        applet.textSize(TEXT_SIZE * applet.height);
        applet.fill(0);
        applet.text(
                "Bitte weggucken: " + spieler.name() + " - " + verbleibendeZeit / 60 + "s",
                TEXT_X * applet.width,
                TEXT_Y * applet.height
        );
    }
}
