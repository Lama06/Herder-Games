package herdergames;

import herdergames.spiel.*;
import processing.core.PConstants;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

final class SpielScreen extends Screen {
    private final SpielDaten spielDaten;
    private final Spiel spiel;

    SpielScreen(HerderGames herderGames, SpielDaten spielDaten) {
        super(herderGames);
        this.spielDaten = spielDaten;

        List<SpielerDaten> aktivierteSpielerDaten = herderGames.getAktivierteSpielerDaten();

        if (spielDaten.factory() instanceof EinzelspielerSpiel.Factory einzelspielerFactory) {
            if (aktivierteSpielerDaten.size() != 1) {
                SpielerDaten spieler1Daten = herderGames.getSpielerDaten().get(Spieler.Id.SPIELER_1);
                spieler1Daten.aktiviert = true;
                spiel = einzelspielerFactory.neuesSpiel(applet, spieler1Daten.convert());
                return;
            }
            Spieler spieler = aktivierteSpielerDaten.get(0).convert();
            spiel = einzelspielerFactory.neuesSpiel(applet, spieler);
        } else if (spielDaten.factory() instanceof SpielerGegenSpielerSpiel.Factory spielerGegenSpielerFactory) {
            if (aktivierteSpielerDaten.size() != 2) {
                SpielerDaten spieler1Daten = herderGames.getSpielerDaten().get(Spieler.Id.SPIELER_1);
                spieler1Daten.aktiviert = true;
                SpielerDaten spieler2Daten = herderGames.getSpielerDaten().get(Spieler.Id.SPIELER_2);
                spieler2Daten.aktiviert = true;
                spiel = spielerGegenSpielerFactory.neuesSpiel(applet, spieler1Daten.convert(), spieler2Daten.convert());
                return;
            }
            Spieler spieler1 = aktivierteSpielerDaten.get(0).convert();
            Spieler spieler2 = aktivierteSpielerDaten.get(1).convert();
            spiel = spielerGegenSpielerFactory.neuesSpiel(applet, spieler1, spieler2);
        } else {
            MehrspielerSpiel.Factory mehrspielerFactory = (MehrspielerSpiel.Factory) spielDaten.factory();
            if (aktivierteSpielerDaten.isEmpty()) {
                SpielerDaten spieler1Daten = herderGames.getSpielerDaten().get(Spieler.Id.SPIELER_1);
                spieler1Daten.aktiviert = true;
                SpielerDaten spieler2Daten = herderGames.getSpielerDaten().get(Spieler.Id.SPIELER_2);
                spieler2Daten.aktiviert = true;
                spiel = mehrspielerFactory.neuesSpiel(applet, Set.of(spieler1Daten.convert(), spieler2Daten.convert()));
                return;
            }
            Set<Spieler> aktivierteSpieler = new HashSet<>();
            for (SpielerDaten aktivierterSpielerDaten : aktivierteSpielerDaten) {
                aktivierteSpieler.add(aktivierterSpielerDaten.convert());
            }
            spiel = mehrspielerFactory.neuesSpiel(applet, aktivierteSpieler);
        }
    }

    @Override
    void keyPressed() {
        if (applet.key == PConstants.ESC) {
            applet.key = 0;
            herderGames.openScreen(new UebergangVonSpielScreen(herderGames, spielDaten));
            return;
        }

        if (applet.key == PConstants.DELETE) {
            herderGames.openScreen(new SpielScreen(herderGames, spielDaten));
            return;
        }

        spiel.keyPressed();
    }

    @Override
    void keyReleased() {
        spiel.keyReleased();
    }

    @Override
    void mousePressed() {
        spiel.mousePressed();
    }

    @Override
    void mouseReleased() {
        spiel.mouseReleased();
    }

    private void drawEinzelspielerSpiel() {
        EinzelspielerSpiel einzelspielerSpiel = (EinzelspielerSpiel) spiel;
        Optional<EinzelspielerSpiel.Ergebnis> ergebnis = einzelspielerSpiel.draw();
        if (ergebnis.isEmpty()) {
            return;
        }

        herderGames.openScreen(new SpielBeendetScreen(herderGames, spielDaten));

        if (herderGames.getAktivierteSpielerDaten().isEmpty()) {
            throw new IllegalStateException();
        }
        SpielerDaten spielerDaten = herderGames.getAktivierteSpielerDaten().get(0);

        spielerDaten.punkte += ergebnis.get().punkte;
    }

    private void drawSpielerGegenSpielerSpiel() {
        SpielerGegenSpielerSpiel spielerGegenSpielerSpiel = (SpielerGegenSpielerSpiel) spiel;
        Optional<Optional<Spieler.Id>> result = spielerGegenSpielerSpiel.draw();
        if (result.isEmpty()) {
            return;
        }

        herderGames.openScreen(new SpielBeendetScreen(herderGames, spielDaten));

        Optional<Spieler.Id> gewinnerId = result.get();
        if (gewinnerId.isEmpty()) {
            return;
        }
        SpielerDaten gewinnerSpielerDaten = herderGames.getSpielerDaten().get(gewinnerId.get());
        gewinnerSpielerDaten.punkte++;
    }

    private void drawMehrspielerSpiel() {
        MehrspielerSpiel mehspielerSpiel = (MehrspielerSpiel) spiel;
        Optional<List<Spieler.Id>> rangliste = mehspielerSpiel.draw();
        if (rangliste.isEmpty()) {
            return;
        }

        herderGames.openScreen(new SpielBeendetScreen(herderGames, spielDaten));

        int punkte = rangliste.get().size() - 1;
        for (int i = 0; i < rangliste.get().size(); i++, punkte--) {
            Spieler.Id spielerId = rangliste.get().get(i);
            SpielerDaten spielerDaten = herderGames.getSpielerDaten().get(spielerId);
            spielerDaten.punkte += punkte;
        }
    }

    @Override
    void draw() {
        if (spiel instanceof EinzelspielerSpiel) {
            drawEinzelspielerSpiel();
        } else if (spiel instanceof SpielerGegenSpielerSpiel) {
            drawSpielerGegenSpielerSpiel();
        } else {
            drawMehrspielerSpiel();
        }
    }
}
