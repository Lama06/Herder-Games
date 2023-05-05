package herdergames.util;

import processing.core.PApplet;

import java.util.List;
import java.util.Objects;

/**
 * Stellt Methoden zur Verfügung, um einen gewichteten Zufallswert aus einer Liste zu finden
 */
public final class GewichteteListe {
    private GewichteteListe() { }

    public static <T> T zufaellig(PApplet applet, List<? extends Eintrag<? extends T>> liste) {
        if (liste.isEmpty()) {
            throw new IllegalArgumentException();
        }

        int gewichtungGesamt = liste.stream().mapToInt(eintrag -> eintrag.gewichtung()).sum();
        int verbleibendeGewichtung = applet.choice(gewichtungGesamt);
        for (Eintrag<? extends T> eintrag : liste) {
            verbleibendeGewichtung -= eintrag.gewichtung;
            if (verbleibendeGewichtung <= 0) {
                return eintrag.wert;
            }
        }

        throw new IllegalStateException();
    }

    public record Eintrag<T>(T wert, int gewichtung) {
        public Eintrag {
            if (gewichtung <= 0) {
                throw new IllegalArgumentException();
            }

            Objects.requireNonNull(wert);
        }
    }
}
