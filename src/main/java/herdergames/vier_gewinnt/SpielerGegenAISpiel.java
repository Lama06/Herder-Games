package herdergames.vier_gewinnt;

import herdergames.ai.AI;
import herdergames.spiel.Spiel;
import processing.core.PApplet;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

public final class SpielerGegenAISpiel extends Spiel.Einzelspieler {
    private static final int AI_TIEFE = 6;
    private static final Spieler MENSCH = Spieler.SPIELER_1;
    private static final Spieler COMPUTER = Spieler.SPIELER_2;

    // In der Mitte anzufangen ist in Vier Gewinnt immer eine gute Iddee.
    // Unsere AI guckt aber nicht weit genug in die Zukunft, um das zu verstehen, also geben wir ihr einen kleinen Tipp.
    private Brett aktuellesBrett = Brett.LEER.mitStein(new Position(5, 3), Optional.of(COMPUTER));

    public SpielerGegenAISpiel(PApplet applet, herdergames.spiel.Spieler spieler) {
        super(applet);
    }

    @Override
    public void mousePressed() {
        OptionalInt mausSpalte = Position.spalteFromMausPosition(applet);
        if (mausSpalte.isEmpty()) {
            return;
        }

        Set<Zug> moeglicheZuege = aktuellesBrett.getMoeglicheZuegeFuerSpieler(MENSCH);
        for (Zug moeglicherZug : moeglicheZuege) {
            if (moeglicherZug.spalte() == mausSpalte.getAsInt()) {
                Optional<Zug> antwort = AI.bestenNaechstenZugBerechnen(moeglicherZug.ergebnis(), COMPUTER, AI_TIEFE);
                if (antwort.isEmpty()) {
                    aktuellesBrett = moeglicherZug.ergebnis();
                    return;
                }
                aktuellesBrett = antwort.get().ergebnis();
                return;
            }
        }
    }

    @Override
    public Optional<Ergebnis> draw() {
        aktuellesBrett.draw(applet);

        if (aktuellesBrett.hatGewonnen(COMPUTER)) {
            return Optional.of(Ergebnis.VERLOREN);
        }
        if (aktuellesBrett.hatGewonnen(MENSCH)) {
            return Optional.of(Ergebnis.GEWONNEN);
        }

        return Optional.empty();
    }
}
