package herdergames.crossy_road;

import herdergames.util.GewichteteListe;
import herdergames.util.Rechteck;
import processing.core.PApplet;

import java.util.List;
import java.util.function.Supplier;

abstract class Zeile {
    static Zeile zufaellig(CrossyRoad spiel, int y) {
        List<GewichteteListe.Eintrag<Supplier<Zeile>>> moeglichkeiten = List.of(
                new GewichteteListe.Eintrag<>(() -> new Strasse(spiel, y), 2),
                new GewichteteListe.Eintrag<>(() -> new Wiese(spiel, y), 3),
                new GewichteteListe.Eintrag<>(() -> new Bahnstrecke(spiel, y), 2)
        );

        return GewichteteListe.zufaellig(spiel.applet, moeglichkeiten).get();
    }

    protected final CrossyRoad spiel;
    protected final PApplet applet;
    protected final int y;

    Zeile(CrossyRoad spiel, int y) {
        this.spiel = spiel;
        applet = spiel.applet;
        this.y = y;
    }

    abstract void draw();

    abstract List<Rechteck> getHitboxen();

    boolean istUntenWeg() {
        return spiel.istUntenWeg(y);
    }
}
