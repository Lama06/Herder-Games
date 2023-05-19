package herdergames.bowling;

import herdergames.spiel.MehrspielerSpiel;
import herdergames.spiel.Spieler;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.util.*;

public final class Bowling extends MehrspielerSpiel {
    private static final int ZIELEN_ZEIT = 5 * 60;
    private static final int ANZAHL_KUGELN = 3;

    static PImage kegelBild;
    static PImage bodenBild;

    public static void init(PApplet applet) {
        kegelBild = applet.loadImage("bowling/kegel.png");
        bodenBild = applet.loadImage("bowling/boden.png");
    }

    private final List<Bahn> bahnen = new ArrayList<>();
    Status status = Status.ZIELEN;
    private int verbleibendeZielenZeit = ZIELEN_ZEIT;
    private int verbleibendeKugeln = ANZAHL_KUGELN - 1;

    public Bowling(PApplet applet, Set<Spieler> alleSpieler) {
        super(applet);
        List<Spieler> spielerSortiert = alleSpieler.stream().sorted(Comparator.comparing(Spieler::id)).toList();
        for (Spieler spieler : spielerSortiert) {
            bahnen.add(new Bahn(this, spieler));
        }
    }

    private void drawBoden() {
        for (float bodenX = 0; bodenX < applet.width; bodenX += bodenBild.width) {
            for (float bodenY = 0; bodenY < applet.height; bodenY += bodenBild.height) {
                applet.imageMode(PConstants.CORNER);
                applet.image(bodenBild, bodenX, bodenY);
            }
        }
    }

    @Override
    public Optional<List<Spieler.Id>> draw() {
        drawBoden();

        float bahnBreite = (float) applet.width / bahnen.size();
        var bahnIndex = 0;
        for (Bahn bahn : bahnen) {
            float bahnX = bahnBreite * bahnIndex++;
            bahn.draw(bahnX, bahnBreite);
        }

        switch (status) {
            case ZIELEN -> {
                applet.fill(verbleibendeZielenZeit <= 2 * 60 ? applet.color(255, 0, 0) : 0);
                applet.textSize(applet.height / 10f);
                applet.textAlign(PConstants.CENTER, PConstants.TOP);
                applet.text(verbleibendeZielenZeit / 60, applet.width / 2f, 0);

                if (verbleibendeZielenZeit-- <= 0) {
                    status = Status.SCHIESSEN;
                    bahnen.forEach(Bahn::schiessen);
                }
            }
            case SCHIESSEN -> {
                if (bahnen.stream().allMatch(bahn -> bahn.kugel.istObenRaus())) {
                    if (verbleibendeKugeln == 0) {
                        List<Spieler.Id> rangliste = bahnen
                                .stream()
                                .sorted(Comparator.comparingInt(bahnen -> bahnen.kegel.size()))
                                .map(bahn -> bahn.spieler.id())
                                .toList();
                        return Optional.of(rangliste);
                    }

                    status = Status.ZIELEN;
                    verbleibendeKugeln--;
                    verbleibendeZielenZeit = ZIELEN_ZEIT;
                    bahnen.forEach(Bahn::resetKugel);
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public void keyPressed() {
        bahnen.forEach(Bahn::keyPressed);
    }

    @Override
    public void keyReleased() {
        bahnen.forEach(Bahn::keyReleased);
    }
}
