package herdergames.break_out;

import herdergames.util.Rechteck;
import processing.core.PConstants;

import java.util.Collections;
import java.util.List;

final class KanonenKugel extends Entity {
    private static final float SIZE = 0.02f;
    private static final float Y_GESCHWINDIGKEIT = -0.015f;

    private final float x;
    private float y = Platform.Y;

    KanonenKugel(Welt welt, float x) {
        super(welt);
        this.x = x;
    }

    @Override
    List<Runnable> draw() {
        y += Y_GESCHWINDIGKEIT;

        applet.ellipseMode(PConstants.CORNER);
        applet.noStroke();
        applet.fill(0);
        float size = Math.min(SIZE * welt.getBreite() * applet.width, SIZE * applet.height);
        applet.circle(
                (welt.getXStart() + x * welt.getBreite()) * applet.width,
                y * applet.height,
                size
        );

        return welt.getEntites(Stein.class)
                .filter(stein -> stein.getRechteck().kollidiertMit(getRechteck()))
                .findAny()
                .map(stein -> List.<Runnable>of(() -> {
                    welt.entities.remove(stein);
                    welt.entities.remove(this);
                }))
                .orElse(Collections.emptyList());
    }

    private Rechteck getRechteck() {
        return new Rechteck(x, y, SIZE, SIZE);
    }
}
