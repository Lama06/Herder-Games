package herdergames.break_out;

import herdergames.util.Rechteck;
import processing.core.PConstants;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

final class Stein extends Entity {
    static final float BREITE = 0.05f;
    static final float HOEHE = 0.03f;
    static final float ABSTAND_X = BREITE / 2;
    static final float ABSTAND_Y = HOEHE / 2;
    private static final float UPGRADE_WAHRSCHEINLICHKEIT = 0.2f;

    final float x;
    final float y;
    final Optional<UpgradeType> upgrade;

    Stein(Welt welt, float x, float y) {
        super(welt);
        this.x = x;
        this.y = y;
        if (applet.random(1) <= UPGRADE_WAHRSCHEINLICHKEIT) {
            upgrade = Optional.of(UpgradeType.zufaellig(applet));
        } else {
            upgrade = Optional.empty();
        }
    }

    @Override
    List<Runnable> draw() {
        applet.rectMode(PConstants.CORNER);
        applet.noStroke();
        applet.fill(upgrade.map(upgrade -> upgrade.getColor(applet)).orElse(applet.color(79, 6, 71)));
        applet.rect(
                (welt.getXStart() + x * welt.getBreite()) * applet.width,
                y * applet.height,
                BREITE * welt.getBreite() * applet.width,
                HOEHE * applet.height
        );

        return Collections.emptyList();
    }

    Rechteck getRechteck() {
        return new Rechteck(x, y, BREITE, HOEHE);
    }
}
