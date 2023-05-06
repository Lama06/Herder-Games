package herdergames.schach;

record Zug(Position von, Position nach, Brett ergebnis) implements herdergames.ai.Zug<Brett> { }
