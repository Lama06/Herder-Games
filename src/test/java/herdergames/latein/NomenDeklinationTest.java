package herdergames.latein;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NomenDeklinationTest {
    private void testDeklination(
            Set<NomenWoerterbuchEintrag> eintraege,
            Genus genus,
            Map<Numerus, Map<Kasus, String>> formen
    ) {
        for (NomenWoerterbuchEintrag eintrag : eintraege) {
            Nomen nomen = eintrag.zuNomen().orElseGet(Assertions::fail);

            assertEquals(nomen.genus, genus);

            for (Map.Entry<Numerus, Map<Kasus, String>> numerusEintrag : formen.entrySet()) {
                for (Map.Entry<Kasus, String> kasusEintrag : numerusEintrag.getValue().entrySet()) {
                    String erwartet = kasusEintrag.getValue();
                    String erhalten = nomen.deklinieren(numerusEintrag.getKey(), kasusEintrag.getKey());
                    assertEquals(erwartet, erhalten);
                }
            }
        }
    }

    @Test
    void testADeklination() {
        testDeklination(
                Set.of(
                        new NomenWoerterbuchEintrag("domina", Optional.empty(), Optional.empty()),
                        new NomenWoerterbuchEintrag("domina", Optional.of("dominae"), Optional.empty()),
                        new NomenWoerterbuchEintrag("domina", Optional.of("dominae"), Optional.of(Genus.FEMININUM))
                ),
                Genus.FEMININUM,
                Map.of(
                        Numerus.SINGULAR, Map.of(
                                Kasus.NOMINATIV, "domina",
                                Kasus.GENITIV, "dominae",
                                Kasus.DATIV, "dominae",
                                Kasus.AKKUSATIV, "dominam",
                                Kasus.ABLATIV, "domina",
                                Kasus.VOKATIV, "domina"
                        ),
                        Numerus.PLURAL, Map.of(
                                Kasus.NOMINATIV, "dominae",
                                Kasus.GENITIV, "dominarum",
                                Kasus.DATIV, "dominis",
                                Kasus.AKKUSATIV, "dominas",
                                Kasus.ABLATIV, "dominis",
                                Kasus.VOKATIV, "dominae"
                        )
                )
        );
    }

    @Test
    void testODeklinationMF() {
        testDeklination(
                Set.of(
                        new NomenWoerterbuchEintrag("Remus", Optional.empty(), Optional.empty()),
                        new NomenWoerterbuchEintrag("Remus", Optional.of("Remi"), Optional.empty()),
                        new NomenWoerterbuchEintrag("Remus", Optional.of("Remi"), Optional.of(Genus.MASKULINUM))
                ),
                Genus.MASKULINUM,
                Map.of(
                        Numerus.SINGULAR, Map.of(
                                Kasus.NOMINATIV, "Remus",
                                Kasus.GENITIV, "Remi",
                                Kasus.DATIV, "Remo",
                                Kasus.AKKUSATIV, "Remum",
                                Kasus.ABLATIV, "Remo",
                                Kasus.VOKATIV, "Reme"
                        ),
                        Numerus.PLURAL, Map.of(
                                Kasus.NOMINATIV, "Remi",
                                Kasus.GENITIV, "Remorum",
                                Kasus.DATIV, "Remis",
                                Kasus.AKKUSATIV, "Remos",
                                Kasus.ABLATIV, "Remis",
                                Kasus.VOKATIV, "Remi"
                        )
                )
        );
    }

    @Test
    void testODeklinationMFVokativ() {
        Nomen albius = new NomenWoerterbuchEintrag("Albius", Optional.empty(), Optional.empty()).zuNomen().orElseGet(Assertions::fail);
        assertEquals(albius.deklinieren(Numerus.SINGULAR, Kasus.VOKATIV), "Albi");
    }

    @Test
    void testODeklinationN() {
        testDeklination(
                Set.of(
                        new NomenWoerterbuchEintrag("templum", Optional.empty(), Optional.empty()),
                        new NomenWoerterbuchEintrag("templum", Optional.of("templi"), Optional.empty()),
                        new NomenWoerterbuchEintrag("templum", Optional.of("templi"), Optional.of(Genus.NEUTRUM))
                ),
                Genus.NEUTRUM,
                Map.of(
                        Numerus.SINGULAR, Map.of(
                                Kasus.NOMINATIV, "templum",
                                Kasus.GENITIV, "templi",
                                Kasus.DATIV, "templo",
                                Kasus.AKKUSATIV, "templum",
                                Kasus.ABLATIV, "templo",
                                Kasus.VOKATIV, "templum"
                        ),
                        Numerus.PLURAL, Map.of(
                                Kasus.NOMINATIV, "templa",
                                Kasus.GENITIV, "templorum",
                                Kasus.DATIV, "templis",
                                Kasus.AKKUSATIV, "templa",
                                Kasus.ABLATIV, "templis",
                                Kasus.VOKATIV, "templa"
                        )
                )
        );
    }

    @Test
    void testKonsDeklinationMF() {
        testDeklination(
                Set.of(new NomenWoerterbuchEintrag("senator", Optional.of("senatoris"), Optional.of(Genus.MASKULINUM))),
                Genus.MASKULINUM,
                Map.of(
                        Numerus.SINGULAR, Map.of(
                                Kasus.NOMINATIV, "senator",
                                Kasus.GENITIV, "senatoris",
                                Kasus.DATIV, "senatori",
                                Kasus.AKKUSATIV, "senatorem",
                                Kasus.ABLATIV, "senatore",
                                Kasus.VOKATIV, "senator"
                        ),
                        Numerus.PLURAL, Map.of(
                                Kasus.NOMINATIV, "senatores",
                                Kasus.GENITIV, "senatorum",
                                Kasus.DATIV, "senatoribus",
                                Kasus.AKKUSATIV, "senatores",
                                Kasus.ABLATIV, "senatoribus",
                                Kasus.VOKATIV, "senatores"
                        )
                )
        );
    }

    @Test
    void testKonsDeklinationN() {
        testDeklination(
                Set.of(new NomenWoerterbuchEintrag("ver", Optional.of("veris"), Optional.of(Genus.NEUTRUM))),
                Genus.NEUTRUM,
                Map.of(
                        Numerus.SINGULAR, Map.of(
                                Kasus.NOMINATIV, "ver",
                                Kasus.GENITIV, "veris",
                                Kasus.DATIV, "veri",
                                Kasus.AKKUSATIV, "ver",
                                Kasus.ABLATIV, "vere",
                                Kasus.VOKATIV, "ver"
                        ),
                        Numerus.PLURAL, Map.of(
                                Kasus.NOMINATIV, "vera",
                                Kasus.GENITIV, "verum",
                                Kasus.DATIV, "veribus",
                                Kasus.AKKUSATIV, "vera",
                                Kasus.ABLATIV, "veribus",
                                Kasus.VOKATIV, "vera"
                        )
                )
        );
    }

    @Test
    void testUDeklination() {
        testDeklination(
                Set.of(
                        new NomenWoerterbuchEintrag("senatus", Optional.of("senatus"), Optional.empty()),
                        new NomenWoerterbuchEintrag("senatus", Optional.of("senatus"), Optional.of(Genus.MASKULINUM))
                ),
                Genus.MASKULINUM,
                Map.of(
                        Numerus.SINGULAR, Map.of(
                                Kasus.NOMINATIV, "senatus",
                                Kasus.GENITIV, "senatus",
                                Kasus.DATIV, "senatui",
                                Kasus.AKKUSATIV, "senatum",
                                Kasus.ABLATIV, "senatu",
                                Kasus.VOKATIV, "senatus"
                        ),
                        Numerus.PLURAL, Map.of(
                                Kasus.NOMINATIV, "senatus",
                                Kasus.GENITIV, "senatuum",
                                Kasus.DATIV, "senatibus",
                                Kasus.AKKUSATIV, "senatus",
                                Kasus.ABLATIV, "senatibus",
                                Kasus.VOKATIV, "senatus"
                        )
                )
        );
    }

    @Test
    void testEDeklination() {
        testDeklination(
                Set.of(
                        new NomenWoerterbuchEintrag("res", Optional.of("rei"), Optional.empty()),
                        new NomenWoerterbuchEintrag("res", Optional.of("rei"), Optional.of(Genus.FEMININUM))
                ),
                Genus.FEMININUM,
                Map.of(
                        Numerus.SINGULAR, Map.of(
                                Kasus.NOMINATIV, "res",
                                Kasus.GENITIV, "rei",
                                Kasus.DATIV, "rei",
                                Kasus.AKKUSATIV, "rem",
                                Kasus.ABLATIV, "re",
                                Kasus.VOKATIV, "res"
                        ),
                        Numerus.PLURAL, Map.of(
                                Kasus.NOMINATIV, "res",
                                Kasus.GENITIV, "rerum",
                                Kasus.DATIV, "rebus",
                                Kasus.AKKUSATIV, "res",
                                Kasus.ABLATIV, "rebus",
                                Kasus.VOKATIV, "res"
                        )
                )
        );
    }
}
