package herdergames.break_out;

import herdergames.util.Rechteck;
import herdergames.util.Steuerung;
import processing.core.PConstants;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

final class Platform extends Entity {
    private static final float BREITE = 0.1f;
    private static final float HOEHE = 0.02f;
    private static final float BEWEGUNGS_GESCHWINDIGKEIT = 0.006f;
    static final float Y = 1 - HOEHE * 2;

    private final Steuerung steuerung;
    private float x = 0.5f - BREITE / 2;

    Platform(Welt welt) {
        super(welt);
        steuerung = new Steuerung(applet, welt.spieler.id());
    }

    private void bewegen() {
        float geschwindigkeit = (welt.aktivierteUpgrades.containsKey(UpgradeType.SCHNELLER) ? 2 : 1) * BEWEGUNGS_GESCHWINDIGKEIT;
        if (steuerung.istLinksGedrueckt() && x > 0) {
            x -= geschwindigkeit;
        }
        if (steuerung.istRechtsGedrueckt() && x + BREITE < 1) {
            x += geschwindigkeit;
        }
    }

    private List<Runnable> upgradesFangen() {
        return welt.getEntites(FallendesUpgrade.class)
                .filter(fallendesUpgrade -> fallendesUpgrade.getRechteck().kollidiertMit(getRechteck()))
                .<Runnable>map(gefangenesUpgrade -> () -> {
                    gefangenesUpgrade.upgrade.enable(welt);
                    welt.entities.remove(gefangenesUpgrade);
                })
                .toList();
    }

    private List<Runnable> kanonenKugelnSpawnen() {
        if (applet.frameCount % 60 != 0) {
            return Collections.emptyList();
        }

        if (!welt.aktivierteUpgrades.containsKey(UpgradeType.KANONE)) {
            return Collections.emptyList();
        }

        return List.of(() -> welt.entities.add(new KanonenKugel(welt, x + BREITE / 2)));
    }

    @Override
    List<Runnable> draw() {
        bewegen();

        applet.rectMode(PConstants.CORNER);
        applet.noStroke();
        applet.fill(applet.color(0, 0, 255));
        applet.rect(
                (welt.getXStart() + x * welt.getBreite()) * applet.width,
                Y * applet.height,
                BREITE * welt.getBreite() * applet.width,
                HOEHE * applet.height
        );

        return Stream.of(upgradesFangen(), kanonenKugelnSpawnen()).flatMap(List::stream).toList();
    }

    @Override
    void keyPressed() {
        steuerung.keyPressed();
    }

    @Override
    void keyReleased() {
        steuerung.keyReleased();
    }

    Rechteck getRechteck() {
        return new Rechteck(x, Y, BREITE, HOEHE);
    }
}
