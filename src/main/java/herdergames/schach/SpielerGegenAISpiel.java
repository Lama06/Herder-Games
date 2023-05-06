package herdergames.schach;

import herdergames.ai.AI;
import herdergames.spiel.EinzelspielerSpiel;
import processing.core.PApplet;

import java.util.Optional;
import java.util.Set;

public final class SpielerGegenAISpiel extends EinzelspielerSpiel {
    private static final int AI_DEPTH = 2;

    private static final Spieler COMPUTER = Spieler.WEISS;
    private static final Spieler MENSCH = Spieler.SCHWARZ;

    private Brett aktuellesBrett = Brett.ANFANG;
    private Optional<Position> ausgewaehltePosition = Optional.empty();

    public SpielerGegenAISpiel(PApplet applet, herdergames.spiel.Spieler spieler) {
        super(applet);
    }

    private void selectNewField() {
        Optional<Position> position = Position.fromMousePosition(applet);
        if (position.isEmpty()) {
            ausgewaehltePosition = Optional.empty();
            return;
        }
        Optional<Stein> stein = aktuellesBrett.getStein(position.get());
        if (stein.isEmpty() || stein.get().spieler() != MENSCH) {
            ausgewaehltePosition = Optional.empty();
            return;
        }

        ausgewaehltePosition = position;
    }

    private void zugMachen() {
        if (ausgewaehltePosition.isEmpty()) {
            return;
        }

        Optional<Position> position = Position.fromMousePosition(applet);
        if (position.isEmpty()) {
            return;
        }

        Set<Zug> moeglicheZuege = aktuellesBrett.getMoeglicheZuegeFuerPosition(ausgewaehltePosition.get());
        for (Zug moeglicherZug : moeglicheZuege) {
            if (moeglicherZug.nach().equals(position.get())) {
                Optional<Zug> antwort = AI.bestenNaechstenZugBerechnen(moeglicherZug.ergebnis(), COMPUTER, AI_DEPTH);
                if (antwort.isEmpty()) {
                    aktuellesBrett = moeglicherZug.ergebnis();
                    ausgewaehltePosition = Optional.empty();
                    return;
                }

                aktuellesBrett = antwort.get().ergebnis();
                ausgewaehltePosition = Optional.empty();
                return;
            }
        }
    }

    @Override
    public void mousePressed() {
        zugMachen();
        selectNewField();
    }

    @Override
    public Optional<Ergebnis> draw() {
        aktuellesBrett.draw(applet, ausgewaehltePosition);

        if (aktuellesBrett.hatVerloren(MENSCH)) {
            return Optional.of(Ergebnis.VERLOREN);
        }
        if (aktuellesBrett.hatVerloren(COMPUTER)) {
            return Optional.of(Ergebnis.GEWONNEN);
        }

        return Optional.empty();
    }
}
