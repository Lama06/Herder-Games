package herdergames.spiel;

import java.util.Objects;

public record Spieler(Spieler.Id id, String name, int punkte) {
    public Spieler {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);

        if (punkte < 0) {
            throw new IllegalArgumentException();
        }
    }

    public enum Id {
        // Reihenfolge ist wichtig, weil Spiele ordinal() verwenden
        SPIELER_1,
        SPIELER_2,
        SPIELER_3,
        SPIELER_4;

        @Override
        public String toString() {
            return switch (this) {
                case SPIELER_1 -> "Spieler 1";
                case SPIELER_2 -> "Spieler 2";
                case SPIELER_3 -> "Spieler 3";
                case SPIELER_4 -> "Spieler 4";
            };
        }
    }
}
