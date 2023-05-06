package herdergames.spiel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpielerTest {
    @Test
    void testIdReihenfolge() {
        assertEquals(Spieler.Id.SPIELER_1.ordinal(), 0);
        assertEquals(Spieler.Id.SPIELER_2.ordinal(), 1);
        assertEquals(Spieler.Id.SPIELER_3.ordinal(), 2);
        assertEquals(Spieler.Id.SPIELER_4.ordinal(), 3);
    }
}