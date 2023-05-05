package herdergames.tic_tac_toe;

import herdergames.spiel.Spiel;
import processing.core.PApplet;

import java.util.Optional;
import java.util.Set;

public final class SpielerGegenSpielerSpiel extends Spiel.SpielerGegenSpieler {
    private final herdergames.spiel.Spieler spielerKreuz;
    private final herdergames.spiel.Spieler spielerKreis;
    private Brett aktuellesBrett = Brett.LEER;
    private herdergames.tic_tac_toe.Spieler amZug = herdergames.tic_tac_toe.Spieler.KREUZ;

    public SpielerGegenSpielerSpiel(PApplet applet, herdergames.spiel.Spieler spieler1, herdergames.spiel.Spieler spieler2) {
        super(applet);
        if (spieler1.punkte() < spieler2.punkte()) {
            spielerKreuz = spieler1;
            spielerKreis = spieler2;
        } else {
            spielerKreuz = spieler2;
            spielerKreis = spieler1;
        }
    }

    @Override
    public void mousePressed() {
        Optional<Position> mausPosition = Position.fromMausPosition(applet);
        if (mausPosition.isEmpty()) {
            return;
        }

        Set<Zug> moeglicheZuege = aktuellesBrett.getMoeglicheZuegeFuerSpieler(amZug);
        for (Zug moeglicherZug : moeglicheZuege) {
            if (moeglicherZug.position().equals(mausPosition.get())) {
                aktuellesBrett = moeglicherZug.ergebnis();
                amZug = amZug.getGegner();
                return;
            }
        }
    }

    @Override
    public Optional<Optional<herdergames.spiel.Spieler.Id>> draw() {
        aktuellesBrett.draw(applet);

        if (aktuellesBrett.hatGewonnen(herdergames.tic_tac_toe.Spieler.KREUZ)) {
            return Optional.of(Optional.of(spielerKreuz.id()));
        }
        if (aktuellesBrett.hatGewonnen(herdergames.tic_tac_toe.Spieler.KREIS)) {
            return Optional.of(Optional.of(spielerKreis.id()));
        }
        if (aktuellesBrett.getMoeglicheZuegeFuerSpieler(amZug).isEmpty()) {
            return Optional.of(Optional.empty());
        }

        return Optional.empty();
    }
}
