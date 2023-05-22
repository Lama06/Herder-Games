package herdergames;

import herdergames.spiel.*;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.MouseEvent;

import java.util.*;

public final class HerderGames {
    private final PApplet applet;
    private final Map<Spieler.Id, SpielerDaten> spielerDaten = new EnumMap<>(Spieler.Id.class);
    private Screen currentScreen;
    private Fliege fliege;

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
        applet.getSurface().setTitle("Herder Games");

        Iterator<SpielDaten> spieleIterator = SpielDaten.SPIELE.iterator();
        while (spieleIterator.hasNext()) {
            SpielDaten spielDaten = spieleIterator.next();
            try {
                spielDaten.init().accept(applet);
            } catch (RuntimeException e) {
                PApplet.println("Fehler beim initialisieren des Spiels %s".formatted(spielDaten.name()));
                e.printStackTrace();
                spieleIterator.remove();
            }
        }

        currentScreen = new AGBScreen(this);
        fliege = new Fliege(applet);
    }

    public void draw() {
        applet.push();
        try {
            currentScreen.draw();
        } catch (RuntimeException e) {
            e.printStackTrace();
            currentScreen = new ErrorScreen(this, e);
        } finally {
            applet.pop();
        }

        applet.push();
        try {
            fliege.draw();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            applet.pop();
        }
    }

    public void mousePressed() {
        try {
            currentScreen.mousePressed();
        } catch (RuntimeException e) {
            e.printStackTrace();
            currentScreen = new ErrorScreen(this, e);
        }
    }

    public void mouseReleased() {
        try {
            currentScreen.mouseReleased();
        } catch (RuntimeException e) {
            e.printStackTrace();
            currentScreen = new ErrorScreen(this, e);
        }
    }

    public void mouseWheel(MouseEvent event) {
        try {
            currentScreen.mouseWheel(event);
        } catch (RuntimeException e) {
            e.printStackTrace();
            currentScreen = new ErrorScreen(this, e);
        }
    }

    public void keyPressed() {
        try {
            currentScreen.keyPressed();
        } catch (RuntimeException e) {
            e.printStackTrace();
            currentScreen = new ErrorScreen(this, e);
        } finally {
            if (applet.key == PConstants.ESC) {
                applet.key = 0; // Verhindern, dass das Fenster geschlossen wird
            }
        }
    }

    public void keyReleased() {
        try {
            currentScreen.keyReleased();
        } catch (RuntimeException e) {
            e.printStackTrace();
            currentScreen = new ErrorScreen(this, e);
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
