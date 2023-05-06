package herdergames.schach;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ZuegeTest {
    private Brett brett(String... zeilen) {
        return Brett.parse(Arrays.asList(zeilen)).orElseGet(Assertions::fail);
    }

    private void testZuege(
            Brett start,
            Spieler amZug,
            Zug... zuege
    ) {
        Set<Zug> erwartet = new HashSet<>(Arrays.asList(zuege));
        Set<Zug> erhalten = start.getMoeglicheZuegeFuerSpieler(amZug);
        assertEquals(erwartet, erhalten);
    }

    @Test
    void testBauerBewegen() {
        testZuege(
                brett(
                        "________",
                        "________",
                        "________",
                        "________",
                        "________",
                        "________",
                        "BB______",
                        "________"
                ),
                Spieler.SCHWARZ,
                new Zug(
                        new Position(6, 0),
                        new Position(5, 0),
                        brett(
                                "________",
                                "________",
                                "________",
                                "________",
                                "________",
                                "B_______",
                                "_B______",
                                "________"
                        )
                ),
                new Zug(
                        new Position(6, 0),
                        new Position(4, 0),
                        brett(
                                "________",
                                "________",
                                "________",
                                "________",
                                "B_______",
                                "________",
                                "_B______",
                                "________"
                        )
                ),
                new Zug(
                        new Position(6, 1),
                        new Position(5, 1),
                        brett(
                                "________",
                                "________",
                                "________",
                                "________",
                                "________",
                                "_B______",
                                "B_______",
                                "________"
                        )
                ),
                new Zug(
                        new Position(6, 1),
                        new Position(4, 1),
                        brett(
                                "________",
                                "________",
                                "________",
                                "________",
                                "_B______",
                                "________",
                                "B_______",
                                "________"
                        )
                )
        );

        testZuege(
                brett(
                        "________",
                        "________",
                        "________",
                        "________",
                        "________",
                        "b_______",
                        "B_______",
                        "________"
                ),
                Spieler.SCHWARZ
        );

        testZuege(
                brett(
                        "________",
                        "________",
                        "________",
                        "________",
                        "________",
                        "_b______",
                        "B_______",
                        "________"
                ),
                Spieler.SCHWARZ,
                new Zug(
                        new Position(6, 0),
                        new Position(5, 1),
                        brett(
                                "________",
                                "________",
                                "________",
                                "________",
                                "________",
                                "_B______",
                                "________",
                                "________"
                        )
                )
        );
    }

    @Test
    void testSpringerZuege() {
        testZuege(
                brett(
                        "________",
                        "________",
                        "________",
                        "________",
                        "________",
                        "________",
                        "________",
                        "S_______"
                ),
                Spieler.SCHWARZ,
                new Zug(
                        new Position(7, 0),
                        new Position(5, 1),
                        brett(
                                "________",
                                "________",
                                "________",
                                "________",
                                "________",
                                "_S______",
                                "________",
                                "________"
                        )
                ),
                new Zug(
                        new Position(7, 0),
                        new Position(6, 2),
                        brett(
                                "________",
                                "________",
                                "________",
                                "________",
                                "________",
                                "________",
                                "__S_____",
                                "________"
                        )
                )
        );
    }

    @Test
    void testImSchach() {
        testZuege(
                brett(
                        "k_______",
                        "_t______",
                        "________",
                        "D_______",
                        "________",
                        "________",
                        "________",
                        "________"
                ),
                Spieler.WEISS,
                new Zug(
                        new Position(1, 1),
                        new Position(1, 0),
                        brett(
                                "k_______",
                                "t_______",
                                "________",
                                "D_______",
                                "________",
                                "________",
                                "________",
                                "________"
                        )
                ),
                new Zug(
                        new Position(0, 0),
                        new Position(0, 1),
                        brett(
                                "_k______",
                                "_t______",
                                "________",
                                "D_______",
                                "________",
                                "________",
                                "________",
                                "________"
                        )
                )
        );
    }
}
