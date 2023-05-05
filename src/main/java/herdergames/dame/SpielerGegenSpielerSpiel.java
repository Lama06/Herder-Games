package herdergames.dame;

import herdergames.spiel.Spiel;
import processing.core.PApplet;

import java.util.Optional;
import java.util.Set;

public final class SpielerGegenSpielerSpiel extends Spiel.SpielerGegenSpieler {
    private final herdergames.spiel.Spieler spielerOben;
    private final herdergames.spiel.Spieler spielerUnten;
    private Brett aktuellesBrett = Brett.ANFANG;
    private Optional<Position> ausgewaehltePosition = Optional.empty();
    private herdergames.dame.Spieler amZug = herdergames.dame.Spieler.SPIELER_UNTEN;

    public SpielerGegenSpielerSpiel(PApplet applet, herdergames.spiel.Spieler spieler1, herdergames.spiel.Spieler spieler2) {
        super(applet);
        if (spieler1.punkte() < spieler2.punkte()) {
            spielerUnten = spieler1;
            spielerOben = spieler2;
        } else {
            spielerUnten = spieler2;
            spielerOben = spieler1;
        }
    }

    private void selectNewField() {
        Optional<Position> position = Position.fromMousePosition(applet);
        if (position.isEmpty()) {
            ausgewaehltePosition = Optional.empty();
            return;
        }
        Optional<Stein> stein = aktuellesBrett.getStein(position.get());
        if (stein.isEmpty() || stein.get().getSpieler() != amZug) {
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

        if (aktuellesBrett.hatVerloren(herdergames.dame.Spieler.SPIELER_OBEN)) {
            return Optional.of(Optional.of(spielerUnten.id()));
        }
        if (aktuellesBrett.hatVerloren(herdergames.dame.Spieler.SPIELER_UNTEN)) {
            return Optional.of(Optional.of(spielerOben.id()));
        }
        return Optional.empty();
    }
}
