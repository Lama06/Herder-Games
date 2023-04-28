import processing.core.PApplet;
import processing.core.PConstants;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Ein Spiel, das lateinische Nomen und Adjektive in KNG Kongruenz dynamisch aus einer Datenbank mit Grundformen
 * generieren kann. Die Spieler müssen daraufhin entscheiden, um welche Formen es sich dabei handeln kann.
 *
 * Features:
 * - Nomen aller Deklinationen: o, a, kons, u, e
 * - Vokativ auf e in der o-Deklination
 * - Neutrumregel
 * - Adjektive der ao-Deklination und kons-Deklination
 * - Adjektive auf er (zB pulcher, pulchra, pulchrum)
 * - Steigerungen von Adjektiven: Komperativ und Superlativ
 * - Nominalisierte Adjektive
 * - Unregelmäßige Adjektive: is, hic, ille
 * - Unregelmäßige Komperrative und Superlative
 *
 * Herr Schwehmer wäre sicher stolz
 */
final class Latein extends Spiel.Mehrspieler {
    private static final String ADJEKTIV_DATEI = "latein/adjektive.txt";
    private static final String NOMEN_DATEI = "latein/nomen.txt";

    private static final int ADJEKTIV_GESTEIGERT_GEWICHTUNG = 1;
    private static final int ADJEKTIV_GEWICHTUNG = ADJEKTIV_GESTEIGERT_GEWICHTUNG * 5;
    private static final int UNREGELMAESSIGES_ADJEKTIV_GEWICHTUNG = ADJEKTIV_GEWICHTUNG * 25;

    private static final int NOMINALISIERTES_ADJEKTIV_GEWICHTUNG = 1;
    private static final int NOMEN_GEWICHTUNG = NOMINALISIERTES_ADJEKTIV_GEWICHTUNG * 30;

    private static void loadAdjektive(PApplet applet) {
        List<GewichteteListe.Eintrag<Adjektiv>> result = new ArrayList<>();

        String[] adjektivEintraege = applet.loadStrings(ADJEKTIV_DATEI);
        for (String adjektivEintrag : adjektivEintraege) {
            Optional<AdjektivWoerterbuchEintrag> eintrag = AdjektivWoerterbuchEintrag.parse(adjektivEintrag);
            if (eintrag.isEmpty()) {
                PApplet.println("Kein legales Adjektiv: " + adjektivEintrag);
                continue;
            }
            Optional<Adjektiv> adjektiv = eintrag.get().zuAdjektiv();
            if (adjektiv.isEmpty()) {
                PApplet.println("Kein legales Adjektiv: " + adjektivEintrag);
                continue;
            }

            int gewichtung = ADJEKTIV_GEWICHTUNG;
            if (adjektiv.get() instanceof UnregelmaessigesAdjektiv) {
                gewichtung = UNREGELMAESSIGES_ADJEKTIV_GEWICHTUNG;
            }
            result.add(new GewichteteListe.Eintrag<>(adjektiv.get(), gewichtung));
        }

        List<GewichteteListe.Eintrag<Adjektiv>> steigerungen = new ArrayList<>();
        for (GewichteteListe.Eintrag<Adjektiv> adjektiv : result) {
            if (!adjektiv.wert.steigerbar) {
                continue;
            }

            for (Steigerung steigerung : Steigerung.values()) {
                steigerungen.add(new GewichteteListe.Eintrag<>(adjektiv.wert.steigern(steigerung), ADJEKTIV_GESTEIGERT_GEWICHTUNG));
            }
        }
        result.addAll(steigerungen);

        adjektive = Collections.unmodifiableList(result);
    }

    private static void loadNomen(PApplet applet) {
        List<GewichteteListe.Eintrag<Nomen>> result = new ArrayList<>();

        String[] nomenEintraege = applet.loadStrings(NOMEN_DATEI);
        for (String nomenEintrag : nomenEintraege) {
            Optional<NomenWoerterbuchEintrag> eintrag = NomenWoerterbuchEintrag.parse(nomenEintrag);
            if (eintrag.isEmpty()) {
                PApplet.println("Kein legales Nomen: " + nomenEintrag);
                continue;
            }
            Optional<Nomen> nomen = eintrag.get().zuNomen();
            if (nomen.isEmpty()) {
                PApplet.println("Kein legales Nomen: " + nomenEintrag);
                continue;
            }
            result.add(new GewichteteListe.Eintrag<>(nomen.get(), NOMEN_GEWICHTUNG));
        }

        for (GewichteteListe.Eintrag<Adjektiv> adjektiv : adjektive) {
            for (Genus genus : Genus.values()) {
                result.add(new GewichteteListe.Eintrag<>(adjektiv.wert.substantivieren(genus), NOMINALISIERTES_ADJEKTIV_GEWICHTUNG));
            }
        }

        nomen = Collections.unmodifiableList(result);
    }

    static void init(PApplet applet) {
        // Zuerst Adjektive dann Nomen laden, denn Nomen brauchen Adjektive, um diese zu substantivieren
        loadAdjektive(applet);
        loadNomen(applet);
    }

    // Für Herr Schwehmer würde vermutlich eine Sekunde genügen, aber ich brauche etwas mehr
    private static final int FORMEN_PRO_RUNDE = 20;
    private static final int ZEIT_PRO_FORM = 20*60;

    private static List<GewichteteListe.Eintrag<Nomen>> nomen;
    private static List<GewichteteListe.Eintrag<Adjektiv>> adjektive;

    private final PartikelManager partikelManager = new PartikelManager(applet);
    private final Counter counter = new Counter();
    private final AktuellesWortAnzeige aktuellesWortAnzeige = new AktuellesWortAnzeige();
    private final List<FormenAuswahl> formenAuswahlen = new ArrayList<>();

    private int verbleibendeFormen = FORMEN_PRO_RUNDE;
    private Nomen aktuellesNomen;
    private Adjektiv aktuellesAdjektiv;
    private NomenForm aktuelleForm;
    private int verbleibendeZeit;

    Latein(PApplet applet, Set<Spieler> alleSpieler) {
        super(applet);
        List<Spieler> spielerSortiert = alleSpieler.stream().sorted(Comparator.comparing(spieler -> spieler.id)).toList();
        for (Spieler spieler : spielerSortiert) {
            formenAuswahlen.add(new FormenAuswahl(spieler));
        }
        naechsteForm();
    }

    @Override
    Optional<List<Spieler.Id>> draw() {
        if ((verbleibendeZeit -= applet.keyPressed && applet.key == ' ' ? 5 : 1) <= 0) {
            int index = 0;
            for (FormenAuswahl formenAuswahl : formenAuswahlen) {
                formenAuswahl.handleZeitVorbei(index++);
            }

            verbleibendeFormen--;
            if (verbleibendeFormen == 0) {
                List<Spieler.Id> rangliste = formenAuswahlen
                        .stream()
                        .sorted(Collections.reverseOrder(Comparator.comparingInt(formenAuswahl -> formenAuswahl.punkte)))
                        .map(formenAuswahl -> formenAuswahl.spieler.id)
                        .toList();
                return Optional.of(rangliste);
            }

            naechsteForm();
        }

        applet.background(255);

        partikelManager.draw();
        counter.draw();
        aktuellesWortAnzeige.draw();

        int index = 0;
        for (FormenAuswahl formenAuswahl : formenAuswahlen) {
            formenAuswahl.draw(index++);
        }

        return Optional.empty();
    }

    @Override
    void keyPressed() {
        for (FormenAuswahl formenAuswahl : formenAuswahlen) {
            formenAuswahl.keyPressed();
        }
    }

    private void naechsteForm() {
        aktuelleForm = NomenForm.zufaellig(applet);
        aktuellesNomen = GewichteteListe.zufaellig(applet, nomen);
        aktuellesAdjektiv = GewichteteListe.zufaellig(applet, adjektive);
        verbleibendeZeit = ZEIT_PRO_FORM;
    }

    private String getAktuellesWort() {
        return aktuelleForm.zuWort(aktuellesNomen, aktuellesAdjektiv);
    }
    
    private Set<NomenForm> getRichtigeFormen() {
        return Arrays.stream(Numerus.values())
                .flatMap(numerus -> Arrays.stream(Kasus.values()).map(kasus -> new NomenForm(numerus, kasus)))
                .filter(form -> form.zuWort(aktuellesNomen, aktuellesAdjektiv).equals(getAktuellesWort()))
                .collect(Collectors.toUnmodifiableSet());
    }
    
    private final class Counter {
        private static final float HOEHE = 0.1f;
        private static final float TEXT_SIZE = HOEHE/2;
        private static final float X = 0.5f;
        private static final float Y = HOEHE/2;
        
        private void draw() {
            int farbe;
            if (verbleibendeZeit <= 3) {
                farbe = applet.color(255, 0, 0);
            } else {
                farbe = applet.color(0);
            }
            applet.fill(farbe);

            applet.textAlign(PConstants.CENTER, PConstants.CENTER);
            applet.textSize(TEXT_SIZE * applet.height);
            applet.text(Integer.toString(verbleibendeZeit / 60), applet.width * X, applet.height * Y);
        }
    }
    
    private final class AktuellesWortAnzeige {
        private static final float HOEHE = 0.2f;
        private static final float TEXT_SIZE = HOEHE/2;
        private static final float X = 0.5f;
        private static final float Y = Counter.HOEHE + HOEHE/2;
        
        private void draw() {
            applet.textAlign(PConstants.CENTER, PConstants.CENTER);
            applet.textSize(TEXT_SIZE * applet.height);
            applet.fill(applet.color(0));
            applet.text(getAktuellesWort(), applet.width * X, applet.height * Y);
        }
    }

    private final class FormenAuswahl {
        private static final float Y = Counter.HOEHE + AktuellesWortAnzeige.HOEHE;
        private static final float HOEHE = 1 - Y;
        private static final float FORM_HOEHE = HOEHE / NomenForm.ANZAHL;
        private static final float FORM_TEXT_SIZE = FORM_HOEHE * (2f/3f);
        private static final float FORM_TEXT_SIZE_AKTIVIERT = FORM_HOEHE;

        private final Spieler spieler;
        private final Set<NomenForm> aktivierteFormen = new HashSet<>();
        private int ausgewaehlteFormIndex;
        private int punkte;

        private FormenAuswahl(Spieler spieler) {
            this.spieler = spieler;
        }

        private void handleZeitVorbei(int index) {
            Set<NomenForm> richtigeFormen = getRichtigeFormen();
            for (NomenForm aktivierteForm : aktivierteFormen) {
                if (richtigeFormen.contains(aktivierteForm)) {
                    partikelManager.spawnPartikel(getFormX(index), getFormY(aktivierteForm), 50);
                    punkte++;
                } else {
                    punkte--;
                }
            }
            aktivierteFormen.clear();
        }

        private NomenForm getAusgewaehlteForm() {
            Numerus numerus = ausgewaehlteFormIndex < Kasus.values().length ? Numerus.SINGULAR : Numerus.PLURAL;
            Kasus kasus = Kasus.values()[ausgewaehlteFormIndex % Kasus.values().length];
            return new NomenForm(numerus, kasus);
        }

        private void keyPressed() {
            if (Steuerung.Richtung.OBEN.istTasteGedrueckt(applet, spieler.id)) {
                if (ausgewaehlteFormIndex > 0) {
                    ausgewaehlteFormIndex--;
                } else {
                    ausgewaehlteFormIndex = NomenForm.ANZAHL-1;
                }
            }
            if (Steuerung.Richtung.UNTEN.istTasteGedrueckt(applet, spieler.id)) {
                if (ausgewaehlteFormIndex < NomenForm.ANZAHL-1) {
                    ausgewaehlteFormIndex++;
                } else {
                    ausgewaehlteFormIndex = 0;
                }
            }
            if (Steuerung.Richtung.LINKS.istTasteGedrueckt(applet, spieler.id)) {
                aktivierteFormen.remove(getAusgewaehlteForm());
            }
            if (Steuerung.Richtung.RECHTS.istTasteGedrueckt(applet, spieler.id)) {
                aktivierteFormen.add(getAusgewaehlteForm());
            }
        }

        private float getFormX(int index) {
            float breite = 1f / formenAuswahlen.size();
            return breite*index + breite/2;
        }

        private float getFormY(NomenForm form) {
            int formIndex = Arrays.asList(Kasus.values()).indexOf(form.kasus);
            if (form.numerus == Numerus.PLURAL) {
                formIndex += Kasus.values().length;
            }
            return Y + FORM_HOEHE*formIndex + FORM_HOEHE/2;
        }

        private void draw(int index) {
            for (Numerus numerus : Numerus.values()) {
                for (Kasus kasus : Kasus.values()) {
                    NomenForm form = new NomenForm(numerus, kasus);

                    int farbe;
                    if (getAusgewaehlteForm().equals(form)) {
                        farbe = applet.color(0, 255, 0);
                    } else {
                        farbe = applet.color(0);
                    }
                    applet.fill(farbe);

                    float textSize;
                    if (aktivierteFormen.contains(form)) {
                        textSize = FORM_TEXT_SIZE_AKTIVIERT;
                    } else {
                        textSize = FORM_TEXT_SIZE;
                    }
                    applet.textSize(textSize * applet.height);

                    applet.textAlign(PConstants.CENTER, PConstants.CENTER);
                    applet.text(form.toString(), applet.width * getFormX(index), applet.height * getFormY(form));
                }
            }
        }
    }

    private enum Numerus {
        SINGULAR("Singular"),
        PLURAL("Plural");

        private final String string;

        Numerus(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }
    }

    private enum Kasus {
        NOMINATIV("Nominativ"),
        GENITIV("Genitiv"),
        DATIV("Dativ"),
        AKKUSATIV("Akkusativ"),
        ABLATIV("Ablativ"),
        VOKATIV("Vokativ");

        private final String string;

        Kasus(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }
    }

    private enum Genus {
        MASKULINUM,
        FEMININUM,
        NEUTRUM;

        private static Optional<Genus> parse(char buchstabe) {
            switch (buchstabe) {
                case 'm':
                    return Optional.of(MASKULINUM);
                case 'f':
                    return Optional.of(FEMININUM);
                case 'n':
                    return Optional.of(NEUTRUM);
                default:
                    return Optional.empty();
            }
        }
    }
    
    private static final class NomenForm {
        private static final int ANZAHL = 12;

        public static NomenForm zufaellig(PApplet applet) {
            Numerus numerus = Numerus.values()[applet.choice(Numerus.values().length)];
            Kasus kasus = Kasus.values()[applet.choice(Kasus.values().length)];
            return new NomenForm(numerus, kasus);
        }

        private final Numerus numerus;
        private final Kasus kasus;

        private NomenForm(Numerus numerus, Kasus kasus) {
            this.numerus = Objects.requireNonNull(numerus);
            this.kasus = Objects.requireNonNull(kasus);
        }

        private String zuWort(Nomen nomen, Adjektiv adjektiv) {
            String nomenForm = nomen.deklinieren(numerus, kasus);
            String adjektivForm = adjektiv.deklinieren(nomen.genus, numerus, kasus);
            return nomenForm + " " + adjektivForm;
        }

        @Override
        public String toString() {
            return kasus + " " + numerus;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NomenForm nomenForm = (NomenForm) o;
            return numerus == nomenForm.numerus && kasus == nomenForm.kasus;
        }

        @Override
        public int hashCode() {
            return Objects.hash(numerus, kasus);
        }
    }

    private static final class NomenWoerterbuchEintrag {
        private static Optional<NomenWoerterbuchEintrag> parse(String text) {
            String[] elemente = text.split(" ");

            if (elemente.length == 1) {
                return Optional.of(new NomenWoerterbuchEintrag(elemente[0], Optional.empty(), Optional.empty()));
            }

            if (elemente.length == 2) {
                return Optional.of(new NomenWoerterbuchEintrag(elemente[0], Optional.of(elemente[1]), Optional.empty()));
            }

            if (elemente.length == 3) {
                if (elemente[2].length() != 1) {
                    return Optional.empty();
                }
                char genusBuchstabe = elemente[2].charAt(0);
                Optional<Genus> genus = Genus.parse(genusBuchstabe);
                if (genus.isEmpty()) {
                    return Optional.empty();
                }
                return Optional.of(new NomenWoerterbuchEintrag(elemente[0], Optional.of(elemente[1]), genus));
            }

            return Optional.empty();
        }

        private final String nominativ;
        private final Optional<String> genitiv;
        private final Optional<Genus> genus;

        private NomenWoerterbuchEintrag(String nominativ, Optional<String> genitiv, Optional<Genus> genus) {
            this.nominativ = Objects.requireNonNull(nominativ);
            this.genitiv = Objects.requireNonNull(genitiv);
            this.genus = Objects.requireNonNull(genus);
        }

        private Optional<Nomen> zuNomen() {
            return Stream.of(
                    KonsNomenDeklinationMF.INSTANCE,
                    KonsNomenDeklinationN.INSTANCE,
                    ONomenDeklinationMF.INSTANCE,
                    ONomenDeklinationN.INSTANCE,
                    ANomenDeklination.INSTANCE,
                    UNomenDeklinations.INSTANCE,
                    ENomenDeklination.INSTANCE
            ).map(nomenDeklination -> nomenDeklination.parse(this))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findAny();
        }
    }

    private static abstract class NomenDeklination {
        abstract Optional<Nomen> parseImpl(String nominativ, Optional<String> genitiv, Genus genus);

        final Optional<Nomen> parse(NomenWoerterbuchEintrag eintrag) {
            Genus genus;
            if (eintrag.genus.isPresent()) {
                if (!getErlaubteGenuse().contains(eintrag.genus.get())) {
                    return Optional.empty();
                }
                genus = eintrag.genus.get();
            } else {
                if (getStandardGenus().isEmpty()) {
                    return Optional.empty();
                }
                genus = getStandardGenus().get();
            }
            return parseImpl(eintrag.nominativ, eintrag.genitiv, genus);
        }

        abstract Optional<Genus> getStandardGenus();

        abstract Set<Genus> getErlaubteGenuse();
    }

    private static abstract class Nomen {
        private final Genus genus;

        private Nomen(Genus genus) {
            this.genus = genus;
        }

        abstract String deklinieren(Numerus numerus, Kasus kasus);
    }

    private static abstract class StammNomenDeklination extends NomenDeklination {
        abstract boolean istGenitivNotwendig();

        abstract Map<Numerus, Map<Kasus, String>> getEndungen();

        Optional<String> getStammAbhaengigeEndung(Numerus numerus, Kasus kasus, String stamm) {
            return Optional.empty();
        }

        @Override
        Optional<Nomen> parseImpl(String nominativ, Optional<String> genitiv, Genus genus) {
            String nominativEndung = getEndungen().get(Numerus.SINGULAR).get(Kasus.NOMINATIV);
            String genitivEndung = getEndungen().get(Numerus.SINGULAR).get(Kasus.GENITIV);

            if (!nominativ.endsWith(nominativEndung)) {
                return Optional.empty();
            }
            String stamm = nominativ.substring(0, nominativ.length()-nominativEndung.length());

            if (istGenitivNotwendig() && genitiv.isEmpty()) {
                return Optional.empty();
            }
            if (genitiv.isPresent() && !genitiv.get().equals(stamm + genitivEndung)) {
                return Optional.empty();
            }

            return Optional.of(new StammNomen(genus, this, stamm));
        }
    }

    private static final class StammNomen extends Nomen {
        private final StammNomenDeklination deklination;
        private final String stamm;

        private StammNomen(Genus genus, StammNomenDeklination deklination, String stamm) {
            super(genus);
            this.stamm = stamm;
            this.deklination = deklination;
        }

        @Override
        String deklinieren(Numerus numerus, Kasus kasus) {
            String endung;
            if (deklination.getStammAbhaengigeEndung(numerus, kasus, stamm).isPresent()) {
                endung = deklination.getStammAbhaengigeEndung(numerus, kasus, stamm).get();
            } else {
                endung = deklination.getEndungen().get(numerus).get(kasus);
            }

            return stamm + endung;
        }
    }

    private static final class KonsNomenDeklinationMF extends NomenDeklination {
        private static final KonsNomenDeklinationMF INSTANCE = new KonsNomenDeklinationMF();

        private static final Map<Numerus, Map<Kasus, String>> ENDUNGEN = Map.of(
                Numerus.SINGULAR, Map.of(
                        Kasus.GENITIV, "is",
                        Kasus.DATIV, "i",
                        Kasus.AKKUSATIV, "em",
                        Kasus.ABLATIV, "e"
                ),
                Numerus.PLURAL, Map.of(
                        Kasus.NOMINATIV, "es",
                        Kasus.GENITIV, "um",
                        Kasus.DATIV, "ibus",
                        Kasus.AKKUSATIV, "es",
                        Kasus.ABLATIV, "ibus",
                        Kasus.VOKATIV, "es"
                )
        );

        @Override
        Optional<Nomen> parseImpl(String nominativ, Optional<String> genitiv, Genus genus) {
            if (genitiv.isEmpty() || !genitiv.get().endsWith("is")) {
                return Optional.empty();
            }
            String stamm = genitiv.get().substring(0, genitiv.get().length()-"is".length());
            return Optional.of(new KonsNomenMF(genus, nominativ, stamm));
        }

        @Override
        Optional<Genus> getStandardGenus() {
            return Optional.empty();
        }

        @Override
        Set<Genus> getErlaubteGenuse() {
            return Set.of(Genus.MASKULINUM, Genus.FEMININUM);
        }
    }

    private static final class KonsNomenMF extends Nomen {
        private final String nominativSingular;
        private final String stamm;

        private KonsNomenMF(Genus genus, String nominativSingular, String stamm) {
            super(genus);
            this.nominativSingular = nominativSingular;
            this.stamm = stamm;
        }

        @Override
        String deklinieren(Numerus numerus, Kasus kasus) {
            if (numerus == Numerus.SINGULAR && (Set.of(Kasus.NOMINATIV, Kasus.VOKATIV).contains(kasus))) {
                return nominativSingular;
            }

            return stamm + KonsNomenDeklinationMF.ENDUNGEN.get(numerus).get(kasus);
        }
    }

    private static final class KonsNomenDeklinationN extends NomenDeklination {
        private static final KonsNomenDeklinationN INSTANCE = new KonsNomenDeklinationN();

        private static final Map<Numerus, Map<Kasus, String>> ENDUNGEN = Map.of(
                Numerus.SINGULAR, Map.of(
                        Kasus.GENITIV, "is",
                        Kasus.DATIV, "i",
                        Kasus.ABLATIV, "e"
                ),
                Numerus.PLURAL, Map.of(
                        Kasus.NOMINATIV, "a",
                        Kasus.GENITIV, "um",
                        Kasus.DATIV, "ibus",
                        Kasus.AKKUSATIV, "a",
                        Kasus.ABLATIV, "ibus",
                        Kasus.VOKATIV, "a"
                )
        );

        @Override
        Optional<Nomen> parseImpl(String nominativ, Optional<String> genitiv, Genus genus) {
            if (genitiv.isEmpty() || !genitiv.get().endsWith("is")) {
                return Optional.empty();
            }
            String stamm = genitiv.get().substring(0, genitiv.get().length()-"is".length());
            return Optional.of(new KonsNomenN(genus, nominativ, stamm));
        }

        @Override
        Optional<Genus> getStandardGenus() {
            return Optional.empty();
        }

        @Override
        Set<Genus> getErlaubteGenuse() {
            return Set.of(Genus.NEUTRUM);
        }
    }

    private static final class KonsNomenN extends Nomen {
        private final String nominativSingular;
        private final String stamm;

        private KonsNomenN(Genus genus, String nominativSingular, String stamm) {
            super(genus);
            this.nominativSingular = nominativSingular;
            this.stamm = stamm;
        }

        @Override
        String deklinieren(Numerus numerus, Kasus kasus) {
            if (numerus == Numerus.SINGULAR && (Set.of(Kasus.NOMINATIV, Kasus.AKKUSATIV, Kasus.VOKATIV).contains(kasus))) {
                return nominativSingular;
            }

            return stamm + KonsNomenDeklinationN.ENDUNGEN.get(numerus).get(kasus);
        }
    }

    private static final class ONomenDeklinationMF extends StammNomenDeklination {
        private static final ONomenDeklinationMF INSTANCE = new ONomenDeklinationMF();

        @Override
        Optional<Genus> getStandardGenus() {
            return Optional.of(Genus.MASKULINUM);
        }

        @Override
        Set<Genus> getErlaubteGenuse() {
            return Set.of(Genus.MASKULINUM, Genus.FEMININUM);
        }

        @Override
        boolean istGenitivNotwendig() {
            return false;
        }

        @Override
        Map<Numerus, Map<Kasus, String>> getEndungen() {
            return Map.of(
                    Numerus.SINGULAR, Map.of(
                            Kasus.NOMINATIV, "us",
                            Kasus.GENITIV, "i",
                            Kasus.DATIV, "o",
                            Kasus.AKKUSATIV, "um",
                            Kasus.ABLATIV, "o"
                    ),
                    Numerus.PLURAL, Map.of(
                            Kasus.NOMINATIV, "i",
                            Kasus.GENITIV, "orum",
                            Kasus.DATIV, "is",
                            Kasus.AKKUSATIV, "os",
                            Kasus.ABLATIV, "is",
                            Kasus.VOKATIV, "i"
                    )
            );
        }

        @Override
        Optional<String> getStammAbhaengigeEndung(Numerus numerus, Kasus kasus, String stamm) {
            if (numerus != Numerus.SINGULAR || kasus != Kasus.VOKATIV) {
                return Optional.empty();
            }

            if (stamm.endsWith("i")) {
                return Optional.of(""); // Vokativ von Albius ist Albi
            }

            return Optional.of("e"); // Et tu, Brute :)
        }
    }

    private static final class ONomenDeklinationN extends StammNomenDeklination {
        private static final ONomenDeklinationN INSTANCE = new ONomenDeklinationN();

        @Override
        Optional<Genus> getStandardGenus() {
            return Optional.of(Genus.NEUTRUM);
        }

        @Override
        Set<Genus> getErlaubteGenuse() {
            return Set.of(Genus.NEUTRUM);
        }

        @Override
        boolean istGenitivNotwendig() {
            return false;
        }

        @Override
        Map<Numerus, Map<Kasus, String>> getEndungen() {
            return Map.of(
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
            );
        }
    }

    private static final class ANomenDeklination extends StammNomenDeklination {
        private static final ANomenDeklination INSTANCE = new ANomenDeklination();

        @Override
        Optional<Genus> getStandardGenus() {
            return Optional.of(Genus.FEMININUM);
        }

        @Override
        Set<Genus> getErlaubteGenuse() {
            return Set.of(Genus.FEMININUM, Genus.MASKULINUM);
        }

        @Override
        boolean istGenitivNotwendig() {
            return false;
        }

        @Override
        Map<Numerus, Map<Kasus, String>> getEndungen() {
            return Map.of(
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
            );
        }
    }

    private static final class UNomenDeklinations extends StammNomenDeklination {
        private static final UNomenDeklinations INSTANCE = new UNomenDeklinations();

        @Override
        Optional<Genus> getStandardGenus() {
            return Optional.of(Genus.MASKULINUM);
        }

        @Override
        Set<Genus> getErlaubteGenuse() {
            return Set.of(Genus.MASKULINUM, Genus.FEMININUM);
        }

        @Override
        boolean istGenitivNotwendig() {
            return true; // Um Verwechselung mit O-Deklination zu vermeiden
        }

        @Override
        Map<Numerus, Map<Kasus, String>> getEndungen() {
            return Map.of(
                    Numerus.SINGULAR, Map.of(
                            Kasus.NOMINATIV, "us",
                            Kasus.GENITIV, "us",
                            Kasus.DATIV, "ui",
                            Kasus.AKKUSATIV, "um",
                            Kasus.ABLATIV, "u",
                            Kasus.VOKATIV, "us"
                    ),
                    Numerus.PLURAL, Map.of(
                            Kasus.NOMINATIV, "us",
                            Kasus.GENITIV, "uum",
                            Kasus.DATIV, "ibus",
                            Kasus.AKKUSATIV, "us",
                            Kasus.ABLATIV, "ibus",
                            Kasus.VOKATIV, "us"
                    )
            );
        }
    }

    private static final class ENomenDeklination extends StammNomenDeklination {
        private static final ENomenDeklination INSTANCE = new ENomenDeklination();

        @Override
        Optional<Genus> getStandardGenus() {
            return Optional.of(Genus.FEMININUM);
        }

        @Override
        Set<Genus> getErlaubteGenuse() {
            return Set.of(Genus.MASKULINUM, Genus.FEMININUM);
        }

        @Override
        boolean istGenitivNotwendig() {
            return true;
        }

        @Override
        Map<Numerus, Map<Kasus, String>> getEndungen() {
            return Map.of(
                    Numerus.SINGULAR, Map.of(
                            Kasus.NOMINATIV, "es",
                            Kasus.GENITIV, "ei",
                            Kasus.DATIV, "ei",
                            Kasus.AKKUSATIV, "em",
                            Kasus.ABLATIV, "e",
                            Kasus.VOKATIV, "es"
                    ),
                    Numerus.PLURAL, Map.of(
                            Kasus.NOMINATIV, "es",
                            Kasus.GENITIV, "erum",
                            Kasus.DATIV, "ebus",
                            Kasus.AKKUSATIV, "es",
                            Kasus.ABLATIV, "ebus",
                            Kasus.VOKATIV, "es"
                    )
            );
        }
    }

    private static final class AdjektivWoerterbuchEintrag {
        private static Optional<AdjektivWoerterbuchEintrag> parse(String text) {
            String[] elemente = text.split(" ");

            if (elemente.length == 1) {
                return Optional.of(new AdjektivWoerterbuchEintrag(elemente[0], Optional.empty(), Optional.empty()));
            }

            if (elemente.length == 2) {
                return Optional.of(new AdjektivWoerterbuchEintrag(elemente[0], Optional.of(elemente[1]), Optional.empty()));
            }

            if (elemente.length == 3) {
                return Optional.of(new AdjektivWoerterbuchEintrag(elemente[0], Optional.of(elemente[1]), Optional.of(elemente[2])));
            }

            return Optional.empty();
        }

        private final String ersteForm;
        private final Optional<String> zweiteForm;
        private final Optional<String> dritteForm;

        private AdjektivWoerterbuchEintrag(String ersteForm, Optional<String> zweiteForm, Optional<String> dritteForm) {
            this.ersteForm = Objects.requireNonNull(ersteForm);
            this.zweiteForm = Objects.requireNonNull(zweiteForm);
            this.dritteForm = Objects.requireNonNull(dritteForm);
        }

        private Optional<Adjektiv> zuAdjektiv() {
            return Stream.of(
                            AOAdjektivDeklination.INSTANCE,
                            KonsAdjektivDeklination.INSTANCE,
                            HicDeklination.INSTANCE,
                            IlleDeklination.INSTANCE,
                            IsDeklination.INSTANCE
                    ).map(adjektivDeklination -> adjektivDeklination.parse(this))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findAny();
        }
    }

    private enum Steigerung {
        KOMPERATIV,
        SUPERLATIV
    }

    private static abstract class AdjektivDeklination {
        abstract Optional<Adjektiv> parse(AdjektivWoerterbuchEintrag eintrag);
    }

    private static abstract class Adjektiv {
        private final boolean steigerbar;

        private Adjektiv(boolean steigerbar) {
            this.steigerbar = steigerbar;
        }

        abstract String deklinieren(Genus genus, Numerus numerus, Kasus kasus);

        abstract Adjektiv steigern(Steigerung steigerung);

        private Nomen substantivieren(Genus genus) {
            return new Nomen(genus) {
                @Override
                String deklinieren(Numerus numerus, Kasus kasus) {
                    return Adjektiv.this.deklinieren(genus, numerus, kasus);
                }
            };
        }
    }

    private static final class AOAdjektivDeklination extends AdjektivDeklination {
        private static final AOAdjektivDeklination INSTANCE = new AOAdjektivDeklination();

        private static final Map<Genus, Map<Numerus, Map<Kasus, String>>> ENDUNGEN = Map.of(
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
            // bonus a um

            if (!eintrag.ersteForm.endsWith("us")) {
                return Optional.empty();
            }
            String stamm = eintrag.ersteForm.substring(0, eintrag.ersteForm.length()-"us".length());

            if (eintrag.zweiteForm.isPresent() || eintrag.dritteForm.isPresent()) {
                return Optional.empty();
            }

            return Optional.of(new AOAdjektiv(stamm, Optional.empty()));
        }

        private Optional<Adjektiv> parseAUmLang(AdjektivWoerterbuchEintrag eintrag) {
            // bonus a um

            if (!eintrag.ersteForm.endsWith("us")) {
                return Optional.empty();
            }
            String stamm = eintrag.ersteForm.substring(0, eintrag.ersteForm.length()-"us".length());

            if (eintrag.zweiteForm.isEmpty() || !eintrag.zweiteForm.get().equals("a")) {
                return Optional.empty();
            }

            if (eintrag.dritteForm.isEmpty() || !eintrag.dritteForm.get().equals("um")) {
                return Optional.empty();
            }

            return Optional.of(new AOAdjektiv(stamm, Optional.empty()));
        }

        private Optional<Adjektiv> parseAUmKomplett(AdjektivWoerterbuchEintrag eintrag) {
            // bonus bona bonum

            if (!eintrag.ersteForm.endsWith("us")) {
                return Optional.empty();
            }
            String stamm = eintrag.ersteForm.substring(0, eintrag.ersteForm.length()-"us".length());

            if (eintrag.zweiteForm.isEmpty() || !eintrag.zweiteForm.get().equals(stamm + "a")) {
                return Optional.empty();
            }

            if (eintrag.dritteForm.isEmpty() || !eintrag.dritteForm.get().equals(stamm + "um")) {
                return Optional.empty();
            }

            return Optional.of(new AOAdjektiv(stamm, Optional.empty()));
        }

        private Optional<Adjektiv> parseEr(AdjektivWoerterbuchEintrag eintrag) {
            // pulcher pulchra pulchrum

            if (!eintrag.ersteForm.endsWith("er")) {
                return Optional.empty();
            }

            if (eintrag.zweiteForm.isEmpty() || !eintrag.zweiteForm.get().endsWith("a")) {
                return Optional.empty();
            }
            String stamm = eintrag.zweiteForm.get().substring(0, eintrag.zweiteForm.get().length()-"a".length());

            if (eintrag.dritteForm.isEmpty() || !eintrag.dritteForm.get().equals(stamm + "um")) {
                return Optional.empty();
            }

            return Optional.of(new AOAdjektiv(stamm, Optional.of(eintrag.ersteForm)));
        }

        @Override
        Optional<Adjektiv> parse(AdjektivWoerterbuchEintrag eintrag) {
            return parseAUmKurz(eintrag)
                    .or(() -> parseAUmLang(eintrag))
                    .or(() -> parseAUmKomplett(eintrag))
                    .or(() -> parseEr(eintrag));
        }
    }

    private static final class AOAdjektiv extends Adjektiv {
        private final String stamm;
        private final Optional<String> nominativSingularM;

        private AOAdjektiv(String stamm, Optional<String> nominativSingularM) {
            super(true);
            this.stamm = Objects.requireNonNull(stamm);
            this.nominativSingularM = Objects.requireNonNull(nominativSingularM);
        }

        @Override
        String deklinieren(Genus genus, Numerus numerus, Kasus kasus) {
            if (
                    genus == Genus.MASKULINUM &&
                    numerus == Numerus.SINGULAR &&
                    (kasus == Kasus.NOMINATIV || kasus == Kasus.VOKATIV) &&
                    nominativSingularM.isPresent()
            ) {
                return nominativSingularM.get();
            }

            return stamm + AOAdjektivDeklination.ENDUNGEN.get(genus).get(numerus).get(kasus);
        }

        @Override
        Adjektiv steigern(Steigerung steigerung) {
            String nominativSingularMasklulinum = deklinieren(Genus.MASKULINUM, Numerus.SINGULAR, Kasus.NOMINATIV);

            switch (steigerung) {
                case KOMPERATIV:
                    return new KomperativAdjektiv(nominativSingularMasklulinum, stamm);
                case SUPERLATIV:
                    return new SuperlativAdjektiv(nominativSingularMasklulinum, stamm);
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    private static final class KonsAdjektivDeklination extends AdjektivDeklination {
        private static final KonsAdjektivDeklination INSTANCE = new KonsAdjektivDeklination();

        private static final Map<Genus, Map<Numerus, Map<Kasus, String>>> ENDUNGEN = Map.of(
                Genus.MASKULINUM, Map.of(
                        Numerus.SINGULAR, Map.of(
                                Kasus.GENITIV, "is",
                                Kasus.DATIV, "i",
                                Kasus.AKKUSATIV, "em",
                                Kasus.ABLATIV, "i"
                        ),
                        Numerus.PLURAL, Map.of(
                                Kasus.NOMINATIV, "es",
                                Kasus.GENITIV, "um",
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
                                Kasus.GENITIV, "um",
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
                                Kasus.GENITIV, "um",
                                Kasus.DATIV, "ibus",
                                Kasus.AKKUSATIV, "ia",
                                Kasus.ABLATIV, "ibus",
                                Kasus.VOKATIV, "ia"
                        )
                )
        );

        private Optional<Adjektiv> parseEinendig(AdjektivWoerterbuchEintrag eintrag) {
            // vehemens vehementis

            String nominativSingular = eintrag.ersteForm;

            if (eintrag.zweiteForm.isEmpty() || !eintrag.zweiteForm.get().endsWith("is")) {
                return Optional.empty();
            }
            String stamm = eintrag.zweiteForm.get().substring(0, eintrag.zweiteForm.get().length()-"is".length());

            if (eintrag.dritteForm.isPresent()) {
                return Optional.empty();
            }

            return Optional.of(new KonsAdjektiv(nominativSingular, nominativSingular, nominativSingular, stamm));
        }

        private Optional<Adjektiv> parseZweiendigKurz(AdjektivWoerterbuchEintrag eintrag) {
            // gravis e

            if (!eintrag.ersteForm.endsWith("is")) {
                return Optional.empty();
            }
            String stamm = eintrag.ersteForm.substring(0, eintrag.ersteForm.length()-"is".length());

            if (eintrag.zweiteForm.isEmpty() || !eintrag.zweiteForm.get().equals("e")) {
                return Optional.empty();
            }
            String nominativSingularNeutrum = stamm + "e";

            if (eintrag.dritteForm.isPresent()) {
                return Optional.empty();
            }

            return Optional.of(new KonsAdjektiv(eintrag.ersteForm, eintrag.ersteForm, nominativSingularNeutrum, stamm));
        }

        private Optional<Adjektiv> parseZweiendigLang(AdjektivWoerterbuchEintrag eintrag) {
            // gravis grave

            if (!eintrag.ersteForm.endsWith("is")) {
                return Optional.empty();
            }
            String stamm = eintrag.ersteForm.substring(0, eintrag.ersteForm.length()-"is".length());

            if (eintrag.zweiteForm.isEmpty() || !eintrag.zweiteForm.get().equals(stamm + "e")) {
                return Optional.empty();
            }

            if (eintrag.dritteForm.isPresent()) {
                return Optional.empty();
            }

            return Optional.of(new KonsAdjektiv(eintrag.ersteForm, eintrag.ersteForm, eintrag.zweiteForm.get(), stamm));
        }

        private Optional<Adjektiv> parseDreiendig(AdjektivWoerterbuchEintrag eintrag) {
            // acer acris acre

            String nominativSingularMaskulinum = eintrag.ersteForm;

            if (eintrag.zweiteForm.isEmpty() || !eintrag.zweiteForm.get().endsWith("is")) {
                return Optional.empty();
            }
            String nominativSingularFemininum = eintrag.zweiteForm.get();
            String stamm = nominativSingularFemininum.substring(0, nominativSingularFemininum.length()-"is".length());

            if (eintrag.dritteForm.isEmpty() || !eintrag.dritteForm.get().equals(stamm + "e")) {
                return Optional.empty();
            }
            String nominativSingularNeutrum = eintrag.dritteForm.get();

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

    private static final class KonsAdjektiv extends Adjektiv {
        private final String nominativSingularMaskulinum;
        private final String nominativSingularFemininum;
        private final String nominativSingularNeutrum;
        private final String stamm;

        private KonsAdjektiv(
                String nominativSingularMaskulinum,
                String nominativSingularFemininum,
                String nominativSingularNeutrum,
                String stamm
        ) {
            super(true);
            this.nominativSingularMaskulinum = Objects.requireNonNull(nominativSingularMaskulinum);
            this.nominativSingularFemininum = Objects.requireNonNull(nominativSingularFemininum);
            this.nominativSingularNeutrum = Objects.requireNonNull(nominativSingularNeutrum);
            this.stamm = Objects.requireNonNull(stamm);
        }

        @Override
        String deklinieren(Genus genus, Numerus numerus, Kasus kasus) {
            if (numerus == Numerus.SINGULAR && (kasus == Kasus.NOMINATIV || kasus == Kasus.VOKATIV)) {
                switch (genus) {
                    case MASKULINUM:
                        return nominativSingularMaskulinum;
                    case FEMININUM:
                        return nominativSingularFemininum;
                    case NEUTRUM:
                        return nominativSingularNeutrum;
                    default:
                        throw new IllegalArgumentException();
                }
            }

            if (genus == Genus.NEUTRUM && numerus == Numerus.SINGULAR && kasus == Kasus.AKKUSATIV) {
                return nominativSingularNeutrum;
            }

            return stamm + KonsAdjektivDeklination.ENDUNGEN.get(genus).get(numerus).get(kasus);
        }

        @Override
        Adjektiv steigern(Steigerung steigerung) {
            switch (steigerung) {
                case KOMPERATIV:
                    return new KomperativAdjektiv(nominativSingularMaskulinum, stamm);
                case SUPERLATIV:
                    return new SuperlativAdjektiv(nominativSingularMaskulinum, stamm);
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    private static abstract class UnregelmaessigeAdjektivDeklination extends AdjektivDeklination {
        abstract Map<Genus, Map<Numerus, Map<Kasus, String>>> getFormen();

        @Override
        Optional<Adjektiv> parse(AdjektivWoerterbuchEintrag eintrag) {
            String nominativSingularMaskulinum = getFormen().get(Genus.MASKULINUM).get(Numerus.SINGULAR).get(Kasus.NOMINATIV);
            String nominativSingularFemininum = getFormen().get(Genus.FEMININUM).get(Numerus.SINGULAR).get(Kasus.NOMINATIV);
            String nominativSingularNeutrum = getFormen().get(Genus.NEUTRUM).get(Numerus.SINGULAR).get(Kasus.NOMINATIV);

            if (eintrag.ersteForm.isEmpty() || !eintrag.ersteForm.equals(nominativSingularMaskulinum) ||
                    eintrag.zweiteForm.isEmpty() || !eintrag.zweiteForm.get().equals(nominativSingularFemininum) ||
                    eintrag.dritteForm.isEmpty() || !eintrag.dritteForm.get().equals(nominativSingularNeutrum)) {
                return Optional.empty();
            }

            return Optional.of(new UnregelmaessigesAdjektiv(this));
        }
    }

    private static final class UnregelmaessigesAdjektiv extends Adjektiv {
        private final UnregelmaessigeAdjektivDeklination deklination;

        private UnregelmaessigesAdjektiv(UnregelmaessigeAdjektivDeklination deklination) {
            super(false);
            this.deklination = Objects.requireNonNull(deklination);
        }

        @Override
        String deklinieren(Genus genus, Numerus numerus, Kasus kasus) {
            return deklination.getFormen().get(genus).get(numerus).get(kasus);
        }

        @Override
        Adjektiv steigern(Steigerung steigerung)  {
            throw new UnsupportedOperationException();
        }
    }

    private static final class HicDeklination extends UnregelmaessigeAdjektivDeklination {
        private static final HicDeklination INSTANCE = new HicDeklination();

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

    private static final class IlleDeklination extends UnregelmaessigeAdjektivDeklination {
        private static final IlleDeklination INSTANCE = new IlleDeklination();

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

    private static final class IsDeklination extends UnregelmaessigeAdjektivDeklination {
        private static final IsDeklination INSTANCE = new IsDeklination();

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

    private static final class KomperativAdjektiv extends Adjektiv {
        private static final String NOMINATIV_SINGULAR_MASKULINUM_FEMININUM_ENDUNG = "ior";
        private static final String NOMINATIV_SINGULAR_NEUTRUM_ENDUNG = "ius";
        private static final String KENNZEICHEN = "ior";

        private static final Map<String, String> UNREGELMAESSIGE_KOMPERATIVE = Map.of(
            "bonus", "meli",
            "malus", "pe",
            "magnus", "ma"
        );

        private final String stamm;

        private KomperativAdjektiv(String nominativSingularMaskulinum, String stamm) {
            super(false);
            if (UNREGELMAESSIGE_KOMPERATIVE.containsKey(nominativSingularMaskulinum)) {
                this.stamm = UNREGELMAESSIGE_KOMPERATIVE.get(nominativSingularMaskulinum);
                return;
            }
            this.stamm = Objects.requireNonNull(stamm);
        }

        @Override
        String deklinieren(Genus genus, Numerus numerus, Kasus kasus) {
            if (numerus == Numerus.SINGULAR && (kasus == Kasus.NOMINATIV || kasus == Kasus.VOKATIV)) {
                switch (genus) {
                    case MASKULINUM:
                    case FEMININUM:
                        return stamm + NOMINATIV_SINGULAR_MASKULINUM_FEMININUM_ENDUNG;
                    case NEUTRUM:
                        return stamm + NOMINATIV_SINGULAR_NEUTRUM_ENDUNG;
                }
            }

            if (genus == Genus.NEUTRUM && numerus == Numerus.SINGULAR && kasus == Kasus.AKKUSATIV) {
                return stamm + NOMINATIV_SINGULAR_NEUTRUM_ENDUNG;
            }

            return stamm + KENNZEICHEN + KonsAdjektivDeklination.ENDUNGEN.get(genus).get(numerus).get(kasus);
        }

        @Override
        Adjektiv steigern(Steigerung steigerung) {
            throw new UnsupportedOperationException();
        }
    }

    private static final class SuperlativAdjektiv extends Adjektiv {
        private static final String KENNZEICHEN = "issim";
        private static final String KENNZEICHEN_ER = "rim";

        private static final Map<String, String> UNREGELMAESSIGE_SUPERLATIVE = Map.of(
                "bonus", "optim",
                "malus", "pessim",
                "magnus", "maxim"
        );

        private final String nominativSingularMaskulinum;
        private final String stamm;

        private SuperlativAdjektiv(String nominativSingularMaskulinum, String stamm) {
            super(false);
            this.nominativSingularMaskulinum = Objects.requireNonNull(nominativSingularMaskulinum);

            if (UNREGELMAESSIGE_SUPERLATIVE.containsKey(nominativSingularMaskulinum)) {
                this.stamm = UNREGELMAESSIGE_SUPERLATIVE.get(nominativSingularMaskulinum);
                return;
            }
            this.stamm = Objects.requireNonNull(stamm);
        }

        @Override
        String deklinieren(Genus genus, Numerus numerus, Kasus kasus) {
            String endung = AOAdjektivDeklination.ENDUNGEN.get(genus).get(numerus).get(kasus);

            if (nominativSingularMaskulinum.endsWith("er")) {
                // Der Superlativ von pulcher ist pulcherrimus
                return nominativSingularMaskulinum + KENNZEICHEN_ER + endung;
            }

            return stamm + KENNZEICHEN + endung;
        }

        @Override
        Adjektiv steigern(Steigerung steigerung) {
            throw new UnsupportedOperationException();
        }
    }
}
