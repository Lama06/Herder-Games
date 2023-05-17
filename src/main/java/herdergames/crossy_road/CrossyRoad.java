package herdergames.crossy_road;

import herdergames.spiel.MehrspielerSpiel;
import herdergames.spiel.Spieler;
import processing.core.PApplet;
import processing.core.PConstants;

import java.util.*;

public final class CrossyRoad extends MehrspielerSpiel {
    static final int BREITE = 15;
    static final int HOEHE = 9;
    static final float SCROLL_SPEED = 0.004f;

    private final List<Huhn> huehner = new ArrayList<>();
    private final List<Spieler.Id> rangliste = new ArrayList<>();
    final List<Zeile> zeilen = new ArrayList<>();
    private float scroll = 0;

    public CrossyRoad(PApplet applet, Set<Spieler> alleSpieler) {
        super(applet);

        for (Spieler spieler : alleSpieler) {
            huehner.add(new Huhn(this, spieler));
        }

        for (int y = -1; y < HOEHE; y++) {
            if (y >= HOEHE / 2) {
                zeilen.add(new Wiese(this, y));
                continue;
            }
            zeilen.add(Zeile.zufaellig(this, y));
        }
    }

    @Override
    public Optional<List<Spieler.Id>> draw() {
        applet.background(255);

        applet.pushMatrix();

        applet.translate(getAbstandX(), getAbstandY());
        applet.translate(0, (scroll += getScrollSpeed()) * getFeldSize());
        applet.scale(getFeldSize());

        ListIterator<Zeile> zeileIterator = zeilen.listIterator();
        while (zeileIterator.hasNext()) {
            Zeile zeile = zeileIterator.next();
            if (zeile.istUntenWeg()) {
                zeileIterator.remove();
                zeileIterator.add(Zeile.zufaellig(this, zeile.y - HOEHE - 1));
                continue;
            }

            zeile.draw();
        }

        Iterator<Huhn> huehnerIterator = huehner.iterator();
        while (huehnerIterator.hasNext()) {
            Huhn huhn = huehnerIterator.next();
            if (huhn.istTot()) {
                huehnerIterator.remove();
                rangliste.add(0, huhn.spieler.id());
                continue;
            }

            huhn.draw();
        }

        applet.popMatrix();

        applet.rectMode(PConstants.CORNERS);
        applet.noStroke();
        applet.fill(0);
        applet.rect(0, 0, applet.width, getAbstandY());
        applet.rect(0, 0, getAbstandX(), applet.height);
        applet.rect(applet.width - getAbstandX(), 0, applet.width, applet.height);
        applet.rect(0, applet.height - getAbstandY(), applet.width, applet.height);

        if (huehner.isEmpty()) {
            return Optional.of(rangliste);
        }

        return Optional.empty();
    }

    @Override
    public void keyPressed() {
        huehner.forEach(Huhn::keyPressed);
    }

    private float getFeldSize() {
        return Math.min((float) applet.width / BREITE, (float) applet.height / HOEHE);
    }

    private float getAbstandX() {
        return (applet.width - getFeldSize() * BREITE) / 2f;
    }

    private float getAbstandY() {
        return (applet.height - getFeldSize() * HOEHE) / 2f;
    }

    private float getScrollSpeed() {
        if (huehner.stream().allMatch(huhn -> {
            float yStart = -scroll;
            float screenY = huhn.feldY - yStart;
            return screenY < (float) HOEHE / 2;
        })) {
            return SCROLL_SPEED * 4;
        }

        return SCROLL_SPEED;
    }

    boolean istObenWeg(float y, float hoehe) {
        float yStart = -scroll;
        return y + hoehe < yStart;
    }

    boolean istUntenWeg(float y) {
        float yEnde = HOEHE - scroll;
        return y > yEnde;
    }

    boolean istWeg(float y, float hoehe) {
        return istUntenWeg(y) || istObenWeg(y, hoehe);
    }
}
