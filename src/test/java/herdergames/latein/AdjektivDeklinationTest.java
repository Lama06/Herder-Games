package herdergames.latein;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AdjektivDeklinationTest {
    private void testDeklination(
            Set<AdjektivWoerterbuchEintrag> eintraege,
            Optional<Steigerung> steigerung,
            Map<Genus, Map<Numerus, Map<Kasus, String>>> formen
    ) {
        for (AdjektivWoerterbuchEintrag eintrag : eintraege) {
            Adjektiv adjektiv = eintrag.zuAdjektiv().orElseGet(Assertions::fail);
            if (steigerung.isPresent()) {
                assertTrue(adjektiv.steigerbar);
                adjektiv = adjektiv.steigern(steigerung.get());
            }

            for (Map.Entry<Genus, Map<Numerus, Map<Kasus, String>>> genusEintrag : formen.entrySet()) {
                Genus genus = genusEintrag.getKey();
                for (Map.Entry<Numerus, Map<Kasus, String>> numerusEintrag : genusEintrag.getValue().entrySet()) {
                    Numerus numerus = numerusEintrag.getKey();
                    for (Map.Entry<Kasus, String> kasusEintrag : numerusEintrag.getValue().entrySet()) {
                        Kasus kasus = kasusEintrag.getKey();

                        String erwartet = kasusEintrag.getValue();
                        String erhalten = adjektiv.deklinieren(genus, numerus, kasus);
                        assertEquals(erwartet, erhalten);
                    }
                }
            }
        }
    }

    @Test
    void testAODeklination() {
        testDeklination(
                Set.of(
                        new AdjektivWoerterbuchEintrag("bonus", Optional.empty(), Optional.empty()),
                        new AdjektivWoerterbuchEintrag("bonus", Optional.of("a"), Optional.of("um")),
                        new AdjektivWoerterbuchEintrag("bonus", Optional.of("bona"), Optional.of("bonum"))
                ),
                Optional.empty(),
                Map.of(
                        Genus.MASKULINUM, Map.of(
                                Numerus.SINGULAR, Map.of(
                                        Kasus.NOMINATIV, "bonus",
                                        Kasus.GENITIV, "boni",
                                        Kasus.DATIV, "bono",
                                        Kasus.AKKUSATIV, "bonum",
                                        Kasus.ABLATIV, "bono",
                                        Kasus.VOKATIV, "bone"
                                ),
                                Numerus.PLURAL, Map.of(
                                        Kasus.NOMINATIV, "boni",
                                        Kasus.GENITIV, "bonorum",
                                        Kasus.DATIV, "bonis",
                                        Kasus.AKKUSATIV, "bonos",
                                        Kasus.ABLATIV, "bonis",
                                        Kasus.VOKATIV, "boni"
                                )
                        ),
                        Genus.NEUTRUM, Map.of(
                                Numerus.SINGULAR, Map.of(
                                        Kasus.NOMINATIV, "bonum",
                                        Kasus.GENITIV, "boni",
                                        Kasus.DATIV, "bono",
                                        Kasus.AKKUSATIV, "bonum",
                                        Kasus.ABLATIV, "bono",
                                        Kasus.VOKATIV, "bonum"
                                ),
                                Numerus.PLURAL, Map.of(
                                        Kasus.NOMINATIV, "bona",
                                        Kasus.GENITIV, "bonorum",
                                        Kasus.DATIV, "bonis",
                                        Kasus.AKKUSATIV, "bona",
                                        Kasus.ABLATIV, "bonis",
                                        Kasus.VOKATIV, "bona"
                                )
                        ),
                        Genus.FEMININUM, Map.of(
                                Numerus.SINGULAR, Map.of(
                                        Kasus.NOMINATIV, "bona",
                                        Kasus.GENITIV, "bonae",
                                        Kasus.DATIV, "bonae",
                                        Kasus.AKKUSATIV, "bonam",
                                        Kasus.ABLATIV, "bona",
                                        Kasus.VOKATIV, "bona"
                                ),
                                Numerus.PLURAL, Map.of(
                                        Kasus.NOMINATIV, "bonae",
                                        Kasus.GENITIV, "bonarum",
                                        Kasus.DATIV, "bonis",
                                        Kasus.AKKUSATIV, "bonas",
                                        Kasus.ABLATIV, "bonis",
                                        Kasus.VOKATIV, "bonae"
                                )
                        )
                )
        );
    }

    @Test
    void testAODeklinationAufEr() {
        testDeklination(
                Set.of(new AdjektivWoerterbuchEintrag("pulcher", Optional.of("pulchra"), Optional.of("pulchrum"))),
                Optional.empty(),
                Map.of(
                        Genus.MASKULINUM, Map.of(
                                Numerus.SINGULAR, Map.of(
                                        Kasus.NOMINATIV, "pulcher",
                                        Kasus.GENITIV, "pulchri",
                                        Kasus.DATIV, "pulchro",
                                        Kasus.AKKUSATIV, "pulchrum",
                                        Kasus.ABLATIV, "pulchro",
                                        Kasus.VOKATIV, "pulcher"
                                ),
                                Numerus.PLURAL, Map.of(
                                        Kasus.NOMINATIV, "pulchri",
                                        Kasus.GENITIV, "pulchrorum",
                                        Kasus.DATIV, "pulchris",
                                        Kasus.AKKUSATIV, "pulchros",
                                        Kasus.ABLATIV, "pulchris",
                                        Kasus.VOKATIV, "pulchri"
                                )
                        ),
                        Genus.NEUTRUM, Map.of(
                                Numerus.SINGULAR, Map.of(
                                        Kasus.NOMINATIV, "pulchrum",
                                        Kasus.GENITIV, "pulchri",
                                        Kasus.DATIV, "pulchro",
                                        Kasus.AKKUSATIV, "pulchrum",
                                        Kasus.ABLATIV, "pulchro",
                                        Kasus.VOKATIV, "pulchrum"
                                ),
                                Numerus.PLURAL, Map.of(
                                        Kasus.NOMINATIV, "pulchra",
                                        Kasus.GENITIV, "pulchrorum",
                                        Kasus.DATIV, "pulchris",
                                        Kasus.AKKUSATIV, "pulchra",
                                        Kasus.ABLATIV, "pulchris",
                                        Kasus.VOKATIV, "pulchra"
                                )
                        ),
                        Genus.FEMININUM, Map.of(
                                Numerus.SINGULAR, Map.of(
                                        Kasus.NOMINATIV, "pulchra",
                                        Kasus.GENITIV, "pulchrae",
                                        Kasus.DATIV, "pulchrae",
                                        Kasus.AKKUSATIV, "pulchram",
                                        Kasus.ABLATIV, "pulchra",
                                        Kasus.VOKATIV, "pulchra"
                                ),
                                Numerus.PLURAL, Map.of(
                                        Kasus.NOMINATIV, "pulchrae",
                                        Kasus.GENITIV, "pulchrarum",
                                        Kasus.DATIV, "pulchris",
                                        Kasus.AKKUSATIV, "pulchras",
                                        Kasus.ABLATIV, "pulchris",
                                        Kasus.VOKATIV, "pulchrae"
                                )
                        )
                )
        );
    }

    @Test
    void testKonsDeklination() {
        testDeklination(
                Set.of(new AdjektivWoerterbuchEintrag("felix", Optional.of("felicis"), Optional.empty())),
                Optional.empty(),
                Map.of(
                        Genus.MASKULINUM, Map.of(
                                Numerus.SINGULAR, Map.of(
                                        Kasus.NOMINATIV, "felix",
                                        Kasus.GENITIV, "felicis",
                                        Kasus.DATIV, "felici",
                                        Kasus.AKKUSATIV, "felicem",
                                        Kasus.ABLATIV, "felici",
                                        Kasus.VOKATIV, "felix"
                                ),
                                Numerus.PLURAL, Map.of(
                                        Kasus.NOMINATIV, "felices",
                                        Kasus.GENITIV, "felicium",
                                        Kasus.DATIV, "felicibus",
                                        Kasus.AKKUSATIV, "felices",
                                        Kasus.ABLATIV, "felicibus",
                                        Kasus.VOKATIV, "felices"
                                )
                        ),
                        Genus.NEUTRUM, Map.of(
                                Numerus.SINGULAR, Map.of(
                                        Kasus.NOMINATIV, "felix",
                                        Kasus.GENITIV, "felicis",
                                        Kasus.DATIV, "felici",
                                        Kasus.AKKUSATIV, "felix",
                                        Kasus.ABLATIV, "felici",
                                        Kasus.VOKATIV, "felix"
                                ),
                                Numerus.PLURAL, Map.of(
                                        Kasus.NOMINATIV, "felicia",
                                        Kasus.GENITIV, "felicium",
                                        Kasus.DATIV, "felicibus",
                                        Kasus.AKKUSATIV, "felicia",
                                        Kasus.ABLATIV, "felicibus",
                                        Kasus.VOKATIV, "felicia"
                                )
                        ),
                        Genus.FEMININUM, Map.of(
                                Numerus.SINGULAR, Map.of(
                                        Kasus.NOMINATIV, "felix",
                                        Kasus.GENITIV, "felicis",
                                        Kasus.DATIV, "felici",
                                        Kasus.AKKUSATIV, "felicem",
                                        Kasus.ABLATIV, "felici",
                                        Kasus.VOKATIV, "felix"
                                ),
                                Numerus.PLURAL, Map.of(
                                        Kasus.NOMINATIV, "felices",
                                        Kasus.GENITIV, "felicium",
                                        Kasus.DATIV, "felicibus",
                                        Kasus.AKKUSATIV, "felices",
                                        Kasus.ABLATIV, "felicibus",
                                        Kasus.VOKATIV, "felices"
                                )
                        )
                )
        );
    }

    @Test
    void testKomperativ() {
        testDeklination(
                Set.of(new AdjektivWoerterbuchEintrag("gravis", Optional.of("grave"), Optional.empty())),
                Optional.of(Steigerung.KOMPERATIV),
                Map.of(
                        Genus.MASKULINUM, Map.of(
                                Numerus.SINGULAR, Map.of(
                                        Kasus.NOMINATIV, "gravior",
                                        Kasus.GENITIV, "gravioris",
                                        Kasus.DATIV, "graviori",
                                        Kasus.AKKUSATIV, "graviorem",
                                        Kasus.ABLATIV, "graviori",
                                        Kasus.VOKATIV, "gravior"
                                ),
                                Numerus.PLURAL, Map.of(
                                        Kasus.NOMINATIV, "graviores",
                                        Kasus.GENITIV, "graviorium",
                                        Kasus.DATIV, "gravioribus",
                                        Kasus.AKKUSATIV, "graviores",
                                        Kasus.ABLATIV, "gravioribus",
                                        Kasus.VOKATIV, "graviores"
                                )
                        ),
                        Genus.NEUTRUM, Map.of(
                                Numerus.SINGULAR, Map.of(
                                        Kasus.NOMINATIV, "gravius",
                                        Kasus.GENITIV, "gravioris",
                                        Kasus.DATIV, "graviori",
                                        Kasus.AKKUSATIV, "gravius",
                                        Kasus.ABLATIV, "graviori",
                                        Kasus.VOKATIV, "gravius"
                                ),
                                Numerus.PLURAL, Map.of(
                                        Kasus.NOMINATIV, "gravioria",
                                        Kasus.GENITIV, "graviorium",
                                        Kasus.DATIV, "gravioribus",
                                        Kasus.AKKUSATIV, "gravioria",
                                        Kasus.ABLATIV, "gravioribus",
                                        Kasus.VOKATIV, "gravioria"
                                )
                        ),
                        Genus.FEMININUM, Map.of(
                                Numerus.SINGULAR, Map.of(
                                        Kasus.NOMINATIV, "gravior",
                                        Kasus.GENITIV, "gravioris",
                                        Kasus.DATIV, "graviori",
                                        Kasus.AKKUSATIV, "graviorem",
                                        Kasus.ABLATIV, "graviori",
                                        Kasus.VOKATIV, "gravior"
                                ),
                                Numerus.PLURAL, Map.of(
                                        Kasus.NOMINATIV, "graviores",
                                        Kasus.GENITIV, "graviorium",
                                        Kasus.DATIV, "gravioribus",
                                        Kasus.AKKUSATIV, "graviores",
                                        Kasus.ABLATIV, "gravioribus",
                                        Kasus.VOKATIV, "graviores"
                                )
                        )
                )
        );
    }

    @Test
    void testUnregelmaessigerKomperativ() {
        Adjektiv magnusKomperativ = new AdjektivWoerterbuchEintrag("magnus", Optional.empty(), Optional.empty())
                .zuAdjektiv()
                .orElseGet(Assertions::fail)
                .steigern(Steigerung.KOMPERATIV);

        assertEquals(magnusKomperativ.deklinieren(Genus.MASKULINUM, Numerus.SINGULAR, Kasus.NOMINATIV), "maior");
        assertEquals(magnusKomperativ.deklinieren(Genus.NEUTRUM, Numerus.SINGULAR, Kasus.NOMINATIV), "maius");
        assertEquals(magnusKomperativ.deklinieren(Genus.MASKULINUM, Numerus.SINGULAR, Kasus.GENITIV), "maioris");
    }

    @Test
    void testSuperlativ() {
        testDeklination(
                Set.of(new AdjektivWoerterbuchEintrag("gravis", Optional.of("grave"), Optional.empty())),
                Optional.of(Steigerung.SUPERLATIV),
                Map.of(
                        Genus.MASKULINUM, Map.of(
                                Numerus.SINGULAR, Map.of(
                                        Kasus.NOMINATIV, "gravissimus",
                                        Kasus.GENITIV, "gravissimi",
                                        Kasus.DATIV, "gravissimo",
                                        Kasus.AKKUSATIV, "gravissimum",
                                        Kasus.ABLATIV, "gravissimo",
                                        Kasus.VOKATIV, "gravissime"
                                ),
                                Numerus.PLURAL, Map.of(
                                        Kasus.NOMINATIV, "gravissimi",
                                        Kasus.GENITIV, "gravissimorum",
                                        Kasus.DATIV, "gravissimis",
                                        Kasus.AKKUSATIV, "gravissimos",
                                        Kasus.ABLATIV, "gravissimis",
                                        Kasus.VOKATIV, "gravissimi"
                                )
                        ),
                        Genus.NEUTRUM, Map.of(
                                Numerus.SINGULAR, Map.of(
                                        Kasus.NOMINATIV, "gravissimum",
                                        Kasus.GENITIV, "gravissimi",
                                        Kasus.DATIV, "gravissimo",
                                        Kasus.AKKUSATIV, "gravissimum",
                                        Kasus.ABLATIV, "gravissimo",
                                        Kasus.VOKATIV, "gravissimum"
                                ),
                                Numerus.PLURAL, Map.of(
                                        Kasus.NOMINATIV, "gravissima",
                                        Kasus.GENITIV, "gravissimorum",
                                        Kasus.DATIV, "gravissimis",
                                        Kasus.AKKUSATIV, "gravissima",
                                        Kasus.ABLATIV, "gravissimis",
                                        Kasus.VOKATIV, "gravissima"
                                )
                        ),
                        Genus.FEMININUM, Map.of(
                                Numerus.SINGULAR, Map.of(
                                        Kasus.NOMINATIV, "gravissima",
                                        Kasus.GENITIV, "gravissimae",
                                        Kasus.DATIV, "gravissimae",
                                        Kasus.AKKUSATIV, "gravissimam",
                                        Kasus.ABLATIV, "gravissima",
                                        Kasus.VOKATIV, "gravissima"
                                ),
                                Numerus.PLURAL, Map.of(
                                        Kasus.NOMINATIV, "gravissimae",
                                        Kasus.GENITIV, "gravissimarum",
                                        Kasus.DATIV, "gravissimis",
                                        Kasus.AKKUSATIV, "gravissimas",
                                        Kasus.ABLATIV, "gravissimis",
                                        Kasus.VOKATIV, "gravissimae"
                                )
                        )
                )
        );
    }

    @Test
    void testSuperlativAufEr() {
        Adjektiv pulcherSuperlativ = new AdjektivWoerterbuchEintrag("pulcher", Optional.of("pulchra"), Optional.of("pulchrum"))
                .zuAdjektiv()
                .orElseGet(Assertions::fail)
                .steigern(Steigerung.SUPERLATIV);

        assertEquals(pulcherSuperlativ.deklinieren(Genus.MASKULINUM, Numerus.SINGULAR, Kasus.NOMINATIV), "pulcherrimus");
        assertEquals(pulcherSuperlativ.deklinieren(Genus.NEUTRUM, Numerus.SINGULAR, Kasus.NOMINATIV), "pulcherrimum");
        assertEquals(pulcherSuperlativ.deklinieren(Genus.MASKULINUM, Numerus.SINGULAR, Kasus.GENITIV), "pulcherrimi");
    }

    @Test
    void testUnregelmaessigerSuperlativ() {
        Adjektiv magnusSuperlativ = new AdjektivWoerterbuchEintrag("magnus", Optional.empty(), Optional.empty())
                .zuAdjektiv()
                .orElseGet(Assertions::fail)
                .steigern(Steigerung.SUPERLATIV);

        assertEquals(magnusSuperlativ.deklinieren(Genus.MASKULINUM, Numerus.SINGULAR, Kasus.NOMINATIV), "maximus");
        assertEquals(magnusSuperlativ.deklinieren(Genus.NEUTRUM, Numerus.SINGULAR, Kasus.NOMINATIV), "maximum");
        assertEquals(magnusSuperlativ.deklinieren(Genus.MASKULINUM, Numerus.SINGULAR, Kasus.GENITIV), "maximi");
    }
}
