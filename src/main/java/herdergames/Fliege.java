package herdergames;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Optional;

final class Fliege {
    private static final String BILD_DATEI = "fliege.png";
    private static final int UNGLUECKSZAHL = 13; // o:
    private static final float GROESSE = 0.04f;

    private final PApplet applet;
    private final PImage bild;

    private Optional<Position> position = Optional.empty();

    Fliege(PApplet applet) {
        this.applet = applet;
        bild = applet.loadImage(BILD_DATEI);
    }

    void draw() {
        if (!sollFliegeAngezeigtWerden()) {
            position = Optional.empty();
            return;
        }

        if (position.isEmpty()) {
            position = Optional.of(Position.zufaellig(applet));
        }

        float groesse = Math.min(GROESSE * applet.width, GROESSE * applet.height);
        applet.imageMode(PConstants.CORNER);
        applet.image(bild, position.get().x() * applet.width, position.get().y() * applet.height, groesse, groesse);
    }

    private boolean sollFliegeAngezeigtWerden() {
        LocalDateTime zeit = LocalDateTime.now();
        int minute = zeit.get(ChronoField.MINUTE_OF_HOUR);
        int sekunde = zeit.get(ChronoField.SECOND_OF_MINUTE);
        return sekunde <= UNGLUECKSZAHL && minute % UNGLUECKSZAHL == 0;
    }

    private record Position(float x, float y) {
        private static Position zufaellig(PApplet applet) {
            return new Position(applet.random(GROESSE, 1-GROESSE), applet.random(GROESSE, 1-GROESSE));
        }
    }
}
