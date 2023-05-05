package herdergames.latein;

import java.util.Map;

final class IsDeklination extends UnregelmaessigeAdjektivDeklination {
    static final IsDeklination INSTANCE = new IsDeklination();

    @Override
    Map<Genus, Map<Numerus, Map<Kasus, String>>> getFormen() {
        return Map.of(
                Genus.MASKULINUM, Map.of(
                        Numerus.SINGULAR, Map.of(
                                Kasus.NOMINATIV, "is",
                                Kasus.GENITIV, "eius",
                                Kasus.DATIV, "ei",
                                Kasus.AKKUSATIV, "eum",
                                Kasus.ABLATIV, "eo",
                                Kasus.VOKATIV, "is"
                        ),
                        Numerus.PLURAL, Map.of(
                                Kasus.NOMINATIV, "ii",
                                Kasus.GENITIV, "eorum",
                                Kasus.DATIV, "iis",
                                Kasus.AKKUSATIV, "eos",
                                Kasus.ABLATIV, "iis",
                                Kasus.VOKATIV, "ii"
                        )
                ),
                Genus.FEMININUM, Map.of(
                        Numerus.SINGULAR, Map.of(
                                Kasus.NOMINATIV, "ea",
                                Kasus.GENITIV, "eius",
                                Kasus.DATIV, "ei",
                                Kasus.AKKUSATIV, "eam",
                                Kasus.ABLATIV, "ea",
                                Kasus.VOKATIV, "ea"
                        ),
                        Numerus.PLURAL, Map.of(
                                Kasus.NOMINATIV, "eae",
                                Kasus.GENITIV, "earum",
                                Kasus.DATIV, "eis",
                                Kasus.AKKUSATIV, "eas",
                                Kasus.ABLATIV, "eis",
                                Kasus.VOKATIV, "eae"
                        )
                ),
                Genus.NEUTRUM, Map.of(
                        Numerus.SINGULAR, Map.of(
                                Kasus.NOMINATIV, "id",
                                Kasus.GENITIV, "eius",
                                Kasus.DATIV, "ei",
                                Kasus.AKKUSATIV, "id",
                                Kasus.ABLATIV, "eo",
                                Kasus.VOKATIV, "id"
                        ),
                        Numerus.PLURAL, Map.of(
                                Kasus.NOMINATIV, "ea",
                                Kasus.GENITIV, "eorum",
                                Kasus.DATIV, "eis",
                                Kasus.AKKUSATIV, "ea",
                                Kasus.ABLATIV, "eis",
                                Kasus.VOKATIV, "ea"
                        )
                )
        );
    }
}
