package herdergames.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KreisTest {
    @Test
    void testKollision() {
        assertTrue(new Kreis(0.5f, 0.5f, 0.2f).kollidiertMit(new Kreis(0.5f, 0.8f, 0.2f)));
        assertFalse(new Kreis(0.5f, 0.5f, 0.2f).kollidiertMit(new Kreis(0.5f, 0.8f, 0.05f)));
    }

    @Test
    void testBeruehrtBildschirmRand() {
        assertTrue(new Kreis(0.1f, 0.1f, 0.2f).beruehrtBildschirmRand());
        assertFalse(new Kreis(0.5f, 0.5f, 0.2f).beruehrtBildschirmRand());
    }

    @Test
    void testIstWegVomBildschirm() {
        assertTrue(new Kreis(-0.1f, -0.1f, 0.05f).istWegVomBildschirm());
        assertFalse(new Kreis(0.5f, 0.5f, 0.4f).istWegVomBildschirm());
    }
}
