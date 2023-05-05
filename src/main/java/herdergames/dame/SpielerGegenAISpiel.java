package herdergames.dame;

import herdergames.ai.AI;
import herdergames.spiel.Spiel;
import processing.core.PApplet;

import java.util.*;

public final class SpielerGegenAISpiel extends Spiel.Einzelspieler {
    private static final herdergames.dame.Spieler COMPUTER = herdergames.dame.Spieler.SPIELER_OBEN;
    private static final herdergames.dame.Spieler MENSCH = herdergames.dame.Spieler.SPIELER_UNTEN;
    private static final int AI_DEPTH = 6;
    private static final int AI_ZUG_SCHRITT_DELAY = 60;

    private Brett aktuellesBrett = Brett.ANFANG;
    private Optional<Position> ausgewaehltePosition = Optional.empty();

    private List<Brett> verbleibendeAiZugSchritte = Collections.emptyList();
    private int nextAiZugSchritt;

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
        if (stein.isEmpty() || stein.get().getSpieler() != MENSCH) {
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
                ausgewaehltePosition = Optional.empty();

                Optional<Zug> antwort = AI.bestenNaechstenZugBerechnen(moeglicherZug.ergebnis(), COMPUTER, AI_DEPTH);
                if (antwort.isEmpty()) {
                    aktuellesBrett = moeglicherZug.ergebnis();
                    return;
                }

                if (antwort.get().schritte().size() == 1) {
                    aktuellesBrett = antwort.get().ergebnis();
                    return;
                }

                aktuellesBrett = antwort.get().schritte().get(0);

                List<Brett> verbleibendeAiZugSchritte = new ArrayList<>(antwort.get().schritte());
                verbleibendeAiZugSchritte.remove(0);
                this.verbleibendeAiZugSchritte = verbleibendeAiZugSchritte;

                nextAiZugSchritt = AI_ZUG_SCHRITT_DELAY;

                return;
            }
        }
    }

    @Override
    public void mousePressed() {
        if (!verbleibendeAiZugSchritte.isEmpty()) {
            return;
        }

        zugMachen();

        if (!verbleibendeAiZugSchritte.isEmpty()) {
            return;
        }

        selectNewField();
    }

    private void aiZugSchritteMachen() {
        if (verbleibendeAiZugSchritte.isEmpty()) {
            return;
        }

        if (nextAiZugSchritt > 0) {
            nextAiZugSchritt--;
            return;
        }

        Brett schritt = verbleibendeAiZugSchritte.remove(0);
        aktuellesBrett = schritt;
        nextAiZugSchritt = AI_ZUG_SCHRITT_DELAY;
    }

    @Override
    public Optional<Ergebnis> draw() {
        aiZugSchritteMachen();

        aktuellesBrett.draw(applet, ausgewaehltePosition);

        if (!verbleibendeAiZugSchritte.isEmpty()) {
            return Optional.empty();
        }

        if (aktuellesBrett.hatVerloren(MENSCH)) {
            return Optional.of(Ergebnis.VERLOREN);
        }
        if (aktuellesBrett.hatVerloren(COMPUTER)) {
            return Optional.of(Ergebnis.GEWONNEN);
        }

        return Optional.empty();
    }
}
