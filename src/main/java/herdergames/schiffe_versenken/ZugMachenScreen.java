package herdergames.schiffe_versenken;

import herdergames.spiel.Spieler;

import java.util.Optional;

final class ZugMachenScreen extends Screen {
    private static final float BRETTER_ABSTAND_X = 0.05f;

    private final Spieler spieler;

    ZugMachenScreen(SchiffeVersenken spiel, Spieler spieler) {
        super(spiel);
        this.spieler = spieler;
    }

    @Override
    void draw() {
        SpielBrett eigenesBrett = getEigenesBrett();
        eigenesBrett.draw(applet, getEigenesBrettX(), getEigenesBrettY(), getFeldSize(), false);

        SpielBrett gegnerBrett = getGegnerBrett();
        gegnerBrett.draw(applet, getGegnerBrettX(), getGegnerBrettY(), getFeldSize(), true);
    }

    @Override
    void mousePressed() {
        SpielBrett gegnerBrett = getGegnerBrett();
        SpielBrett.MausPressedErgebnis ergebnis = gegnerBrett.handleGegnerMausPressed(
                applet,
                applet.mouseX - getGegnerBrettX(),
                applet.mouseY - getGegnerBrettY(),
                getFeldSize()
        );
        setGegnerBrett(ergebnis.neuesBrett());
        if (ergebnis.zugBeendet()) {
            spiel.openScreen(new WegGuckenScreen(
                    spiel,
                    spieler,
                    () -> new ZugMachenScreen(spiel, getGegner())
            ));
        }
    }

    private float getFeldSize() {
        return Math.min(
                ((1f-BRETTER_ABSTAND_X) * applet.width) / (SpielBrett.SIZE * 2f),
                (float) applet.height / SpielBrett.SIZE
        );
    }

    private float getBrettSize() {
        return getFeldSize() * SpielBrett.SIZE;
    }

    private float getBreiteGesamt() {
        return getBrettSize() * 2 + BRETTER_ABSTAND_X * applet.width;
    }

    private float getAbstandX() {
        return (applet.width - getBreiteGesamt()) / 2;
    }

    private float getAbstandY() {
        return (applet.height - getBrettSize()) / 2;
    }

    private float getEigenesBrettX() {
        return getAbstandX();
    }

    private float getEigenesBrettY() {
        return getAbstandY();
    }

    private float getGegnerBrettX() {
        return getEigenesBrettX() + getBrettSize() + BRETTER_ABSTAND_X * applet.width;
    }

    private float getGegnerBrettY() {
        return getAbstandY();
    }

    private SpielBrett getEigenesBrett() {
        if (spieler == spiel.spieler1) {
            return spiel.spieler1SpielBrett.orElseThrow();
        } else {
            return spiel.spieler2SpielBrett.orElseThrow();
        }
    }

    private SpielBrett getGegnerBrett() {
        if (spieler == spiel.spieler1) {
            return spiel.spieler2SpielBrett.orElseThrow();
        } else {
            return spiel.spieler1SpielBrett.orElseThrow();
        }
    }

    private void setGegnerBrett(SpielBrett neuesBrett) {
        if (spieler == spiel.spieler1) {
            spiel.spieler2SpielBrett = Optional.of(neuesBrett);
        } else {
            spiel.spieler1SpielBrett = Optional.of(neuesBrett);
        }
    }

    private Spieler getGegner() {
        if (spieler == spiel.spieler1) {
            return spiel.spieler2;
        } else {
            return spiel.spieler1;
        }
    }
}
