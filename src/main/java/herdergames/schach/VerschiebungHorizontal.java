package herdergames.schach;

import java.util.Set;

record VerschiebungHorizontal(int verschiebung) {
    static final VerschiebungHorizontal LINKS = new VerschiebungHorizontal(-1);
    static final VerschiebungHorizontal KEINE = new VerschiebungHorizontal(0);
    static final VerschiebungHorizontal RECHTS = new VerschiebungHorizontal(1);

    static final Set<VerschiebungHorizontal> LINKS_RECHTS = Set.of(LINKS, RECHTS);

    VerschiebungHorizontal doppelt() {
        return new VerschiebungHorizontal(verschiebung * 2);
    }
}
