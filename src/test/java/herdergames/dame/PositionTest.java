package herdergames.dame;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {
    @Test
    void testIsValid() {
        assertTrue(Position.isValid(0, 0));
        assertTrue(Position.isValid(1, 1));
        assertTrue(Position.isValid(5, 3));
        assertTrue(Position.isValid(6, 6));
        assertTrue(Position.isValid(2, 6));
        assertTrue(Position.isValid(4, 2));

        assertFalse(Position.isValid(1, 2));
        assertFalse(Position.isValid(2, 3));
        assertFalse(Position.isValid(3, 0));
        assertFalse(Position.isValid(4, 7));
        assertFalse(Position.isValid(-1, 1));
        assertFalse(Position.isValid(8, 8));
    }
}
