package herdergames.break_out;

import herdergames.util.Rechteck;
import processing.core.PConstants;

import java.util.Collections;
import java.util.List;

final class FallendesUpgrade extends Entity {
    private static final float SIZE = 0.02f;
    private static final float Y_GESCHWINDIGKEIT = 0.002f;

    final UpgradeType upgrade;
    private final float x;
    private float y;

    FallendesUpgrade(Welt welt, UpgradeType upgrade, float x, float y) {
        super(welt);
        this.upgrade = upgrade;
        this.x = x;
        this.y = y;
    }

    private void bewegen() {
        y += Y_GESCHWINDIGKEIT;
    }

    @Override
    List<Runnable> draw() {
        bewegen();

        applet.ellipseMode(PConstants.CORNER);
        applet.noStroke();
        applet.fill(upgrade.getColor(applet));
        float size = Math.min(SIZE * welt.getBreite() * applet.width, SIZE * applet.height);
        applet.circle(
                (welt.getXStart() + x * welt.getBreite()) * applet.width,
                y * applet.height,
                size
        );

        if (y >= 1) {
            return List.of(() -> welt.entities.remove(this));
        }
        return Collections.emptyList();
    }

    Rechteck getRechteck() {
        return new Rechteck(x, y, SIZE, SIZE);
    }
}
