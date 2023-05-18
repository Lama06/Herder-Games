package herdergames.cookie_clicker;

import herdergames.spiel.EinzelspielerSpiel;
import herdergames.spiel.Spieler;
import processing.core.PApplet;
import processing.core.PConstants;

import java.util.Optional;

public final class CookieClicker extends EinzelspielerSpiel {
    private float kekse = 0;
    private float kekseProSekunde = 0;
    private boolean mausTasteNeuGedrueckt = true;
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

    public CookieClicker(PApplet applet, Spieler spieler) {
        super(applet);
    }

    public Optional<Ergebnis> draw() {
        hintergrund();
        keks();
        displayUpgrades();

        if (applet.frameCount % 60 == 0) {
            kekse += kekseProSekunde;
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

    private String numberToK(int anzahl) {
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

        applet.text("Kekse: " + numberToK((int) kekse), applet.width / 20f, applet.height / 20f);
        applet.text("Kekse pro Sekunde: " + (int) kekseProSekunde, applet.width / 20f, applet.height / 10f);

        if (knopf(applet.width / 8f, applet.height / 4f, applet.width / 4f, applet.height / 2f, 2, 20, 20)) {
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

    private static class Upgrade {
        private final String name;
        private float price;
        private int amount;
        private final int cookiesPerSecond;
        private final float priceIncrease = 1.15f;

        private Upgrade(String name, int price, int cookiesPerSecond) {
            this.name = name;
            this.price = price;
            this.cookiesPerSecond = cookiesPerSecond;
        }

        private String getDescription() {
            return name + " kostet " + (int) price + " Kekse und gibt " + cookiesPerSecond + " Kekse pro Sekunde und du hast " + amount + " davon";
        }
    }
}
