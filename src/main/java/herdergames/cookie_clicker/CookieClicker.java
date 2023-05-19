package herdergames.cookie_clicker;

import herdergames.spiel.EinzelspielerSpiel;
import herdergames.spiel.Spieler;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.nio.file.Files;
import java.util.Optional;

public final class CookieClicker extends EinzelspielerSpiel {
    private static PImage keksBild;

    public static void init(PApplet applet) {
        keksBild = applet.loadImage("cookie_clicker/keks.png");
    }

    private long kekse = 0;
    private long kekseProSekunde = 0;
    private final Upgrade[] upgrades = {
            new Upgrade("Keks Bäcker 1", 15, 1),
            new Upgrade("Keks Bäcker 2", 100, 5),
            new Upgrade("Keks Bäcker 3", 500, 10),
            new Upgrade("Keks Bäcker 4", 1_000, 50),
            new Upgrade("Keks Bäcker 5", 5_000, 100),
            new Upgrade("Keks Bäcker 6", 10_000, 500),
            new Upgrade("Keks Bäcker 7", 50_000, 1_000),
            new Upgrade("Keks Bäcker 8", 100_000, 5_000),
            new Upgrade("Keks Bäcker 9", 500_000, 10_000),
            new Upgrade("Keks Bäcker 10", 1_000_000, 50_000)
    };
    private final Cookie[] cookies = new Cookie[500];
    private boolean mausTasteNeuGedrueckt = true;

    public CookieClicker(PApplet applet, Spieler spieler) {
        super(applet);

        for (int i = 0; i < cookies.length; i++) {
            cookies[i] = new Cookie(applet);
        }

        if (Files.exists(applet.sketchFile("cookie_clicker/save.txt").toPath())) {
            String[] speicherStand = applet.loadStrings("cookie_clicker/save.txt");
            kekse = Long.parseLong(speicherStand[0]);
        }
    }

    public Optional<Ergebnis> draw() {
        hintergrund();
        keksRegen();
        keks();
        displayUpgrades();

        if (applet.frameCount % 60 == 0) {
            kekse += kekseProSekunde;
            applet.saveStrings("cookie_clicker/save.txt", new String[]{ Long.toString(kekse) });
        }

        return Optional.empty();
    }

    @Override
    public void mouseReleased() {
        if (applet.mouseButton == PConstants.LEFT) {
            mausTasteNeuGedrueckt = true;
        }
    }

    private boolean knopf(float x, float y, float breite, float hoehe, int r, int g, int b) {
        applet.rectMode(PConstants.CORNER);
        applet.fill(r, g, b);
        applet.rect(x, y, breite, hoehe);
        if (applet.mousePressed && mausTasteNeuGedrueckt) {
            if (applet.mouseX >= x && applet.mouseX <= x + breite) {
                if (applet.mouseY >= y && applet.mouseY <= y + hoehe) {
                    mausTasteNeuGedrueckt = false;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean knopf(float x, float y, float breite, float hoehe, PImage bild) {
        applet.imageMode(PConstants.CORNER);
        applet.image(bild, x, y, breite, hoehe);
        if (applet.mousePressed && mausTasteNeuGedrueckt) {
            if (applet.mouseX >= x && applet.mouseX <= x + breite) {
                if (applet.mouseY >= y && applet.mouseY <= y + hoehe) {
                    mausTasteNeuGedrueckt = false;
                    return true;
                }
            }
        }
        return false;
    }

    private String numberToK(long anzahl) {
        if (anzahl < 1000) {
            return String.valueOf(anzahl);
        } else if (anzahl < 1000000) {
            return anzahl / 1000 + "K";
        } else {
            return anzahl / 1000000 + "M";
        }
    }

    private void hintergrund() {
        applet.background(50);
    }

    private void keks() {
        applet.fill(255, 255, 0);
        applet.textAlign(PConstants.LEFT);
        applet.textSize(applet.height / 20f);

        applet.text("Kekse: " + numberToK(kekse), applet.width / 20f, applet.height / 20f);
        applet.text("Kekse pro Sekunde: " + kekseProSekunde, applet.width / 20f, applet.height / 10f);

        if (knopf(applet.width / 8f, applet.height / 4f, applet.width / 4f, applet.height / 2f, keksBild)) {
            kekse++;
        }
    }

    private void displayUpgrades() {
        applet.fill(20, 20, 20);
        applet.rectMode(PConstants.CORNER);
        applet.rect(applet.width / 2f, 0, applet.width / 2f, applet.height);

        applet.textAlign(PConstants.LEFT);
        applet.textSize(applet.height / 20f);
        applet.fill(255, 255, 0);
        applet.text("Upgrades", applet.width / 2f + applet.width / 20f, applet.height / 20f);

        for (int i = 0; i < upgrades.length; i++) {
            float breite = applet.width / 2f - applet.width * 0.03f;
            float hoehe = (applet.height * 0.7f) / upgrades.length;
            float x = applet.width / 2f + applet.width * 0.015f;
            float y = applet.height * 0.2f + hoehe * i;

            if (knopf(x, y, breite, hoehe, 20, 20, 40)) {
                if (kekse >= upgrades[i].price) {
                    kekse -= upgrades[i].price;
                    upgrades[i].amount++;
                    upgrades[i].price *= upgrades[i].priceIncrease;
                    kekseProSekunde += upgrades[i].cookiesPerSecond;
                } else {
                    applet.fill(255, 0, 0);
                    applet.rect(x, y, breite, hoehe);
                }
            }

            applet.textAlign(PConstants.CENTER, PConstants.CENTER);

            applet.textSize(applet.width / 40f);
            applet.fill(255, 255, 0);
            applet.text(upgrades[i].name, x + breite / 2f, y + hoehe / 3f);

            applet.textSize(applet.width / 100f);
            applet.fill(255, 255, 0);
            applet.text(upgrades[i].getDescription(), x + breite / 2f, y + hoehe * (3f / 4f));
        }
    }

    private void keksRegen() {
        for (int i = 0; i < cookies.length; i++) {
            applet.image(keksBild, cookies[i].x, cookies[i].y, cookies[i].size, cookies[i].size);
            cookies[i].y += cookies[i].speed;

            if (cookies[i].y > applet.height) {
                cookies[i].y = 0;
                cookies[i].x = applet.random(0, applet.width / 2f);
                cookies[i].speed = applet.random(applet.height / 800f, applet.height / 200f);
                cookies[i].size = applet.random(applet.height / 80f, applet.height / 40f);
            }
        }
    }
}
