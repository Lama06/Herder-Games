package herdergames.schach;

import java.util.Set;

record Verschiebung2D(VerschiebungVertikal vertikal, VerschiebungHorizontal horizontal) {
    static final Set<Verschiebung2D> LAEUFER_VERSCHIEBUNGEN = Set.of(
            new Verschiebung2D(VerschiebungVertikal.OBEN, VerschiebungHorizontal.LINKS),
            new Verschiebung2D(VerschiebungVertikal.OBEN, VerschiebungHorizontal.RECHTS),
            new Verschiebung2D(VerschiebungVertikal.UNTEN, VerschiebungHorizontal.RECHTS),
            new Verschiebung2D(VerschiebungVertikal.UNTEN, VerschiebungHorizontal.LINKS)
    );

    static final Set<Verschiebung2D> SPRINGER_VERSCHIEBUNGEN = Set.of(
            new Verschiebung2D(VerschiebungVertikal.OBEN.doppelt(), VerschiebungHorizontal.LINKS),
            new Verschiebung2D(VerschiebungVertikal.OBEN.doppelt(), VerschiebungHorizontal.RECHTS),

            new Verschiebung2D(VerschiebungVertikal.OBEN, VerschiebungHorizontal.LINKS.doppelt()),
            new Verschiebung2D(VerschiebungVertikal.UNTEN, VerschiebungHorizontal.LINKS.doppelt()),

            new Verschiebung2D(VerschiebungVertikal.UNTEN.doppelt(), VerschiebungHorizontal.LINKS),
            new Verschiebung2D(VerschiebungVertikal.UNTEN.doppelt(), VerschiebungHorizontal.RECHTS),

            new Verschiebung2D(VerschiebungVertikal.OBEN, VerschiebungHorizontal.RECHTS.doppelt()),
            new Verschiebung2D(VerschiebungVertikal.UNTEN, VerschiebungHorizontal.RECHTS.doppelt())
    );

    static final Set<Verschiebung2D> TURM_VERSCHIEBUNGEN = Set.of(
            new Verschiebung2D(VerschiebungVertikal.OBEN, VerschiebungHorizontal.KEINE),
            new Verschiebung2D(VerschiebungVertikal.UNTEN, VerschiebungHorizontal.KEINE),
            new Verschiebung2D(VerschiebungVertikal.KEINE, VerschiebungHorizontal.LINKS),
            new Verschiebung2D(VerschiebungVertikal.KEINE, VerschiebungHorizontal.RECHTS)
    );

    static final Set<Verschiebung2D> ALLE_VERSCHIEBUNGEN = Set.of(
            new Verschiebung2D(VerschiebungVertikal.OBEN, VerschiebungHorizontal.LINKS),
            new Verschiebung2D(VerschiebungVertikal.OBEN, VerschiebungHorizontal.KEINE),
            new Verschiebung2D(VerschiebungVertikal.OBEN, VerschiebungHorizontal.RECHTS),

            new Verschiebung2D(VerschiebungVertikal.KEINE, VerschiebungHorizontal.LINKS),
            new Verschiebung2D(VerschiebungVertikal.KEINE, VerschiebungHorizontal.KEINE),
            new Verschiebung2D(VerschiebungVertikal.KEINE, VerschiebungHorizontal.RECHTS),

            new Verschiebung2D(VerschiebungVertikal.UNTEN, VerschiebungHorizontal.LINKS),
            new Verschiebung2D(VerschiebungVertikal.UNTEN, VerschiebungHorizontal.KEINE),
            new Verschiebung2D(VerschiebungVertikal.UNTEN, VerschiebungHorizontal.RECHTS)
    );
}
