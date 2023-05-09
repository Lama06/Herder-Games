package herdergames;

import herdergames.spiel.*;
import processing.core.PApplet;
import processing.core.PConstants;

import java.util.*;

public final class HerderGames {
    private final PApplet applet;
    private final Map<Spieler.Id, SpielerDaten> spielerDaten = new EnumMap<>(Spieler.Id.class);
    private Screen currentScreen;

    public HerderGames(PApplet applet) {
        this.applet = applet;

        for (Spieler.Id spielerId : Spieler.Id.values()) {
            spielerDaten.put(spielerId, new SpielerDaten(spielerId));
        }
    }

    public void settings() {
        applet.fullScreen(PConstants.JAVA2D);
    }

    public void setup() {
        for (SpielDaten spielDaten : SpielDaten.SPIELE) {
            try {
                spielDaten.init().accept(applet);
            } catch (RuntimeException e) {
                PApplet.println("Fehler beim initialisieren des Spiels %s".formatted(spielDaten.name()));
                e.printStackTrace();
            }
        }

        currentScreen = new VideosSkalierenScreen(this);
    }

    public void draw() {
        applet.pushStyle();
        try {
            currentScreen.draw();
        } catch (RuntimeException e) {
            e.printStackTrace();
            currentScreen = new SpielAuswahlScreen(this, 0);
        } finally {
            applet.popStyle();
        }
    }

    public void mousePressed() {
        try {
            currentScreen.mousePressed();
        } catch (RuntimeException e) {
            e.printStackTrace();
            currentScreen = new SpielAuswahlScreen(this, 0);
        }
    }

    public void keyPressed() {
        try {
            currentScreen.keyPressed();
        } catch (RuntimeException e) {
            e.printStackTrace();
            currentScreen = new SpielAuswahlScreen(this, 0);
        }
    }

    public void keyReleased() {
        try {
            currentScreen.keyReleased();
        } catch (RuntimeException e) {
            e.printStackTrace();
            currentScreen = new SpielAuswahlScreen(this, 0);
        }
    }

    PApplet getApplet() {
        return applet;
    }

    List<SpielerDaten> getAktivierteSpielerDaten() {
        return spielerDaten.values().stream().filter(spieler -> spieler.aktiviert).toList();
    }

    Map<Spieler.Id, SpielerDaten> getSpielerDaten() {
        return spielerDaten;
    }

    void openScreen(Screen currentScreen) {
        this.currentScreen = currentScreen;
    }
}
