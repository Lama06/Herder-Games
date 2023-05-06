package herdergames;

import herdergames.spiel.Spieler;

final class SpielerDaten {
    final Spieler.Id id;
    String name;
    int punkte = 0;
    boolean aktiviert;

    SpielerDaten(Spieler.Id id) {
        this.id = id;
        name = id.toString();
    }

    Spieler convert() {
        return new Spieler(id, name, punkte);
    }
}
