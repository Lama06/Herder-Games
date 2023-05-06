package herdergames.schach;

import processing.core.PApplet;

import java.util.Optional;
import java.util.Set;

public final class SpielerGegenSpielerSpiel extends herdergames.spiel.SpielerGegenSpielerSpiel {
    private final herdergames.spiel.Spieler weiss;
    private final herdergames.spiel.Spieler schwarz;
    private Brett aktuellesBrett = Brett.ANFANG;
    private Optional<Position> ausgewaehltePosition = Optional.empty();
    private Spieler amZug = Spieler.WEISS;

    public SpielerGegenSpielerSpiel(PApplet applet, herdergames.spiel.Spieler spieler1, herdergames.spiel.Spieler spieler2) {
        super(applet);
        if (spieler1.punkte() < spieler2.punkte()) {
            weiss = spieler1;
            schwarz = spieler2;
        } else {
            weiss = spieler2;
            schwarz = spieler1;
        }
    }

    private void selectNewField() {
        Optional<Position> position = Position.fromMousePosition(applet);
        if (position.isEmpty()) {
            ausgewaehltePosition = Optional.empty();
            return;
        }
        Optional<Stein> stein = aktuellesBrett.getStein(position.get());
        if (stein.isEmpty() || stein.get().spieler() != amZug) {
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
                aktuellesBrett = moeglicherZug.ergebnis();
                amZug = amZug.getGegner();
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
    public Optional<Optional<herdergames.spiel.Spieler.Id>> draw() {
        aktuellesBrett.draw(applet, ausgewaehltePosition);

        if (aktuellesBrett.hatVerloren(Spieler.WEISS)) {
            return Optional.of(Optional.of(schwarz.id()));
        }
        if (aktuellesBrett.hatVerloren(Spieler.SCHWARZ)) {
            return Optional.of(Optional.of(weiss.id()));
        }

        return Optional.empty();
    }
}
