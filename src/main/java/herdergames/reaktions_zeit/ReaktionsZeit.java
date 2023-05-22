package herdergames.reaktions_zeit;

import herdergames.spiel.MehrspielerSpiel;
import herdergames.spiel.Spieler;
import herdergames.util.Rechteck;
import herdergames.util.Steuerung;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.util.*;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingDouble;
import static java.util.Map.Entry.comparingByKey;

public final class ReaktionsZeit extends MehrspielerSpiel {
    private static final float WAHRSCHEINLICHKEIT_REAGIEREN = 1f / (4f * 60f);
    private static final float WAHRSCHEINLICHKEIT_NICHT_REAGIEREN = WAHRSCHEINLICHKEIT_REAGIEREN / 2f;
    private static final int ANZAHL_RUNDEN = 7;
    private static final int NICHT_REAGIEREN_ZEIT = 4 * 60;
    private static final int STRAFZEIT = 30;

    private static PImage michaelKippBild;

    public static void init(PApplet applet) {
        michaelKippBild = applet.loadImage("reaktions_zeit/kipp.png");
    }


    private Status status = Status.NICHTS;
    private int verbleibendeRunden = ANZAHL_RUNDEN;
    private final Map<Spieler, Integer> spielerReaktionsZeitGesamt = new HashMap<>();
    private Rechteck bildRechteck;
    private int reaktionsZeit;
    private Map<Spieler, Integer> spielerReaktionsZeit;
    private int verbleibendeNichtReagierenZeit;

    public ReaktionsZeit(PApplet applet, Set<Spieler> alleSpieler) {
        super(applet);
        for (Spieler spieler : alleSpieler) {
            spielerReaktionsZeitGesamt.put(spieler, 0);
        }
    }

    @Override
    public Optional<List<Spieler.Id>> draw() {
        applet.background(0);

        switch (status) {
            case NICHTS -> {
                if (verbleibendeRunden == 0) {
                    List<Spieler.Id> rangliste = spielerReaktionsZeitGesamt.keySet()
                            .stream()
                            .sorted(comparingDouble(spielerReaktionsZeitGesamt::get))
                            .map(Spieler::id)
                            .toList();
                    return Optional.of(rangliste);
                }

                if (applet.random(1f) <= WAHRSCHEINLICHKEIT_REAGIEREN) {
                    status = Status.REAGIEREN;
                    bildRechteck = zufaelligeBildPosition();
                    reaktionsZeit = 0;
                    spielerReaktionsZeit = new HashMap<>();
                    break;
                }

                if (applet.random(1f) <= WAHRSCHEINLICHKEIT_NICHT_REAGIEREN) {
                    status = Status.NICHT_REAGIEREN;
                    verbleibendeNichtReagierenZeit = NICHT_REAGIEREN_ZEIT;
                    bildRechteck = zufaelligeBildPosition();
                }
            }
            case REAGIEREN -> {
                if (spielerReaktionsZeit.size() == spielerReaktionsZeitGesamt.size()) {
                    status = Status.NICHTS;
                    spielerReaktionsZeit.forEach((spieler, zeit) -> {
                        int zeitDavor = spielerReaktionsZeitGesamt.get(spieler);
                        spielerReaktionsZeitGesamt.put(spieler, zeitDavor + zeit);
                    });
                    verbleibendeRunden--;
                    break;
                }

                reaktionsZeit++;

                applet.imageMode(PConstants.CORNER);
                applet.noTint();
                applet.image(
                        michaelKippBild,
                        bildRechteck.x() * applet.width,
                        bildRechteck.y() * applet.height,
                        bildRechteck.breite() * applet.width,
                        bildRechteck.hoehe() * applet.height
                );
            }
            case NICHT_REAGIEREN -> {
                if (verbleibendeNichtReagierenZeit-- <= 0) {
                    status = Status.NICHTS;
                    verbleibendeRunden--;
                    break;
                }

                applet.imageMode(PConstants.CORNER);
                applet.tint(255, 0, 0);
                applet.image(
                        michaelKippBild,
                        bildRechteck.x() * applet.width,
                        bildRechteck.y() * applet.height,
                        bildRechteck.breite() * applet.width,
                        bildRechteck.hoehe() * applet.height
                );
            }
        }

        drawGesamtZeiten();

        return Optional.empty();
    }

    private void drawGesamtZeiten() {
        Map<Spieler, Integer> angezeigteZeiten = new HashMap<>(spielerReaktionsZeitGesamt);
        if (status == Status.REAGIEREN) {
            for (Spieler spieler : angezeigteZeiten.keySet()) {
                if (spielerReaktionsZeit.containsKey(spieler)) {
                    angezeigteZeiten.put(spieler, angezeigteZeiten.get(spieler) + spielerReaktionsZeit.get(spieler));
                } else {
                    angezeigteZeiten.put(spieler, angezeigteZeiten.get(spieler) + reaktionsZeit);
                }
            }
        }

        float x = applet.width / 2f;
        float y = 0.1f * applet.height;
        float textSize = 0.05f * applet.height;

        List<Map.Entry<Spieler, Integer>> angezeigteZeitenSortiert = angezeigteZeiten.entrySet().stream().sorted(comparingByKey(comparing(Spieler::id))).toList();
        for (Map.Entry<Spieler, Integer> angezeigteZeitEintrag : angezeigteZeitenSortiert) {
            int millisekunden = (int) ((angezeigteZeitEintrag.getValue() / 60f) * 1000f);

            applet.fill(255);
            applet.textAlign(PConstants.CENTER, PConstants.TOP);
            applet.textSize(textSize);
            applet.text(angezeigteZeitEintrag.getKey().name() + ": " + millisekunden + "ms", x, y);

            y += textSize;
        }
    }

    private Rechteck zufaelligeBildPosition() {
        float minSize = 0.3f;
        float x = applet.random(1 - minSize);
        float y = applet.random(1 - minSize);
        float maxBreite = 1f - x;
        float maxHoehe = 1f - y;
        float breite = applet.random(minSize, maxBreite);
        float hoehe = applet.random(minSize, maxHoehe);
        return new Rechteck(x, y, breite, hoehe);
    }

    @Override
    public void keyPressed() {
        switch (status) {
            case REAGIEREN -> {
                for (Spieler spieler : spielerReaktionsZeitGesamt.keySet()) {
                    if (!Steuerung.Richtung.OBEN.istTasteGedrueckt(applet, spieler.id())) {
                        continue;
                    }

                    if (spielerReaktionsZeit.containsKey(spieler)) {
                        continue;
                    }

                    spielerReaktionsZeit.put(spieler, reaktionsZeit);
                }
            }
            case NICHT_REAGIEREN, NICHTS -> {
                for (Spieler spieler : spielerReaktionsZeitGesamt.keySet()) {
                    if (!Steuerung.Richtung.OBEN.istTasteGedrueckt(applet, spieler.id())) {
                        continue;
                    }

                    spielerReaktionsZeitGesamt.put(spieler, spielerReaktionsZeitGesamt.get(spieler) + STRAFZEIT);
                }
            }
        }
    }

    enum Status {
        NICHTS,
        REAGIEREN,
        NICHT_REAGIEREN
    }
}
