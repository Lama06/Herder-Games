package herdergames.schach;

import processing.core.PApplet;

import java.util.*;

record Brett(List<List<Optional<Stein>>> zeilen) implements herdergames.ai.Brett<Brett, Zug, Spieler> {
    static final int SIZE = 8;

    static final Brett ANFANG = createAnfang();

    private static Brett createAnfang() {
        List<Optional<Stein>> zeile0 = List.of(
                Optional.of(new Stein(Spieler.WEISS, Figur.TURM)),
                Optional.of(new Stein(Spieler.WEISS, Figur.SPRINGER)),
                Optional.of(new Stein(Spieler.WEISS, Figur.LAEUFER)),
                Optional.of(new Stein(Spieler.WEISS, Figur.KOENIG)),
                Optional.of(new Stein(Spieler.WEISS, Figur.DAME)),
                Optional.of(new Stein(Spieler.WEISS, Figur.LAEUFER)),
                Optional.of(new Stein(Spieler.WEISS, Figur.SPRINGER)),
                Optional.of(new Stein(Spieler.WEISS, Figur.TURM))
        );

        List<Optional<Stein>> zeile1 = List.of(
                Optional.of(new Stein(Spieler.WEISS, Figur.BAUER)),
                Optional.of(new Stein(Spieler.WEISS, Figur.BAUER)),
                Optional.of(new Stein(Spieler.WEISS, Figur.BAUER)),
                Optional.of(new Stein(Spieler.WEISS, Figur.BAUER)),
                Optional.of(new Stein(Spieler.WEISS, Figur.BAUER)),
                Optional.of(new Stein(Spieler.WEISS, Figur.BAUER)),
                Optional.of(new Stein(Spieler.WEISS, Figur.BAUER)),
                Optional.of(new Stein(Spieler.WEISS, Figur.BAUER))
        );

        List<Optional<Stein>> leereZeile = List.of(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        List<Optional<Stein>> zeile6 = List.of(
                Optional.of(new Stein(Spieler.SCHWARZ, Figur.BAUER)),
                Optional.of(new Stein(Spieler.SCHWARZ, Figur.BAUER)),
                Optional.of(new Stein(Spieler.SCHWARZ, Figur.BAUER)),
                Optional.of(new Stein(Spieler.SCHWARZ, Figur.BAUER)),
                Optional.of(new Stein(Spieler.SCHWARZ, Figur.BAUER)),
                Optional.of(new Stein(Spieler.SCHWARZ, Figur.BAUER)),
                Optional.of(new Stein(Spieler.SCHWARZ, Figur.BAUER)),
                Optional.of(new Stein(Spieler.SCHWARZ, Figur.BAUER))
        );

        List<Optional<Stein>> zeile7 = List.of(
                Optional.of(new Stein(Spieler.SCHWARZ, Figur.TURM)),
                Optional.of(new Stein(Spieler.SCHWARZ, Figur.SPRINGER)),
                Optional.of(new Stein(Spieler.SCHWARZ, Figur.LAEUFER)),
                Optional.of(new Stein(Spieler.SCHWARZ, Figur.KOENIG)),
                Optional.of(new Stein(Spieler.SCHWARZ, Figur.DAME)),
                Optional.of(new Stein(Spieler.SCHWARZ, Figur.LAEUFER)),
                Optional.of(new Stein(Spieler.SCHWARZ, Figur.SPRINGER)),
                Optional.of(new Stein(Spieler.SCHWARZ, Figur.TURM))
        );

        return new Brett(List.of(
                zeile0,
                zeile1,
                leereZeile,
                leereZeile,
                leereZeile,
                leereZeile,
                zeile6,
                zeile7
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
                char steinBuchstabe = zeileText.charAt(spalteIndex);
                Optional<Optional<Stein>> stein = Stein.buchstabeZuStein(steinBuchstabe);
                if (stein.isEmpty()) {
                    return Optional.empty();
                }
                zeile.add(stein.get());
            }
        }

        return Optional.of(new Brett(zeilen));
    }

    static int getSize(PApplet applet) {
        return Math.min(applet.width, applet.height);
    }

    static int getAbstandHorizontal(PApplet applet) {
        return (applet.width - getSize(applet)) / 2;
    }

    static int getAbstandVertikal(PApplet applet) {
        return (applet.height - getSize(applet)) / 2;
    }

    static int getFeldSize(PApplet applet) {
        return getSize(applet) / SIZE;
    }

    static int getSteinSize(PApplet applet) {
        return (getFeldSize(applet) / 3) * 2;
    }

    static int getSteinAbstand(PApplet applet) {
        return (getFeldSize(applet) - getSteinSize(applet)) / 2;
    }

    Brett(List<List<Optional<Stein>>> zeilen) {
        if (zeilen.size() != SIZE) {
            throw new IllegalArgumentException();
        }

        for (List<Optional<Stein>> zeile : zeilen) {
            if (zeile.size() != SIZE) {
                throw new IllegalArgumentException();
            }
        }

        List<List<Optional<Stein>>> zeilenKopien = new ArrayList<>();
        for (List<Optional<Stein>> zeile : zeilen) {
            zeilenKopien.add(List.copyOf(zeile));
        }
        this.zeilen = List.copyOf(zeilenKopien);
    }

    Optional<Stein> getStein(Position position) {
        return zeilen.get(position.zeile()).get(position.spalte());
    }

    private Brett mitSteinen(Map<Position, Optional<Stein>> neueSteine) {
        List<List<Optional<Stein>>> neueZeilen = new ArrayList<>(SIZE);
        for (int zeile = 0; zeile < SIZE; zeile++) {
            boolean zeileVeraendert = false;
            for (Position position : neueSteine.keySet()) {
                if (position.zeile() == zeile) {
                    zeileVeraendert = true;
                    break;
                }
            }

            if (zeileVeraendert) {
                List<Optional<Stein>> neueZeile = new ArrayList<>(zeilen.get(zeile));
                for (Position position : neueSteine.keySet()) {
                    if (position.zeile() != zeile) {
                        continue;
                    }
                    neueZeile.set(position.spalte(), neueSteine.get(position));
                }
                neueZeilen.add(zeile, neueZeile);
            } else {
                neueZeilen.add(zeile, zeilen.get(zeile));
            }
        }
        return new Brett(neueZeilen);
    }

    private Optional<Zug> getBauerDoppeltBewegenZug(Position startPosition) {
        Optional<Stein> stein = getStein(startPosition);
        if (stein.isEmpty() || stein.get().figur() != Figur.BAUER) {
            return Optional.empty();
        }
        Spieler spieler = stein.get().spieler();
        if (startPosition.zeile() != spieler.getBauernStartZeile()) {
            return Optional.empty();
        }

        Optional<Position> uebersprungenePosition = startPosition.add(
                spieler.getBauerBewegenVerschiebung().verschiebung(),
                0
        );
        if (uebersprungenePosition.isEmpty()) {
            return Optional.empty();
        }
        if (getStein(uebersprungenePosition.get()).isPresent()) {
            return Optional.empty();
        }

        Optional<Position> neuePosition = startPosition.add(
                spieler.getBauerBewegenVerschiebung().verschiebung() * 2,
                0
        );
        if (neuePosition.isEmpty()) {
            return Optional.empty();
        }
        if (getStein(neuePosition.get()).isPresent()) {
            return Optional.empty();
        }

        Brett neuesBrett = mitSteinen(Map.of(
                startPosition, Optional.empty(),
                neuePosition.get(), Optional.of(new Stein(spieler, Figur.BAUER))
        ));
        return Optional.of(new Zug(startPosition, neuePosition.get(), neuesBrett));
    }

    private Optional<Zug> getBauerBewegenZug(Position startPosition) {
        Optional<Stein> stein = getStein(startPosition);
        if (stein.isEmpty() || stein.get().figur() != Figur.BAUER) {
            return Optional.empty();
        }
        Spieler spieler = stein.get().spieler();

        Optional<Position> neuePosition = startPosition.add(
                spieler.getBauerBewegenVerschiebung().verschiebung(),
                0
        );
        if (neuePosition.isEmpty()) {
            return Optional.empty();
        }
        if (getStein(neuePosition.get()).isPresent()) {
            return Optional.empty();
        }

        boolean umwandlung = neuePosition.get().zeile() == spieler.getBauernUmwandlungsZeile();
        Figur neueFigur = umwandlung ? Figur.DAME : Figur.BAUER;

        Brett neuesBrett = mitSteinen(Map.of(
                startPosition, Optional.empty(),
                neuePosition.get(), Optional.of(new Stein(spieler, neueFigur))
        ));
        return Optional.of(new Zug(startPosition, neuePosition.get(), neuesBrett));
    }

    private Set<Zug> getMoeglicheBauerSchlagenZuege(Position startPosition) {
        Optional<Stein> stein = getStein(startPosition);
        if (stein.isEmpty() || stein.get().figur() != Figur.BAUER) {
            return Collections.emptySet();
        }
        Spieler spieler = stein.get().spieler();

        Set<Zug> result = new HashSet<>();

        for (VerschiebungHorizontal verschiebungHorizontal : VerschiebungHorizontal.LINKS_RECHTS) {
            Optional<Position> neuePosition = startPosition.add(
                    spieler.getBauerBewegenVerschiebung().verschiebung(),
                    verschiebungHorizontal.verschiebung()
            );
            if (neuePosition.isEmpty()) {
                continue;
            }
            Optional<Stein> schlagenStein = getStein(neuePosition.get());
            if (schlagenStein.isEmpty() || schlagenStein.get().spieler() == spieler) {
                continue;
            }

            boolean umwandlung = neuePosition.get().zeile() == spieler.getBauernUmwandlungsZeile();
            Figur neueFigur = umwandlung ? Figur.DAME : Figur.BAUER;

            Brett neuesBrett = mitSteinen(Map.of(
                    startPosition, Optional.empty(),
                    neuePosition.get(), Optional.of(new Stein(spieler, neueFigur))
            ));
            result.add(new Zug(startPosition, neuePosition.get(), neuesBrett));
        }

        return result;
    }

    private Set<Zug> getMoeglicheVerschiebungsZuege(
            Position startPosition,
            Figur figur,
            Set<Verschiebung2D> verschiebungen,
            int maxVerschiebungen
    ) {
        Optional<Stein> stein = getStein(startPosition);
        if (stein.isEmpty() || stein.get().figur() != figur) {
            return Collections.emptySet();
        }
        Spieler spieler = stein.get().spieler();

        Set<Zug> result = new HashSet<>();

        verschiebungen:
        for (Verschiebung2D verschiebung : verschiebungen) {
            for (int anzahlVerschiebungen = 1; anzahlVerschiebungen <= maxVerschiebungen; anzahlVerschiebungen++) {
                Optional<Position> neuePosition = startPosition.add(
                        verschiebung.vertikal().verschiebung() * anzahlVerschiebungen,
                        verschiebung.horizontal().verschiebung() * anzahlVerschiebungen
                );
                if (neuePosition.isEmpty()) {
                    continue;
                }
                Optional<Stein> schlagenStein = getStein(neuePosition.get());
                if (schlagenStein.isPresent() && schlagenStein.get().spieler() == spieler) {
                    continue verschiebungen;
                }

                Brett neuesBrett = mitSteinen(Map.of(
                        startPosition, Optional.empty(),
                        neuePosition.get(), Optional.of(new Stein(spieler, figur))
                ));
                result.add(new Zug(startPosition, neuePosition.get(), neuesBrett));

                if (schlagenStein.isPresent()) {
                    continue verschiebungen;
                }
            }
        }

        return result;
    }

    private Set<Zug> getMoeglicheLaeuferZuege(Position startPosition) {
        return getMoeglicheVerschiebungsZuege(
                startPosition,
                Figur.LAEUFER,
                Verschiebung2D.LAEUFER_VERSCHIEBUNGEN,
                SIZE
        );
    }

    private Set<Zug> getMoeglicheSpringerZuege(Position startPosition) {
        return getMoeglicheVerschiebungsZuege(
                startPosition,
                Figur.SPRINGER,
                Verschiebung2D.SPRINGER_VERSCHIEBUNGEN,
                1
        );
    }

    private Set<Zug> getMoeglicheTurmZuege(Position startPosition) {
        return getMoeglicheVerschiebungsZuege(
                startPosition,
                Figur.TURM,
                Verschiebung2D.TURM_VERSCHIEBUNGEN,
                SIZE
        );
    }

    private Set<Zug> getMoeglicheDameZuege(Position startPosition) {
        return getMoeglicheVerschiebungsZuege(
                startPosition,
                Figur.DAME,
                Verschiebung2D.ALLE_VERSCHIEBUNGEN,
                SIZE
        );
    }

    private Set<Zug> getMoeglicheKoenigZuege(Position startPosition) {
        return getMoeglicheVerschiebungsZuege(
                startPosition,
                Figur.KOENIG,
                Verschiebung2D.ALLE_VERSCHIEBUNGEN,
                1
        );
    }

    private boolean imSchach(Spieler spieler) {
        // Hier zu überprüfen, ob der Gegener im Schach ist würde zu einer Endlosrekursion führen
        Set<Zug> gegnerZuege = getMoeglicheZuegeFuerSpieler(spieler.getGegner(), false);

        for (Zug gegnerZug : gegnerZuege) {
            if (gegnerZug.ergebnis().countSteine(new Stein(spieler, Figur.KOENIG)) == 0) {
                return true;
            }
        }

        return false;
    }

    private Set<Zug> getMoeglicheZuegeFuerSpieler(Spieler spieler, boolean checkImSchach) {
        Set<Zug> result = new HashSet<>();

        for (int zeile = 0; zeile < SIZE; zeile++) {
            for (int spalte = 0; spalte < SIZE; spalte++) {
                Position position = new Position(zeile, spalte);

                Optional<Stein> stein = getStein(position);
                if (stein.isEmpty() || stein.get().spieler() != spieler) {
                    continue;
                }

                getBauerBewegenZug(position).ifPresent(result::add);
                getBauerDoppeltBewegenZug(position).ifPresent(result::add);
                result.addAll(getMoeglicheBauerSchlagenZuege(position));
                result.addAll(getMoeglicheSpringerZuege(position));
                result.addAll(getMoeglicheLaeuferZuege(position));
                result.addAll(getMoeglicheTurmZuege(position));
                result.addAll(getMoeglicheDameZuege(position));
                result.addAll(getMoeglicheKoenigZuege(position));
            }
        }

        if (checkImSchach) {
            result.removeIf(zug -> zug.ergebnis().imSchach(spieler));
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
        Spieler spieler = stein.get().spieler();

        Set<Zug> zuege = new HashSet<>(getMoeglicheZuegeFuerSpieler(spieler));
        zuege.removeIf(zug -> !zug.von().equals(position));
        return zuege;
    }

    private int countSteine(Stein gesucht) {
        int result = 0;
        for (List<Optional<Stein>> zeile : zeilen) {
            for (Optional<Stein> stein : zeile) {
                if (stein.isEmpty() || !stein.get().equals(gesucht)) {
                    continue;
                }
                result++;
            }
        }
        return result;
    }

    boolean hatVerloren(Spieler spieler) {
        return getMoeglicheZuegeFuerSpieler(spieler, true).isEmpty();
    }

    @Override
    public int getBewertung(Spieler perspektive) {
        final int VERLOREN_BEWERTUNG = -1000;
        final int GEWONNEN_BEWERTUNG = 1000;

        if (hatVerloren(perspektive)) {
            return VERLOREN_BEWERTUNG;
        }
        if (hatVerloren(perspektive.getGegner())) {
            return GEWONNEN_BEWERTUNG;
        }

        int perspektivePunkte = 0;
        for (Figur figur : Figur.values()) {
            perspektivePunkte += countSteine(new Stein(perspektive, figur)) * figur.wert;
        }

        int gegnerPunkte = 0;
        for (Figur figur : Figur.values()) {
            gegnerPunkte += countSteine(new Stein(perspektive.getGegner(), figur)) * figur.wert;
        }

        return perspektivePunkte - gegnerPunkte;
    }

    void draw(PApplet applet, Optional<Position> ausgewaehltePosition) {
        applet.background(255);

        Set<Position> possibleMovePositions = new HashSet<>();
        if (ausgewaehltePosition.isPresent()) {
            for (Zug zug : getMoeglicheZuegeFuerPosition(ausgewaehltePosition.get())) {
                possibleMovePositions.add(zug.nach());
            }
        }

        int abstandX = getAbstandHorizontal(applet);
        int abstandY = getAbstandVertikal(applet);
        int feldSize = getFeldSize(applet);
        int steinSize = getSteinSize(applet);
        int steinAbstand = getSteinAbstand(applet);

        for (int zeile = 0; zeile < SIZE; zeile++) {
            for (int spalte = 0; spalte < SIZE; spalte++) {
                Position position = new Position(zeile, spalte);

                int screenX = abstandX + feldSize * spalte;
                int screenY = abstandY + feldSize * zeile;

                int backgroundColor;
                if (ausgewaehltePosition.isPresent() && ausgewaehltePosition.get().equals(position)) {
                    backgroundColor = applet.color(161, 2, 118);
                } else if (possibleMovePositions.contains(position)) {
                    backgroundColor = applet.color(245, 59, 194);
                } else if (position.isSchwarz()) {
                    backgroundColor = applet.color(50);
                } else {
                    backgroundColor = applet.color(200);
                }

                applet.fill(backgroundColor);
                applet.rect(screenX, screenY, feldSize, feldSize);

                Optional<Stein> stein = getStein(position);
                if (stein.isEmpty()) {
                    continue;
                }
                stein.get().draw(applet, screenX + steinAbstand, screenY + steinAbstand, steinSize, steinSize);
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
                result.append(Stein.steinZuBuchstabe(getStein(new Position(zeile, spalte))));
            }
        }

        return result.toString();
    }
}
