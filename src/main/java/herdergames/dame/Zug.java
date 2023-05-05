package herdergames.dame;

import java.util.List;
import java.util.Objects;

record Zug(Position von, Position nach, List<Brett> schritte) implements herdergames.ai.Zug<Brett> {
    Zug(Position von, Position nach, List<Brett> schritte) {
        if (schritte.isEmpty()) {
            throw new IllegalArgumentException();
        }

        this.von = Objects.requireNonNull(von);
        this.nach = Objects.requireNonNull(nach);
        this.schritte = List.copyOf(schritte);
    }

    @Override
    public Brett ergebnis() {
        return schritte.get(schritte.size() - 1);
    }
}
