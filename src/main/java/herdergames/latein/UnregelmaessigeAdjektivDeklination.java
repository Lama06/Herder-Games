package herdergames.latein;

import java.util.Map;
import java.util.Optional;

abstract class UnregelmaessigeAdjektivDeklination extends AdjektivDeklination {
    abstract Map<Genus, Map<Numerus, Map<Kasus, String>>> getFormen();

    @Override
    final Optional<Adjektiv> parse(AdjektivWoerterbuchEintrag eintrag) {
        String nominativSingularMaskulinum = getFormen().get(Genus.MASKULINUM).get(Numerus.SINGULAR).get(Kasus.NOMINATIV);
        String nominativSingularFemininum = getFormen().get(Genus.FEMININUM).get(Numerus.SINGULAR).get(Kasus.NOMINATIV);
        String nominativSingularNeutrum = getFormen().get(Genus.NEUTRUM).get(Numerus.SINGULAR).get(Kasus.NOMINATIV);

        if (eintrag.ersteForm().isEmpty() || !eintrag.ersteForm().equals(nominativSingularMaskulinum) ||
                eintrag.zweiteForm().isEmpty() || !eintrag.zweiteForm().get().equals(nominativSingularFemininum) ||
                eintrag.dritteForm().isEmpty() || !eintrag.dritteForm().get().equals(nominativSingularNeutrum)) {
            return Optional.empty();
        }

        return Optional.of(new UnregelmaessigesAdjektiv(this));
    }
}
