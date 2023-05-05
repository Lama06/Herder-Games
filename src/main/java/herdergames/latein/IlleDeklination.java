package herdergames.latein;

import java.util.Map;

final class IlleDeklination extends UnregelmaessigeAdjektivDeklination {
    static final IlleDeklination INSTANCE = new IlleDeklination();

    @Override
    Map<Genus, Map<Numerus, Map<Kasus, String>>> getFormen() {
        return Map.of(
                Genus.MASKULINUM, Map.of(
                        Numerus.SINGULAR, Map.of(
                                Kasus.NOMINATIV, "ille",
                                Kasus.GENITIV, "illius",
                                Kasus.DATIV, "illi",
                                Kasus.AKKUSATIV, "illum",
                                Kasus.ABLATIV, "illo",
                                Kasus.VOKATIV, "ille"
                        ),
                        Numerus.PLURAL, Map.of(
                                Kasus.NOMINATIV, "illi",
                                Kasus.GENITIV, "illorum",
                                Kasus.DATIV, "illis",
                                Kasus.AKKUSATIV, "illos",
                                Kasus.ABLATIV, "illis",
                                Kasus.VOKATIV, "illi"
                        )
                ),
                Genus.FEMININUM, Map.of(
                        Numerus.SINGULAR, Map.of(
                                Kasus.NOMINATIV, "illa",
                                Kasus.GENITIV, "illius",
                                Kasus.DATIV, "illi",
                                Kasus.AKKUSATIV, "illam",
                                Kasus.ABLATIV, "illa",
                                Kasus.VOKATIV, "illa"
                        ),
                        Numerus.PLURAL, Map.of(
                                Kasus.NOMINATIV, "illae",
                                Kasus.GENITIV, "illarum",
                                Kasus.DATIV, "illis",
                                Kasus.AKKUSATIV, "illas",
                                Kasus.ABLATIV, "illis",
                                Kasus.VOKATIV, "illae"
                        )
                ),
                Genus.NEUTRUM, Map.of(
                        Numerus.SINGULAR, Map.of(
                                Kasus.NOMINATIV, "illud",
                                Kasus.GENITIV, "illius",
                                Kasus.DATIV, "illi",
                                Kasus.AKKUSATIV, "illud",
                                Kasus.ABLATIV, "illo",
                                Kasus.VOKATIV, "illud"
                        ),
                        Numerus.PLURAL, Map.of(
                                Kasus.NOMINATIV, "illa",
                                Kasus.GENITIV, "illorum",
                                Kasus.DATIV, "illis",
                                Kasus.AKKUSATIV, "illa",
                                Kasus.ABLATIV, "illis",
                                Kasus.VOKATIV, "illa"
                        )
                )
        );
    }
}
