package herdergames.vier_gewinnt;

import herdergames.spiel.Spiel;
import processing.core.PApplet;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

public final class SpielerGegenSpielerSpiel extends Spiel.SpielerGegenSpieler {
    private final herdergames.spiel.Spieler spieler1;
    private final herdergames.spiel.Spieler spieler2;
    private Brett aktuellesBrett = Brett.LEER;
    private Spieler amZug = Spieler.SPIELER_1;

    public SpielerGegenSpielerSpiel(PApplet applet, herdergames.spiel.Spieler spieler1, herdergames.spiel.Spieler spieler2) {
        super(applet);
        if (spieler1.punkte() < spieler2.punkte()) {
            this.spieler1 = spieler1;
            this.spieler2 = spieler2;
        } else {
            this.spieler1 = spieler2;
            this.spieler2 = spieler1;
        }
    }

    @Override
    public void mousePressed() {
        OptionalInt mausSpalte = Position.spalteFromMausPosition(applet);
        if (mausSpalte.isEmpty()) {
            return;
        }

        Set<Zug> moeglicheZuege = aktuellesBrett.getMoeglicheZuegeFuerSpieler(amZug);
        for (Zug moeglicherZug : moeglicheZuege) {
            if (moeglicherZug.spalte() == mausSpalte.getAsInt()) {
                aktuellesBrett = moeglicherZug.ergebnis();
                amZug = amZug.getGegner();
                return;
            }
        }
    }

    @Override
    public Optional<Optional<herdergames.spiel.Spieler.Id>> draw() {
        aktuellesBrett.draw(applet);

        if (aktuellesBrett.hatGewonnen(Spieler.SPIELER_1)) {
            return Optional.of(Optional.of(spieler1.id()));
        }
        if (aktuellesBrett.hatGewonnen(Spieler.SPIELER_2)) {
            return Optional.of(Optional.of(spieler2.id()));
        }
        if (aktuellesBrett.getMoeglicheZuegeFuerSpieler(amZug).isEmpty()) {
            return Optional.of(Optional.empty());
        }

        return Optional.empty();
    }
}
