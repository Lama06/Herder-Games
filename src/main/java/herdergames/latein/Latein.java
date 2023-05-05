package herdergames.latein;

import herdergames.spiel.Spiel;
import herdergames.util.GewichteteListe;
import herdergames.util.PartikelManager;
import herdergames.util.Steuerung;
import processing.core.PApplet;
import processing.core.PConstants;

import java.util.*;
import java.util.stream.Collectors;

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
public final class Latein extends Spiel.Mehrspieler {
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

    public static void init(PApplet applet) {
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

    public Latein(PApplet applet, Set<Spieler> alleSpieler) {
        super(applet);
        List<Spieler> spielerSortiert = alleSpieler.stream().sorted(Comparator.comparing(spieler -> spieler.id)).toList();
        for (Spieler spieler : spielerSortiert) {
            formenAuswahlen.add(new FormenAuswahl(spieler));
        }
        naechsteForm();
    }

    @Override
    public Optional<List<Spieler.Id>> draw() {
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
    public void keyPressed() {
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
            int formIndex = Arrays.asList(Kasus.values()).indexOf(form.kasus());
            if (form.numerus() == Numerus.PLURAL) {
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
}
