package herdergames.schach;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.util.Arrays;
import java.util.Optional;

record Stein(Spieler spieler, Figur figur) {
    static char steinZuBuchstabe(Optional<Stein> stein) {
        if (stein.isEmpty()) {
            return '_';
        }

        char buchstabe = stein.get().figur.buchstabe;
        if (stein.get().spieler == Spieler.WEISS) {
            return Character.toLowerCase(buchstabe);
        } else {
            return Character.toUpperCase(buchstabe);
        }
    }

    static Optional<Optional<Stein>> buchstabeZuStein(char buchstabe) {
        if (buchstabe == '_') {
            return Optional.of(Optional.empty());
        }

        Spieler spieler = Character.isLowerCase(buchstabe) ? Spieler.WEISS : Spieler.SCHWARZ;
        Optional<Figur> figur = Arrays.stream(Figur.values()).filter(f -> f.buchstabe == Character.toLowerCase(buchstabe)).findAny();
        return figur.map(f -> Optional.of(new Stein(spieler, f)));
    }

    private PImage getImage() {
        return switch (spieler) {
            case WEISS -> switch (figur) {
                case BAUER -> Schach.WEISS_BAUER;
                case LAEUFER -> Schach.WEISS_LAEUFER;
                case SPRINGER -> Schach.WEISS_SPRINGER;
                case TURM -> Schach.WEISS_TURM;
                case DAME -> Schach.WEISS_DAME;
                case KOENIG -> Schach.WEISS_KOENIG;
            };
            case SCHWARZ -> switch (figur) {
                case BAUER -> Schach.SCHWARZ_BAUER;
                case LAEUFER -> Schach.SCHWARZ_LAEUFER;
                case SPRINGER -> Schach.SCHWARZ_SPRINGER;
                case TURM -> Schach.SCHWARZ_TURM;
                case DAME -> Schach.SCHWARZ_DAME;
                case KOENIG -> Schach.SCHWARZ_KOENIG;
            };
        };
    }

    void draw(PApplet applet, int x, int y, int width, int height) {
        applet.imageMode(PConstants.CORNER);
        applet.image(getImage(), x, y, width, height);
    }
}
