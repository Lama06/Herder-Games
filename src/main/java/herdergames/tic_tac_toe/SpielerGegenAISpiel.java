package herdergames.tic_tac_toe;

import herdergames.ai.AI;
import herdergames.spiel.Spiel;
import processing.core.PApplet;

import java.util.Optional;
import java.util.Set;

public final class SpielerGegenAISpiel extends Spiel.Einzelspieler {
    private static final int AI_TIEFE = 9; // Ein Tic Tac Toe Spiel ist nach spätestens 9 Zügen beendet, weil dann das Brett voll ist
    private static final herdergames.tic_tac_toe.Spieler MENSCH = herdergames.tic_tac_toe.Spieler.KREIS;
    private static final herdergames.tic_tac_toe.Spieler COMPUTER = herdergames.tic_tac_toe.Spieler.KREUZ;
    private Brett aktuellesBrett;

    public SpielerGegenAISpiel(PApplet applet, Spieler spieler) {
        super(applet);

        Optional<Zug> ersterZug = AI.bestenNaechstenZugBerechnen(Brett.LEER, COMPUTER, AI_TIEFE);
        if (ersterZug.isEmpty()) {
            // Das sollte niemals passieren, weil der Computer immer einen Zug findet, wenn er anfängt
            throw new IllegalStateException();
        }
        aktuellesBrett = ersterZug.get().ergebnis();
    }

    @Override
    public void mousePressed() {
        Optional<Position> mausPosition = Position.fromMausPosition(applet);
        if (mausPosition.isEmpty()) {
            return;
        }

        Set<Zug> moeglicheZuege = aktuellesBrett.getMoeglicheZuegeFuerSpieler(MENSCH);
        for (Zug moeglicherZug : moeglicheZuege) {
            if (moeglicherZug.position().equals(mausPosition.get())) {
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
