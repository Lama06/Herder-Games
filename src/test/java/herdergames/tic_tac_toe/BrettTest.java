package herdergames.tic_tac_toe;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

final class BrettTest {
    @Test
    void testGewonnen() {
        Brett brett1 = new Brett(List.of(
                List.of(Optional.of(Spieler.KREUZ), Optional.of(Spieler.KREIS), Optional.of(Spieler.KREUZ)),
                List.of(Optional.of(Spieler.KREIS), Optional.of(Spieler.KREUZ), Optional.of(Spieler.KREIS)),
                List.of(Optional.of(Spieler.KREUZ), Optional.of(Spieler.KREIS), Optional.of(Spieler.KREUZ))
        ));
        assertTrue(brett1.hatGewonnen(Spieler.KREUZ));
        assertFalse(brett1.hatGewonnen(Spieler.KREIS));
    }
}
