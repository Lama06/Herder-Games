package herdergames.break_out;

import herdergames.util.Rechteck;
import processing.core.PConstants;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

final class Ball extends Entity {
    private static final float SIZE = 0.02f;
    private static final float GESCHWINDIGKEIT_Y = 0.006f;
    private static final float MAX_GESCHWINDIGKEIT_X = 0.004f;

    private float geschwindigkeitY = GESCHWINDIGKEIT_Y;
    private float geschwindigkeitX = 0;
    private float x = 0.5f;
    private float y = 0.5f;

    Ball(Welt welt) {
        super(welt);
    }

    boolean istUntenRaus() {
        return y >= 1;
    }

    private boolean kollidiertMitRandOben() {
        return y <= 0;
    }

    private boolean kollidiertMitRandLinksRechts() {
        return x <= 0 || x + SIZE >= 1;
    }

    private boolean kollidiertMitRand() {
        return kollidiertMitRandOben() || kollidiertMitRandLinksRechts();
    }

    private Optional<Platform> getKollidierendePlatform() {
        return welt.getEntites(Platform.class)
                .filter((platform) -> platform.getRechteck().kollidiertMit(getRechteck()))
                .findAny();
    }

    private Optional<Stein> getKollidierendenStein() {
        return welt.getEntites(Stein.class)
                .filter(stein -> stein.getRechteck().kollidiertMit(getRechteck()))
                .findAny();
    }

    private void vonRandAbprallen() {
        if (kollidiertMitRandOben()) {
            geschwindigkeitY *= -1;
        }
        if (kollidiertMitRandLinksRechts()) {
            geschwindigkeitX *= -1;
        }
    }

    private void vonPlatformAbprallen(Platform platform) {
        Rechteck platformRechteck = platform.getRechteck();

        float platformXMitte = platformRechteck.getXMitte();
        float neueGeschwindigkeitX = MAX_GESCHWINDIGKEIT_X * (Math.abs(getRechteck().getXMitte() - platformXMitte) / (platformRechteck.breite() / 2));
        if (getRechteck().getXMitte() < platformXMitte) {
            neueGeschwindigkeitX *= -1;
        }

        geschwindigkeitX = neueGeschwindigkeitX;
        geschwindigkeitY *= -1;
    }

    private List<Runnable> vonSteinAbprallen(Stein stein) {
        geschwindigkeitY *= -1;

        welt.partikelManager.spawnPartikel(
                welt.getXStart() + x * welt.getBreite(),
                y,
                50
        );

        return List.of(() -> {
            welt.entities.remove(stein);
            stein.upgrade.ifPresent(upgradeType -> welt.entities.add(new FallendesUpgrade(welt, upgradeType, stein.x, stein.y)));
        });
    }

    private List<Runnable> bewegen() {
        boolean kollidiertMitRandVorher = kollidiertMitRand();
        Optional<Platform> kollidierendePlatformVorher = getKollidierendePlatform();
        Optional<Stein> kollidierenderSteinVorher = getKollidierendenStein();

        x += geschwindigkeitX;
        y += geschwindigkeitY;

        boolean kollidiertMitRandNachher = kollidiertMitRand();
        Optional<Platform> kollidierendePlatformNachher = getKollidierendePlatform();
        Optional<Stein> kollidierenderSteinNachher = getKollidierendenStein();

        if (!kollidiertMitRandVorher && kollidiertMitRandNachher) {
            vonRandAbprallen();
        }

        if (kollidierendePlatformVorher.isEmpty() && kollidierendePlatformNachher.isPresent()) {
            vonPlatformAbprallen(kollidierendePlatformNachher.get());
        }

        if (kollidierenderSteinVorher.isEmpty() && kollidierenderSteinNachher.isPresent()) {
            return vonSteinAbprallen(kollidierenderSteinNachher.get());
        }

        return Collections.emptyList();
    }

    @Override
    List<Runnable> draw() {
        applet.ellipseMode(PConstants.CORNER);
        applet.noStroke();
        applet.fill(applet.color(255, 0, 0));
        float size = Math.min(SIZE * welt.getBreite() * applet.width, SIZE * applet.height);
        applet.circle(
                (welt.getXStart() + x * welt.getBreite()) * applet.width,
                y * applet.height,
                size
        );

        return bewegen();
    }

    private Rechteck getRechteck() {
        return new Rechteck(x, y, SIZE, SIZE);
    }
}
