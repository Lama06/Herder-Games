package herdergames.dame;

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

    private Zug zug(Position von, Position nach, Brett... schritte) {
        return new Zug(von, nach, Arrays.asList(schritte));
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
    void testSteinBewegen() {
        testZuege(
                brett(
                        "_ _ a _ ",
                        " _ _ A _",
                        "_ _ _ _ ",
                        " _ _ _ _",
                        "_ _ _ _ ",
                        " _ _ _ _",
                        "_ _ _ _ ",
                        " b _ _ _"
                ),
                Spieler.SPIELER_UNTEN,
                zug(
                        new Position(7, 1),
                        new Position(6, 2),
                        brett(
                                "_ _ a _ ",
                                " _ _ A _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ b _ _ ",
                                " _ _ _ _"
                        )
                ),
                zug(
                        new Position(7, 1),
                        new Position(6, 0),
                        brett(
                                "_ _ a _ ",
                                " _ _ A _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "b _ _ _ ",
                                " _ _ _ _"
                        )
                )
        );
    }

    @Test
    void testSteinSchlagen() {
        testZuege(
                brett(
                        "_ _ _ _ ",
                        " _ _ _ _",
                        "_ _ _ _ ",
                        " _ _ _ _",
                        "_ _ A a ",
                        " _ b a _",
                        "_ _ _ a ",
                        " _ _ _ _"
                ),
                Spieler.SPIELER_UNTEN,
                zug(
                        new Position(5, 3),
                        new Position(7, 5),
                        brett(
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ b _",
                                "_ _ _ a ",
                                " _ _ a _",
                                "_ _ _ a ",
                                " _ _ _ _"
                        ),
                        brett(
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ a b",
                                "_ _ _ a ",
                                " _ _ _ _"
                        ),
                        brett(
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ a _",
                                "_ _ _ _ ",
                                " _ _ b _"
                        )
                )
        );

        testZuege(
                brett(
                        "_ _ _ _ ",
                        " _ _ _ _",
                        "_ _ a _ ",
                        " b b b _",
                        "_ _ _ _ ",
                        " _ _ _ _",
                        "_ _ _ _ ",
                        " _ _ _ _"
                ),
                Spieler.SPIELER_OBEN,
                zug(
                        new Position(2, 4),
                        new Position(4, 6),
                        brett(
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " b b _ _",
                                "_ _ _ a ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ _ _"
                        )
                ),
                zug(
                        new Position(2, 4),
                        new Position(2, 0),
                        brett(
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " b _ b _",
                                "_ a _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ _ _"
                        ),
                        brett(
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "a _ _ _ ",
                                " _ _ b _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ _ _"
                        )
                )
        );

        testZuege(
                brett(
                        "_ _ _ _ ",
                        " _ A a _",
                        "_ b _ _ ",
                        " _ _ _ _",
                        "_ _ _ _ ",
                        " _ _ _ _",
                        "_ _ _ _ ",
                        " _ _ _ _"
                ),
                Spieler.SPIELER_UNTEN,
                zug(
                        new Position(2, 2),
                        new Position(0, 4),
                        brett(
                                "_ _ B _ ",
                                " _ _ a _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ _ _"
                        )
                )
        );
    }

    @Test
    void testDameBewegen() {
        testZuege(
                brett(
                        "b _ _ _ ",
                        " _ _ _ b",
                        "_ _ _ _ ",
                        " _ _ _ _",
                        "_ _ A _ ",
                        " _ _ _ _",
                        "_ _ _ _ ",
                        " b _ _ b"
                ),
                Spieler.SPIELER_OBEN,

                // Nach unten rechts
                zug(
                        new Position(4, 4),
                        new Position(5, 5),
                        brett(
                                "b _ _ _ ",
                                " _ _ _ b",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ A _",
                                "_ _ _ _ ",
                                " b _ _ b"
                        )
                ),
                zug(
                        new Position(4, 4),
                        new Position(6, 6),
                        brett(
                                "b _ _ _ ",
                                " _ _ _ b",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ A ",
                                " b _ _ b"
                        )
                ),

                // Nach unten links
                zug(
                        new Position(4, 4),
                        new Position(5, 3),
                        brett(
                                "b _ _ _ ",
                                " _ _ _ b",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ A _ _",
                                "_ _ _ _ ",
                                " b _ _ b"
                        )
                ),
                zug(
                        new Position(4, 4),
                        new Position(6, 2),
                        brett(
                                "b _ _ _ ",
                                " _ _ _ b",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ A _ _ ",
                                " b _ _ b"
                        )
                ),

                // Nach oben rechts
                zug(
                        new Position(4, 4),
                        new Position(3, 5),
                        brett(
                                "b _ _ _ ",
                                " _ _ _ b",
                                "_ _ _ _ ",
                                " _ _ A _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " b _ _ b"
                        )
                ),
                zug(
                        new Position(4, 4),
                        new Position(2, 6),
                        brett(
                                "b _ _ _ ",
                                " _ _ _ b",
                                "_ _ _ A ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " b _ _ b"
                        )
                ),

                // Nach oben links
                zug(
                        new Position(4, 4),
                        new Position(3, 3),
                        brett(
                                "b _ _ _ ",
                                " _ _ _ b",
                                "_ _ _ _ ",
                                " _ A _ _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " b _ _ b"
                        )
                ),
                zug(
                        new Position(4, 4),
                        new Position(2, 2),
                        brett(
                                "b _ _ _ ",
                                " _ _ _ b",
                                "_ A _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " b _ _ b"
                        )
                ),
                zug(
                        new Position(4, 4),
                        new Position(1, 1),
                        brett(
                                "b _ _ _ ",
                                " A _ _ b",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " b _ _ b"
                        )
                )
        );
    }

    @Test
    void testDameSchlagen() {
        testZuege(
                brett(
                        "_ _ _ _ ",
                        " _ _ b _",
                        "_ _ _ _ ",
                        " _ A _ _",
                        "_ _ _ _ ",
                        " _ _ _ _",
                        "_ _ _ _ ",
                        " _ _ _ _"
                ),
                Spieler.SPIELER_OBEN,
                zug(
                        new Position(3, 3),
                        new Position(0, 6),
                        brett(
                                "_ _ _ A ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ _ _"
                        )
                )
        );

        testZuege(
                brett(
                        "_ _ _ _ ",
                        " _ b _ _",
                        "_ _ _ _ ",
                        " _ _ _ _",
                        "_ _ B _ ",
                        " _ _ _ _",
                        "_ _ _ _ ",
                        " A _ _ _"
                ),
                Spieler.SPIELER_OBEN,
                zug(
                        new Position(7, 1),
                        new Position(0, 2),
                        brett(
                                "_ _ _ _ ",
                                " _ b _ _",
                                "_ _ _ _ ",
                                " _ _ A _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ _ _"
                        ),
                        brett(
                                "_ A _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ _ _"
                        )
                )
        );

        testZuege(
                brett(
                        "_ _ _ _ ",
                        " _ _ _ _",
                        "_ _ _ _ ",
                        " b _ a _",
                        "_ _ _ _ ",
                        " _ _ _ _",
                        "_ b B _ ",
                        " A _ _ _"
                ),
                Spieler.SPIELER_OBEN,
                zug(
                        new Position(7, 1),
                        new Position(2, 0),
                        brett(
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " b _ a _",
                                "_ _ _ _ ",
                                " _ A _ _",
                                "_ _ B _ ",
                                " _ _ _ _"
                        ),
                        brett(
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " b _ a _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ A _"
                        ),
                        brett(
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "A _ _ _ ",
                                " _ _ a _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ _ _"
                        )
                ),
                zug(
                        new Position(7, 1),
                        new Position(7, 5),
                        brett(
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " b _ a _",
                                "_ _ _ _ ",
                                " _ A _ _",
                                "_ _ B _ ",
                                " _ _ _ _"
                        ),
                        brett(
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "A _ _ _ ",
                                " _ _ a _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ B _ ",
                                " _ _ _ _"
                        ),
                        brett(
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ a _",
                                "_ _ _ _ ",
                                " _ _ _ _",
                                "_ _ _ _ ",
                                " _ _ A _"
                        )
                )
        );
    }

    @Test
    void testKeineZuegeWennGewonnen() {
        testZuege(
                brett(
                        "_ a _ _ ",
                        " _ _ _ _",
                        "_ _ _ _ ",
                        " _ _ _ _",
                        "_ _ _ _ ",
                        " _ _ a _",
                        "_ _ _ a ",
                        " _ _ _ b"
                ),
                Spieler.SPIELER_OBEN
        );
    }
}
