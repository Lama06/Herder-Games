package herdergames.latein;

import java.util.Map;
import java.util.Optional;

final class KonsAdjektivDeklination extends AdjektivDeklination {
    static final KonsAdjektivDeklination INSTANCE = new KonsAdjektivDeklination();

    static final Map<Genus, Map<Numerus, Map<Kasus, String>>> ENDUNGEN = Map.of(
            Genus.MASKULINUM, Map.of(
                    Numerus.SINGULAR, Map.of(
                            Kasus.GENITIV, "is",
                            Kasus.DATIV, "i",
                            Kasus.AKKUSATIV, "em",
                            Kasus.ABLATIV, "i"
                    ),
                    Numerus.PLURAL, Map.of(
                            Kasus.NOMINATIV, "es",
                            Kasus.GENITIV, "ium",
                            Kasus.DATIV, "ibus",
                            Kasus.AKKUSATIV, "es",
                            Kasus.ABLATIV, "ibus",
                            Kasus.VOKATIV, "es"
                    )
            ),
            Genus.FEMININUM, Map.of(
                    Numerus.SINGULAR, Map.of(
                            Kasus.GENITIV, "is",
                            Kasus.DATIV, "i",
                            Kasus.AKKUSATIV, "em",
                            Kasus.ABLATIV, "i"
                    ),
                    Numerus.PLURAL, Map.of(
                            Kasus.NOMINATIV, "es",
                            Kasus.GENITIV, "ium",
                            Kasus.DATIV, "ibus",
                            Kasus.AKKUSATIV, "es",
                            Kasus.ABLATIV, "ibus",
                            Kasus.VOKATIV, "es"
                    )
            ),
            Genus.NEUTRUM, Map.of(
                    Numerus.SINGULAR, Map.of(
                            Kasus.GENITIV, "is",
                            Kasus.DATIV, "i",
                            Kasus.ABLATIV, "i"
                    ),
                    Numerus.PLURAL, Map.of(
                            Kasus.NOMINATIV, "ia",
                            Kasus.GENITIV, "ium",
                            Kasus.DATIV, "ibus",
                            Kasus.AKKUSATIV, "ia",
                            Kasus.ABLATIV, "ibus",
                            Kasus.VOKATIV, "ia"
                    )
            )
    );

    private Optional<Adjektiv> parseEinendig(AdjektivWoerterbuchEintrag eintrag) {
        // vehemens vehementis

        String nominativSingular = eintrag.ersteForm();

        if (eintrag.zweiteForm().isEmpty() || !eintrag.zweiteForm().get().endsWith("is")) {
            return Optional.empty();
        }
        String stamm = eintrag.zweiteForm().get().substring(0, eintrag.zweiteForm().get().length() - "is".length());

        if (eintrag.dritteForm().isPresent()) {
            return Optional.empty();
        }

        return Optional.of(new KonsAdjektiv(nominativSingular, nominativSingular, nominativSingular, stamm));
    }

    private Optional<Adjektiv> parseZweiendigKurz(AdjektivWoerterbuchEintrag eintrag) {
        // gravis e

        if (!eintrag.ersteForm().endsWith("is")) {
            return Optional.empty();
        }
        String stamm = eintrag.ersteForm().substring(0, eintrag.ersteForm().length() - "is".length());

        if (eintrag.zweiteForm().isEmpty() || !eintrag.zweiteForm().get().equals("e")) {
            return Optional.empty();
        }
        String nominativSingularNeutrum = stamm + "e";

        if (eintrag.dritteForm().isPresent()) {
            return Optional.empty();
        }

        return Optional.of(new KonsAdjektiv(eintrag.ersteForm(), eintrag.ersteForm(), nominativSingularNeutrum, stamm));
    }

    private Optional<Adjektiv> parseZweiendigLang(AdjektivWoerterbuchEintrag eintrag) {
        // gravis grave

        if (!eintrag.ersteForm().endsWith("is")) {
            return Optional.empty();
        }
        String stamm = eintrag.ersteForm().substring(0, eintrag.ersteForm().length() - "is".length());

        if (eintrag.zweiteForm().isEmpty() || !eintrag.zweiteForm().get().equals(stamm + "e")) {
            return Optional.empty();
        }

        if (eintrag.dritteForm().isPresent()) {
            return Optional.empty();
        }

        return Optional.of(new KonsAdjektiv(eintrag.ersteForm(), eintrag.ersteForm(), eintrag.zweiteForm().get(), stamm));
    }

    private Optional<Adjektiv> parseDreiendig(AdjektivWoerterbuchEintrag eintrag) {
        // acer acris acre

        String nominativSingularMaskulinum = eintrag.ersteForm();

        if (eintrag.zweiteForm().isEmpty() || !eintrag.zweiteForm().get().endsWith("is")) {
            return Optional.empty();
        }
        String nominativSingularFemininum = eintrag.zweiteForm().get();
        String stamm = nominativSingularFemininum.substring(0, nominativSingularFemininum.length() - "is".length());

        if (eintrag.dritteForm().isEmpty() || !eintrag.dritteForm().get().equals(stamm + "e")) {
            return Optional.empty();
        }
        String nominativSingularNeutrum = eintrag.dritteForm().get();

        return Optional.of(new KonsAdjektiv(nominativSingularMaskulinum, nominativSingularFemininum, nominativSingularNeutrum, stamm));
    }

    @Override
    Optional<Adjektiv> parse(AdjektivWoerterbuchEintrag eintrag) {
        return parseEinendig(eintrag)
                .or(() -> parseZweiendigKurz(eintrag))
                .or(() -> parseZweiendigLang(eintrag))
                .or(() -> parseDreiendig(eintrag));
    }
}
