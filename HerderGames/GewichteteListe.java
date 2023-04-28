import processing.core.PApplet;

import java.util.List;
import java.util.Objects;

/**
 * Stellt Methoden zur Verfügung, um einen gewichteten Zufallswert aus einer Liste zu finden
 */
final class GewichteteListe {
    private GewichteteListe() { }

    static <T> T zufaellig(PApplet applet, List<? extends Eintrag<? extends T>> liste) {
        if (liste.isEmpty()) {
            throw new IllegalArgumentException();
        }

        int gewichtungGesamt = liste.stream().mapToInt(eintrag -> eintrag.gewichtung).sum();
        int verbleibendeGewichtung = applet.choice(gewichtungGesamt);
        for (Eintrag<? extends T> eintrag : liste) {
            verbleibendeGewichtung -= eintrag.gewichtung;
            if (verbleibendeGewichtung <= 0) {
                return eintrag.wert;
            }
        }

        throw new IllegalStateException();
    }

    static final class Eintrag<T> {
        final T wert;
        final int gewichtung;

        Eintrag(T wert, int gewichtung) {
            if (gewichtung <= 0) {
                throw new IllegalArgumentException();
            }

            this.wert = Objects.requireNonNull(wert);
            this.gewichtung = gewichtung;
        }
    }
}
