package herdergames.ai;

import java.util.*;

/*
Eine Implementiertung des Minimax-Algorithmuses
https://de.wikipedia.org/wiki/Minimax-Algorithmus
https://www.youtube.com/watch?v=l-hh51ncgD
*/

public final class AI {
    /**
     * Bewertet ein Spielbrett
     * @param brett Das Spielbrett
     * @param perspektive Die Perspektive, aus der das Spielbrett bewertet werden soll
     * @param spielerAmZug Der Spieler, der beim gegebenen Spielbrett am Zug ist
     * @param maximaleTiefe Wie viele folgende Züge berücksichtigt werden sollen
     * @return Bewertung des Spielbretts
     */
    private static <B extends Brett<B, Z, S>, Z extends Zug<B>, S extends Spieler<S>> int spielbrettRekursivBewerten(B brett, S perspektive, S spielerAmZug, int maximaleTiefe) {
        if (maximaleTiefe < 0) {
            throw new IllegalArgumentException();
        }

        if (maximaleTiefe == 0) {
            return brett.getBewertung(perspektive);
        }

        Set<Z> folgendeZuege = brett.getMoeglicheZuegeFuerSpieler(spielerAmZug);
        if (folgendeZuege.isEmpty()) {
            // Es ist kein weiterer Zug mehr möglich, das Spiel ist also beendet.
            return brett.getBewertung(perspektive);
        }

        int besterFolgenderZugBewertung = 0;
        boolean first = true;
        for (Z folgenderZug : folgendeZuege) {
            int folgenderZugBewertung = spielbrettRekursivBewerten(folgenderZug.getErgebnis(), perspektive, spielerAmZug.getGegner(), maximaleTiefe-1);
            if (first) {
                besterFolgenderZugBewertung = folgenderZugBewertung;
                first = false;
                continue;
            }
            if (spielerAmZug == perspektive) {
                besterFolgenderZugBewertung = Math.max(besterFolgenderZugBewertung, folgenderZugBewertung);
            } else {
                besterFolgenderZugBewertung = Math.min(besterFolgenderZugBewertung, folgenderZugBewertung);
            }
        }
        return besterFolgenderZugBewertung;
    }

    public static <B extends Brett<B, Z, S>, Z extends Zug<B>, S extends Spieler<S>> Optional<Z> bestenNaechstenZugBerechnen(B brett, S spieler, int maximaleTiefe) {
        if (maximaleTiefe <= 0) {
            throw new IllegalArgumentException();
        }

        Set<Z> moeglicheZuege = brett.getMoeglicheZuegeFuerSpieler(spieler);
        if (moeglicheZuege.isEmpty()) {
            return Optional.empty();
        }

        Z besterZug = null;
        int besterZugBewertung = 0;
        for (Z moeglicherZug : moeglicheZuege) {
            int zugErgebnisBewertung = spielbrettRekursivBewerten(moeglicherZug.getErgebnis(), spieler, spieler.getGegner(), maximaleTiefe-1);
            if (besterZug == null) {
                besterZug = moeglicherZug;
                besterZugBewertung = zugErgebnisBewertung;
                continue;
            }

            if (zugErgebnisBewertung > besterZugBewertung) {
                besterZug = moeglicherZug;
                besterZugBewertung = zugErgebnisBewertung;
            }
        }

        return Optional.of(besterZug);
    }
}
