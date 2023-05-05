package herdergames.latein;

import java.util.Map;

final class HicDeklination extends UnregelmaessigeAdjektivDeklination {
    static final HicDeklination INSTANCE = new HicDeklination();

    @Override
    Map<Genus, Map<Numerus, Map<Kasus, String>>> getFormen() {
        return Map.of(
                Genus.MASKULINUM, Map.of(
                        Numerus.SINGULAR, Map.of(
                                Kasus.NOMINATIV, "hic",
                                Kasus.GENITIV, "huius",
                                Kasus.DATIV, "huic",
                                Kasus.AKKUSATIV, "hunc",
                                Kasus.ABLATIV, "hoc",
                                Kasus.VOKATIV, "hic"
                        ),
                        Numerus.PLURAL, Map.of(
                                Kasus.NOMINATIV, "hi",
                                Kasus.GENITIV, "horum",
                                Kasus.DATIV, "his",
                                Kasus.AKKUSATIV, "hos",
                                Kasus.ABLATIV, "hic",
                                Kasus.VOKATIV, "hi"
                        )
                ),
                Genus.FEMININUM, Map.of(
                        Numerus.SINGULAR, Map.of(
                                Kasus.NOMINATIV, "haec",
                                Kasus.GENITIV, "huius",
                                Kasus.DATIV, "huic",
                                Kasus.AKKUSATIV, "hanc",
                                Kasus.ABLATIV, "hac",
                                Kasus.VOKATIV, "haec"
                        ),
                        Numerus.PLURAL, Map.of(
                                Kasus.NOMINATIV, "hae",
                                Kasus.GENITIV, "harum",
                                Kasus.DATIV, "his",
                                Kasus.AKKUSATIV, "has",
                                Kasus.ABLATIV, "his",
                                Kasus.VOKATIV, "hae"
                        )
                ),
                Genus.NEUTRUM, Map.of(
                        Numerus.SINGULAR, Map.of(
                                Kasus.NOMINATIV, "hoc",
                                Kasus.GENITIV, "huius",
                                Kasus.DATIV, "huic",
                                Kasus.AKKUSATIV, "hoc",
                                Kasus.ABLATIV, "hoc",
                                Kasus.VOKATIV, "hoc"
                        ),
                        Numerus.PLURAL, Map.of(
                                Kasus.NOMINATIV, "haec",
                                Kasus.GENITIV, "horum",
                                Kasus.DATIV, "his",
                                Kasus.AKKUSATIV, "haec",
                                Kasus.ABLATIV, "his",
                                Kasus.VOKATIV, "haec"
                        )
                )
        );
    }
}
