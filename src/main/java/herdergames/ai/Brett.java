package herdergames.ai;

import java.util.Set;

public interface Brett<Self extends Brett<Self, Z, S>, Z extends Zug<Self>, S extends Spieler<S>> {
    Set<Z> getMoeglicheZuegeFuerSpieler(S spieler);

    int getBewertung(S perspektive);
}
