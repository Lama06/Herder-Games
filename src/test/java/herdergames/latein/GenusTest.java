package herdergames.latein;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GenusTest {
    @Test
    void testParse() {
        assertEquals(Genus.parse('m'), Optional.of(Genus.MASKULINUM));
        assertEquals(Genus.parse('f'), Optional.of(Genus.FEMININUM));
        assertEquals(Genus.parse('n'), Optional.of(Genus.NEUTRUM));
        assertEquals(Genus.parse('w'), Optional.empty());
    }
}
