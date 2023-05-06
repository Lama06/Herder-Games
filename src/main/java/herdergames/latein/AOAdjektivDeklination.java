package herdergames.latein;

import java.util.Map;
import java.util.Optional;

final class AOAdjektivDeklination extends AdjektivDeklination {
    static final AOAdjektivDeklination INSTANCE = new AOAdjektivDeklination();

    static final Map<Genus, Map<Numerus, Map<Kasus, String>>> ENDUNGEN = Map.of(
            Genus.MASKULINUM, Map.of(
                    Numerus.SINGULAR, Map.of(
                            Kasus.NOMINATIV, "us",
                            Kasus.GENITIV, "i",
                            Kasus.DATIV, "o",
                            Kasus.AKKUSATIV, "um",
                            Kasus.ABLATIV, "o",
                            Kasus.VOKATIV, "e"
                    ),
                    Numerus.PLURAL, Map.of(
                            Kasus.NOMINATIV, "i",
                            Kasus.GENITIV, "orum",
                            Kasus.DATIV, "is",
                            Kasus.AKKUSATIV, "os",
                            Kasus.ABLATIV, "is",
                            Kasus.VOKATIV, "i"
                    )
            ),
            Genus.NEUTRUM, Map.of(
                    Numerus.SINGULAR, Map.of(
                            Kasus.NOMINATIV, "um",
                            Kasus.GENITIV, "i",
                            Kasus.DATIV, "o",
                            Kasus.AKKUSATIV, "um",
                            Kasus.ABLATIV, "o",
                            Kasus.VOKATIV, "um"
                    ),
                    Numerus.PLURAL, Map.of(
                            Kasus.NOMINATIV, "a",
                            Kasus.GENITIV, "orum",
                            Kasus.DATIV, "is",
                            Kasus.AKKUSATIV, "a",
                            Kasus.ABLATIV, "is",
                            Kasus.VOKATIV, "a"
                    )
            ),
            Genus.FEMININUM, Map.of(
                    Numerus.SINGULAR, Map.of(
                            Kasus.NOMINATIV, "a",
                            Kasus.GENITIV, "ae",
                            Kasus.DATIV, "ae",
                            Kasus.AKKUSATIV, "am",
                            Kasus.ABLATIV, "a",
                            Kasus.VOKATIV, "a"
                    ),
                    Numerus.PLURAL, Map.of(
                            Kasus.NOMINATIV, "ae",
                            Kasus.GENITIV, "arum",
                            Kasus.DATIV, "is",
                            Kasus.AKKUSATIV, "as",
                            Kasus.ABLATIV, "is",
                            Kasus.VOKATIV, "ae"
                    )
            )
    );

    private Optional<Adjektiv> parseAUmKurz(AdjektivWoerterbuchEintrag eintrag) {
        // bonus

        if (!eintrag.ersteForm().endsWith("us")) {
            return Optional.empty();
        }
        String stamm = eintrag.ersteForm().substring(0, eintrag.ersteForm().length() - "us".length());

        if (eintrag.zweiteForm().isPresent() || eintrag.dritteForm().isPresent()) {
            return Optional.empty();
        }

        return Optional.of(new AOAdjektiv(stamm, Optional.empty()));
    }

    private Optional<Adjektiv> parseAUmLang(AdjektivWoerterbuchEintrag eintrag) {
        // bonus a um

        if (!eintrag.ersteForm().endsWith("us")) {
            return Optional.empty();
        }
        String stamm = eintrag.ersteForm().substring(0, eintrag.ersteForm().length() - "us".length());

        if (eintrag.zweiteForm().isEmpty() || !eintrag.zweiteForm().get().equals("a")) {
            return Optional.empty();
        }

        if (eintrag.dritteForm().isEmpty() || !eintrag.dritteForm().get().equals("um")) {
            return Optional.empty();
        }

        return Optional.of(new AOAdjektiv(stamm, Optional.empty()));
    }

    private Optional<Adjektiv> parseAUmKomplett(AdjektivWoerterbuchEintrag eintrag) {
        // bonus bona bonum

        if (!eintrag.ersteForm().endsWith("us")) {
            return Optional.empty();
        }
        String stamm = eintrag.ersteForm().substring(0, eintrag.ersteForm().length() - "us".length());

        if (eintrag.zweiteForm().isEmpty() || !eintrag.zweiteForm().get().equals(stamm + "a")) {
            return Optional.empty();
        }

        if (eintrag.dritteForm().isEmpty() || !eintrag.dritteForm().get().equals(stamm + "um")) {
            return Optional.empty();
        }

        return Optional.of(new AOAdjektiv(stamm, Optional.empty()));
    }

    private Optional<Adjektiv> parseEr(AdjektivWoerterbuchEintrag eintrag) {
        // pulcher pulchra pulchrum

        if (!eintrag.ersteForm().endsWith("er")) {
            return Optional.empty();
        }

        if (eintrag.zweiteForm().isEmpty() || !eintrag.zweiteForm().get().endsWith("a")) {
            return Optional.empty();
        }
        String stamm = eintrag.zweiteForm().get().substring(0, eintrag.zweiteForm().get().length() - "a".length());

        if (eintrag.dritteForm().isEmpty() || !eintrag.dritteForm().get().equals(stamm + "um")) {
            return Optional.empty();
        }

        return Optional.of(new AOAdjektiv(stamm, Optional.of(eintrag.ersteForm())));
    }

    @Override
    Optional<Adjektiv> parse(AdjektivWoerterbuchEintrag eintrag) {
        return parseAUmKurz(eintrag)
                .or(() -> parseAUmLang(eintrag))
                .or(() -> parseAUmKomplett(eintrag))
                .or(() -> parseEr(eintrag));
    }
}
