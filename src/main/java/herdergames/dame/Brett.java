package herdergames.dame;

import processing.core.PApplet;
import processing.core.PConstants;

import java.util.*;

record Brett(List<List<Optional<Stein>>> zeilen) implements herdergames.ai.Brett<Brett, Zug, Spieler> {
    static final int SIZE = 8;

    static final Brett ANFANG = createAnfang();

    private static Brett createAnfang() {
        List<Optional<Stein>> zeileSpielerOben = List.of(
                Optional.of(Stein.STEIN_SPIELER_OBEN),
                Optional.of(Stein.STEIN_SPIELER_OBEN),
                Optional.of(Stein.STEIN_SPIELER_OBEN),
                Optional.of(Stein.STEIN_SPIELER_OBEN)
        );

        List<Optional<Stein>> zeileLeer = List.of(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        List<Optional<Stein>> zeileSpielerUnten = List.of(
                Optional.of(Stein.STEIN_SPIELER_UNTEN),
                Optional.of(Stein.STEIN_SPIELER_UNTEN),
                Optional.of(Stein.STEIN_SPIELER_UNTEN),
                Optional.of(Stein.STEIN_SPIELER_UNTEN)
        );

        return new Brett(List.of(
                zeileSpielerOben,
                zeileSpielerOben,
                zeileSpielerOben,
                zeileLeer,
                zeileLeer,
                zeileSpielerUnten,
                zeileSpielerUnten,
                zeileSpielerUnten
        ));
    }

    static Optional<Brett> parse(List<String> zeilenText) {
        if (zeilenText.size() != SIZE) {
            return Optional.empty();
        }

        List<List<Optional<Stein>>> zeilen = new ArrayList<>();

        for (int zeileIndex = 0; zeileIndex < SIZE; zeileIndex++) {
            String zeileText = zeilenText.get(zeileIndex);
            if (zeileText.length() != SIZE) {
                return Optional.empty();
            }

            List<Optional<Stein>> zeile = new ArrayList<>();
            zeilen.add(zeile);

            for (int spalteIndex = 0; spalteIndex < SIZE; spalteIndex++) {
                char feldBuchstabe = zeileText.charAt(spalteIndex);
                if (!Position.isValid(zeileIndex, spalteIndex)) {
                    if (feldBuchstabe != ' ') {
                        return Optional.empty();
                    }
                    continue;
                }
                Optional<Optional<Stein>> feld = Stein.buchstabeZuFeld(feldBuchstabe);
                if (feld.isEmpty()) {
                    return Optional.empty();
                }
                zeile.add(feld.get());
            }
        }

        return Optional.of(new Brett(zeilen));
    }

    static int calculateSize(PApplet applet) {
        return Math.min(applet.width, applet.height);
    }

    static int calculateAbstandX(PApplet applet) {
        return (applet.width - calculateSize(applet)) / 2;
    }

    static int calculateAbstandY(PApplet applet) {
        return (applet.height - calculateSize(applet)) / 2;
    }

    static int calculateFeldSize(PApplet applet) {
        return calculateSize(applet) / SIZE;
    }

    static int calculateSteinSize(PApplet applet) {
        return (calculateFeldSize(applet) / 3) * 2;
    }

    static int calculateSteinAbstand(PApplet applet) {
        return (calculateFeldSize(applet) - calculateSteinSize(applet)) / 2;
    }

    Brett(List<List<Optional<Stein>>> zeilen) {
        if (zeilen.size() != SIZE) {
            throw new IllegalArgumentException();
        }

        for (List<Optional<Stein>> zeile : zeilen) {
            if (zeile.size() != SIZE / 2) {
                throw new IllegalArgumentException();
            }
        }

        List<List<Optional<Stein>>> zeilenKopien = new ArrayList<>(SIZE);
        for (List<Optional<Stein>> zeile : zeilen) {
            zeilenKopien.add(List.copyOf(zeile));
        }

        this.zeilen = List.copyOf(zeilenKopien);
    }

    Optional<Stein> getStein(Position position) {
        return zeilen.get(position.zeile()).get(position.spalte() / 2);
    }

    private Brett mitSteinen(Map<Position, Optional<Stein>> neueFelder) {
        List<List<Optional<Stein>>> neueZeilen = new ArrayList<>(SIZE);
        for (int zeile = 0; zeile < SIZE; zeile++) {
            boolean zeileVeraendert = false;
            for (Position position : neueFelder.keySet()) {
                if (position.zeile() == zeile) {
                    zeileVeraendert = true;
                    break;
                }
            }

            if (zeileVeraendert) {
                List<Optional<Stein>> neueZeile = new ArrayList<>(zeilen.get(zeile));
                for (Position position : neueFelder.keySet()) {
                    if (position.zeile() != zeile) {
                        continue;
                    }
                    neueZeile.set(position.spalte() / 2, neueFelder.get(position));
                }
                neueZeilen.add(neueZeile);
            } else {
                neueZeilen.add(zeilen.get(zeile));
            }
        }
        return new Brett(neueZeilen);
    }

    private Set<Zug> getMoeglicheSteinBewegenZuege(Position startPosition) {
        Optional<Stein> stein = getStein(startPosition);
        if (stein.isEmpty() || !stein.get().istStein()) {
            return Collections.emptySet();
        }
        Spieler spieler = stein.get().getSpieler();

        Set<Zug> result = new HashSet<>();

        for (RichtungHorizontal richtungHorizontal : RichtungHorizontal.values()) {
            Optional<Position> neuePosition = startPosition.add(spieler.getBewegungsRichtung().verschiebung, richtungHorizontal.verschiebung);
            if (neuePosition.isEmpty()) {
                continue;
            }
            if (getStein(neuePosition.get()).isPresent()) {
                continue;
            }
            Stein neuerStein = neuePosition.get().zeile() == spieler.getDameZeile() ? spieler.getDame() : spieler.getStein();

            Brett neuesBrett = mitSteinen(Map.of(
                    startPosition, Optional.empty(),
                    neuePosition.get(), Optional.of(neuerStein)
            ));
            result.add(new Zug(startPosition, neuePosition.get(), List.of(neuesBrett)));
        }

        return result;
    }

    private Set<Zug> getMoeglicheSteinSchlagenZuege(Position startPosition, boolean rueckwaerts) {
        Optional<Stein> stein = getStein(startPosition);
        if (stein.isEmpty() || !stein.get().istStein()) {
            return Collections.emptySet();
        }
        Spieler spieler = stein.get().getSpieler();

        Set<Zug> result = new HashSet<>();

        RichtungVertikal[] richtungenVertikal;
        if (rueckwaerts) {
            richtungenVertikal = RichtungVertikal.values();
        } else {
            richtungenVertikal = new RichtungVertikal[]{spieler.getBewegungsRichtung()};
        }

        for (RichtungVertikal richtungVertikal : richtungenVertikal) {
            for (RichtungHorizontal richtungHorizontal : RichtungHorizontal.values()) {
                Optional<Position> schlagenPosition = startPosition.add(richtungVertikal.verschiebung, richtungHorizontal.verschiebung);
                if (schlagenPosition.isEmpty()) {
                    continue;
                }
                Optional<Stein> schlagenStein = getStein(schlagenPosition.get());
                if (schlagenStein.isEmpty() || schlagenStein.get().getSpieler() == spieler) {
                    continue;
                }

                Optional<Position> neuePosition = startPosition.add(richtungVertikal.verschiebung * 2, richtungHorizontal.verschiebung * 2);
                if (neuePosition.isEmpty()) {
                    continue;
                }
                if (getStein(neuePosition.get()).isPresent()) {
                    continue;
                }
                Stein neuerStein = neuePosition.get().zeile() == spieler.getDameZeile() ? spieler.getDame() : spieler.getStein();

                Brett neuesBrett = mitSteinen(Map.of(
                        startPosition, Optional.empty(),
                        schlagenPosition.get(), Optional.empty(),
                        neuePosition.get(), Optional.of(neuerStein)
                ));

                Set<Zug> folgendeZuege = neuesBrett.getMoeglicheSteinSchlagenZuege(neuePosition.get(), true);
                if (folgendeZuege.isEmpty()) {
                    result.add(new Zug(startPosition, neuePosition.get(), List.of(neuesBrett)));
                } else {
                    for (Zug folgenderZug : folgendeZuege) {
                        List<Brett> schritte = new ArrayList<>();
                        schritte.add(neuesBrett);
                        schritte.addAll(folgenderZug.schritte());
                        result.add(new Zug(startPosition, folgenderZug.nach(), schritte));
                    }
                }
            }
        }

        return result;
    }

    private Set<Zug> getMoeglicheDameBewegenZuege(Position startPosition) {
        Optional<Stein> stein = getStein(startPosition);
        if (stein.isEmpty() || !stein.get().istDame()) {
            return Collections.emptySet();
        }
        Spieler spieler = stein.get().getSpieler();

        Set<Zug> result = new HashSet<>();

        for (RichtungVertikal richtungVertikal : RichtungVertikal.values()) {
            richtungHorizontal:
            for (RichtungHorizontal richtungHorizontal : RichtungHorizontal.values()) {
                for (int anzahlFelder = 1; anzahlFelder < SIZE; anzahlFelder++) {
                    Optional<Position> neuePosition = startPosition.add(
                            richtungVertikal.verschiebung * anzahlFelder,
                            richtungHorizontal.verschiebung * anzahlFelder
                    );
                    if (neuePosition.isEmpty()) {
                        continue;
                    }
                    if (getStein(neuePosition.get()).isPresent()) {
                        continue richtungHorizontal;
                    }

                    Brett neuesBrett = mitSteinen(Map.of(
                            startPosition, Optional.empty(),
                            neuePosition.get(), Optional.of(spieler.getDame())
                    ));
                    result.add(new Zug(startPosition, neuePosition.get(), List.of(neuesBrett)));
                }
            }
        }

        return result;
    }

    private Set<Zug> getMoeglicheDameSchlagenZuege(Position startPosition) {
        Optional<Stein> stein = getStein(startPosition);
        if (stein.isEmpty() || !stein.get().istDame()) {
            return Collections.emptySet();
        }
        Spieler spieler = stein.get().getSpieler();

        Set<Zug> result = new HashSet<>();

        for (RichtungVertikal richtungVertikal : RichtungVertikal.values()) {
            richtungHorizontal:
            for (RichtungHorizontal richtungHorizontal : RichtungHorizontal.values()) {
                for (int anzahlFelder = 1; anzahlFelder < SIZE; anzahlFelder++) {
                    Optional<Position> schlagenPosition = startPosition.add(
                            richtungVertikal.verschiebung * anzahlFelder,
                            richtungHorizontal.verschiebung * anzahlFelder
                    );
                    if (schlagenPosition.isEmpty()) {
                        continue;
                    }
                    Optional<Stein> schlagenStein = getStein(schlagenPosition.get());
                    if (schlagenStein.isEmpty()) {
                        continue;
                    }
                    if (schlagenStein.get().getSpieler() == spieler) {
                        continue richtungHorizontal;
                    }

                    Optional<Position> neuePosition = schlagenPosition.get().add(richtungVertikal.verschiebung, richtungHorizontal.verschiebung);
                    if (neuePosition.isEmpty()) {
                        continue;
                    }
                    if (getStein(neuePosition.get()).isPresent()) {
                        continue richtungHorizontal;
                    }

                    Brett neuesBrett = mitSteinen(Map.of(
                            startPosition, Optional.empty(),
                            schlagenPosition.get(), Optional.empty(),
                            neuePosition.get(), Optional.of(spieler.getDame())
                    ));
                    Set<Zug> folgendeZuege = neuesBrett.getMoeglicheDameSchlagenZuege(neuePosition.get());
                    if (folgendeZuege.isEmpty()) {
                        result.add(new Zug(startPosition, neuePosition.get(), List.of(neuesBrett)));
                    } else {
                        for (Zug folgenderZug : folgendeZuege) {
                            List<Brett> schritte = new ArrayList<>();
                            schritte.add(neuesBrett);
                            schritte.addAll(folgenderZug.schritte());
                            result.add(new Zug(startPosition, folgenderZug.nach(), schritte));
                        }
                    }

                    continue richtungHorizontal;
                }
            }
        }

        return result;
    }

    private Set<Zug> getMoeglicheZuegeFuerSpieler(Spieler spieler, boolean gewonnenUeberpruefen) {
        if (gewonnenUeberpruefen && hatVerloren(spieler.getGegner())) {
            return Collections.emptySet();
        }

        Set<Zug> result = new HashSet<>();

        for (int zeile = 0; zeile < SIZE; zeile++) {
            for (int spalte = 0; spalte < SIZE; spalte++) {
                if (!Position.isValid(zeile, spalte)) {
                    continue;
                }
                Position position = new Position(zeile, spalte);
                Optional<Stein> stein = getStein(position);
                if (stein.isEmpty() || stein.get().getSpieler() != spieler) {
                    continue;
                }

                result.addAll(getMoeglicheSteinSchlagenZuege(position, false));
                result.addAll(getMoeglicheDameSchlagenZuege(position));
            }
        }

        if (!result.isEmpty()) {
            // Wenn man schlagen kann, muss man schlagen
            return result;
        }

        for (int zeile = 0; zeile < SIZE; zeile++) {
            for (int spalte = 0; spalte < SIZE; spalte++) {
                if (!Position.isValid(zeile, spalte)) {
                    continue;
                }
                Position position = new Position(zeile, spalte);
                Optional<Stein> stein = getStein(position);
                if (stein.isEmpty() || stein.get().getSpieler() != spieler) {
                    continue;
                }

                result.addAll(getMoeglicheSteinBewegenZuege(position));
                result.addAll(getMoeglicheDameBewegenZuege(position));
            }
        }

        return result;
    }

    @Override
    public Set<Zug> getMoeglicheZuegeFuerSpieler(Spieler spieler) {
        return getMoeglicheZuegeFuerSpieler(spieler, true);
    }

    Set<Zug> getMoeglicheZuegeFuerPosition(Position position) {
        Optional<Stein> stein = getStein(position);
        if (stein.isEmpty()) {
            return Collections.emptySet();
        }
        Spieler spieler = stein.get().getSpieler();

        Set<Zug> result = new HashSet<>();
        for (Zug zug : getMoeglicheZuegeFuerSpieler(spieler)) {
            if (!zug.von().equals(position)) {
                continue;
            }

            result.add(zug);
        }
        return result;
    }

    private int countSteine(Stein gesucht) {
        int result = 0;
        for (List<Optional<Stein>> zeile : zeilen) {
            for (Optional<Stein> stein : zeile) {
                if (stein.isPresent() && stein.get() == gesucht) {
                    result++;
                }
            }
        }
        return result;
    }

    boolean hatVerloren(Spieler spieler) {
        return getMoeglicheZuegeFuerSpieler(spieler, false).isEmpty();
    }

    @Override
    public int getBewertung(Spieler perspektive) {
        final int GEWONNEN_BEWERTUNG = 1000;
        final int VERLOREN_BEWERTUNG = -1000;
        final int WERT_STEIN = 1;
        final int WERT_DAME = WERT_STEIN * 2;

        if (hatVerloren(perspektive)) {
            return VERLOREN_BEWERTUNG;
        }
        if (hatVerloren(perspektive.getGegner())) {
            return GEWONNEN_BEWERTUNG;
        }

        int steinePerspektive = countSteine(perspektive.getStein());
        int damenPerspektive = countSteine(perspektive.getDame());
        int insgesamtPerspektive = steinePerspektive * WERT_STEIN + damenPerspektive * WERT_DAME;

        int steineGegner = countSteine(perspektive.getGegner().getStein());
        int damenGegner = countSteine(perspektive.getGegner().getDame());
        int insgesamtGegner = steineGegner * WERT_STEIN + damenGegner * WERT_DAME;

        return insgesamtPerspektive - insgesamtGegner;
    }

    void draw(PApplet applet, Optional<Position> ausgewaehltePosition) {
        applet.background(applet.color(0));

        Set<Position> possibleMovePositions = new HashSet<>();
        if (ausgewaehltePosition.isPresent()) {
            for (Zug zug : getMoeglicheZuegeFuerPosition(ausgewaehltePosition.get())) {
                possibleMovePositions.add(zug.nach());
            }
        }

        int abstandX = calculateAbstandX(applet);
        int abstandY = calculateAbstandY(applet);
        int feldSize = calculateFeldSize(applet);
        int steinSize = calculateSteinSize(applet);
        int steinAbstand = calculateSteinAbstand(applet);

        for (int zeile = 0; zeile < SIZE; zeile++) {
            for (int spalte = 0; spalte < SIZE; spalte++) {
                int screenX = abstandX + feldSize * spalte;
                int screenY = abstandY + feldSize * zeile;

                if (!Position.isValid(zeile, spalte)) {
                    applet.fill(applet.color(255));
                    applet.rectMode(PConstants.CORNER);
                    applet.rect(screenX, screenY, feldSize, feldSize);
                    continue;
                }
                Position position = new Position(zeile, spalte);

                int backgroundColor;
                if (ausgewaehltePosition.isPresent() && ausgewaehltePosition.get().equals(position)) {
                    backgroundColor = applet.color(161, 2, 118);
                } else if (possibleMovePositions.contains(position)) {
                    backgroundColor = applet.color(245, 59, 194);
                } else {
                    backgroundColor = applet.color(0);
                }
                applet.fill(backgroundColor);
                applet.rectMode(PConstants.CORNER);
                applet.rect(screenX, screenY, feldSize, feldSize);

                Optional<Stein> stein = getStein(position);
                if (stein.isEmpty()) {
                    continue;
                }
                int steinColor = stein.get().getColor(applet);
                applet.fill(steinColor);
                applet.ellipseMode(PConstants.CORNER);
                applet.circle(screenX + steinAbstand, screenY + steinAbstand, steinSize);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (int zeile = 0; zeile < SIZE; zeile++) {
            if (zeile != 0) {
                result.append('\n');
            }

            for (int spalte = 0; spalte < SIZE; spalte++) {
                if (!Position.isValid(zeile, spalte)) {
                    result.append(' ');
                    continue;
                }

                result.append(Stein.feldZuBuchstabe(getStein(new Position(zeile, spalte))));
            }
        }

        return result.toString();
    }
}
