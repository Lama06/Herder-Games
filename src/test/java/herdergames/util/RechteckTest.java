package herdergames.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RechteckTest {
    @Test
    void testKollision() {
        assertTrue(new Rechteck(0, 0, 0.2f, 0.2f).kollidiertMit(new Rechteck(0.15f, 0.15f, 0.3f, 0.2f)));
        assertTrue(new Rechteck(0.15f, 0.15f, 0.3f, 0.2f).kollidiertMit(new Rechteck(0, 0, 0.2f, 0.2f)));
        assertFalse(new Rechteck(0, 0, 0.2f, 0.2f).kollidiertMit(new Rechteck(0.3f, 0.3f, 0.5f, 0.5f)));
    }

    @Test
    void testBeruehrtBildschirmRand() {
        assertTrue(new Rechteck(-0.1f, -0.1f, 0.5f, 0.5f).beruehrtBildschirmRand());
        assertTrue(new Rechteck(0.9f, 0.5f, 0.2f, 0.1f).beruehrtBildschirmRand());
        assertFalse(new Rechteck(0.5f, 0.5f, 0.2f, 0.2f).beruehrtBildschirmRand());
    }

    @Test
    void testIstWegVomBildschirm() {
        assertTrue(new Rechteck(-0.1f, -0.1f, 0.05f, 0.05f).istWegVomBildschirm());
        assertFalse(new Rechteck(-0.1f, -0.1f, 0.2f, 0.2f).istWegVomBildschirm());
    }

    @Test
    void testGetXMitte() {
        assertEquals(new Rechteck(0, 0, 0.5f, 0.5f).getXMitte(), 0.25f);
    }

    @Test
    void testGetYMitte() {
        assertEquals(new Rechteck(0, 0, 0.5f, 0.5f).getYMitte(), 0.25f);
    }
}
