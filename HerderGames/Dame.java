import processing.core.PApplet;
import processing.core.PConstants;

import java.util.*;

final class Dame {
    static final Spiel.SpielerGegenSpieler.Factory SPIELER_GEGEN_SPIELER_FACTORY = new Spiel.SpielerGegenSpieler.Factory() {
        @Override
        Spiel.SpielerGegenSpieler neuesSpiel(PApplet applet, Spiel.Spieler spieler1, Spiel.Spieler spieler2) {
            return new SpielerGegenSpielerSpiel(applet, spieler1, spieler2);
        }
    };

    static final Spiel.Einzelspieler.Factory SPIELER_GEGEN_AI_FACTORY = new Spiel.Einzelspieler.Factory() {
        @Override
        Spiel.Einzelspieler neuesSpiel(PApplet applet, Spiel.Spieler spieler) {
            return new SpielerGegenAISpiel(applet, spieler);
        }
    };

    private Dame() {}

    private enum RichtungHorizontal {
        LINKS(-1),
        RECHTS(1);

        private final int verschiebung;

        RichtungHorizontal(int verschiebung) {
            this.verschiebung = verschiebung;
        }
    }

    private enum RichtungVertikal {
        OBEN(-1),
        UNTEN(1);

        private final int verschiebung;

        RichtungVertikal(int verschiebung) {
            this.verschiebung = verschiebung;
        }
    }

    private enum Spieler implements AI.Spieler<Spieler> {
        SPIELER_OBEN,
        SPIELER_UNTEN;

        private Stein getStein() {
            switch (this) {
                case SPIELER_OBEN:
                    return Stein.STEIN_SPIELER_OBEN;
                case SPIELER_UNTEN:
                    return Stein.STEIN_SPIELER_UNTEN;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private Stein getDame() {
            switch (this) {
                case SPIELER_OBEN:
                    return Stein.DAME_SPIELER_OBEN;
                case SPIELER_UNTEN:
                    return Stein.DAME_SPIELER_UNTEN;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private RichtungVertikal getBewegungsRichtung() {
            switch (this) {
                case SPIELER_OBEN:
                    return RichtungVertikal.UNTEN;
                case SPIELER_UNTEN:
                    return RichtungVertikal.OBEN;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private int getDameZeile() {
            switch (this) {
                case SPIELER_OBEN:
                    return 7;
                case SPIELER_UNTEN:
                    return 0;
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public Spieler getGegner() {
            switch (this) {
                case SPIELER_OBEN:
                    return SPIELER_UNTEN;
                case SPIELER_UNTEN:
                    return SPIELER_OBEN;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    private enum Stein {
        STEIN_SPIELER_OBEN,
        STEIN_SPIELER_UNTEN,
        DAME_SPIELER_OBEN,
        DAME_SPIELER_UNTEN;

        private boolean isDame() {
            return this == DAME_SPIELER_OBEN || this == DAME_SPIELER_UNTEN;
        }

        private boolean isStein() {
            return this == STEIN_SPIELER_OBEN || this == STEIN_SPIELER_UNTEN;
        }

        private Spieler getSpieler() {
            switch (this) {
                case STEIN_SPIELER_OBEN:
                case DAME_SPIELER_OBEN:
                    return Spieler.SPIELER_OBEN;
                case STEIN_SPIELER_UNTEN:
                case DAME_SPIELER_UNTEN:
                    return Spieler.SPIELER_UNTEN;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private int getColor(PApplet applet) {
            switch (this) {
                case STEIN_SPIELER_OBEN:
                    return applet.color(66, 176, 245);
                case DAME_SPIELER_OBEN:
                    return applet.color(1, 44, 71);
                case STEIN_SPIELER_UNTEN:
                    return applet.color(245, 103, 32);
                case DAME_SPIELER_UNTEN:
                    return applet.color(196, 14, 35);
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    private static final class Brett implements AI.Brett<Brett, Zug, Spieler> {
        private static final int SIZE = 8;

        private static final Brett ANFANG = createAnfang();
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

        private final List<List<Optional<Stein>>> zeilen;

        private Brett(List<List<Optional<Stein>>> zeilen) {
            if (zeilen.size() != SIZE) {
                throw new IllegalArgumentException();
            }

            for (List<Optional<Stein>> zeile : zeilen) {
                if (zeile.size() != SIZE/2) {
                    throw new IllegalArgumentException();
                }
            }

            List<List<Optional<Stein>>> zeilenKopien = new ArrayList<>(SIZE);
            for (List<Optional<Stein>> zeile : zeilen) {
                zeilenKopien.add(List.copyOf(zeile));
            }

            this.zeilen = List.copyOf(zeilenKopien);
        }

        private Optional<Stein> getStein(Position position) {
            return zeilen.get(position.zeile).get(position.spalte/2);
        }

        private Brett mitSteinen(Map<Position, Optional<Stein>> neueFelder) {
            List<List<Optional<Stein>>> neueZeilen = new ArrayList<>(SIZE);
            for (int zeile = 0; zeile < SIZE; zeile++) {
                boolean zeileVeraendert = false;
                for (Position position : neueFelder.keySet()) {
                    if (position.zeile == zeile) {
                        zeileVeraendert = true;
                        break;
                    }
                }

                if (zeileVeraendert) {
                    List<Optional<Stein>> neueZeile = new ArrayList<>(zeilen.get(zeile));
                    for (Position position : neueFelder.keySet()) {
                        if (position.zeile != zeile) {
                            continue;
                        }
                        neueZeile.set(position.spalte/2, neueFelder.get(position));
                    }
                    neueZeilen.add(zeile, neueZeile);
                } else {
                    neueZeilen.add(zeile, zeilen.get(zeile));
                }
            }
            return new Brett(neueZeilen);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Brett brett = (Brett) o;
            return Objects.equals(zeilen, brett.zeilen);
        }

        @Override
        public int hashCode() {
            return Objects.hash(zeilen);
        }

        private Set<Zug> getMoeglicheSteinBewgenZuege(Position startPosition) {
            Optional<Stein> stein = getStein(startPosition);
            if (stein.isEmpty() || !stein.get().isStein()) {
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
                Stein neuerStein = neuePosition.get().zeile == spieler.getDameZeile() ? spieler.getDame() : spieler.getStein();

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
            if (stein.isEmpty() || !stein.get().isStein()) {
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

                    Optional<Position> neuePosition = startPosition.add(richtungVertikal.verschiebung*2, richtungHorizontal.verschiebung*2);
                    if (neuePosition.isEmpty()) {
                        continue;
                    }
                    if (getStein(neuePosition.get()).isPresent()) {
                        continue;
                    }
                    Stein neuerStein = neuePosition.get().zeile == spieler.getDameZeile() ? spieler.getDame() : spieler.getStein();

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
                            schritte.addAll(folgenderZug.schritte);
                            result.add(new Zug(startPosition, folgenderZug.nach, schritte));
                        }
                    }
                }
            }

            return result;
        }

        private Set<Zug> getMoeglicheDameBewegenZuege(Position startPosition) {
            Optional<Stein> stein = getStein(startPosition);
            if (stein.isEmpty() || !stein.get().isDame()) {
                return Collections.emptySet();
            }
            Spieler spieler = stein.get().getSpieler();

            Set<Zug> result = new HashSet<>();

            for (RichtungVertikal richtungVertikal : RichtungVertikal.values()) {
                richtungHorizontal:
                for (RichtungHorizontal richtungHorizontal : RichtungHorizontal.values()) {
                    for (int anzahlFelder = 1; anzahlFelder < SIZE; anzahlFelder++) {
                        Optional<Position> neuePosition = startPosition.add(
                                richtungVertikal.verschiebung*anzahlFelder,
                                richtungHorizontal.verschiebung*anzahlFelder
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
            if (stein.isEmpty() || !stein.get().isDame()) {
                return Collections.emptySet();
            }
            Spieler spieler = stein.get().getSpieler();

            Set<Zug> result = new HashSet<>();

            for (RichtungVertikal richtungVertikal : RichtungVertikal.values()) {
                richtungHorizontal:
                for (RichtungHorizontal richtungHorizontal : RichtungHorizontal.values()) {
                    for (int anzahlFelder = 1; anzahlFelder < SIZE; anzahlFelder++) {
                        Optional<Position> schlagenPosition = startPosition.add(
                                richtungVertikal.verschiebung*anzahlFelder,
                                richtungHorizontal.verschiebung*anzahlFelder
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
                                schritte.addAll(folgenderZug.schritte);
                                result.add(new Zug(startPosition, folgenderZug.nach, schritte));
                            }
                        }
                    }
                }
            }

            return result;
        }

        @Override
        public Set<Zug> getMoeglicheZuegeFuerSpieler(Spieler spieler) {
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

                    result.addAll(getMoeglicheSteinBewgenZuege(position));
                    result.addAll(getMoeglicheDameBewegenZuege(position));
                }
            }

            return result;
        }

        private Set<Zug> getMoeglicheZuegeFuerPosition(Position position) {
            Optional<Stein> stein = getStein(position);
            if (stein.isEmpty()) {
                return Collections.emptySet();
            }
            Spieler spieler = stein.get().getSpieler();

            Set<Zug> result = new HashSet<>();
            for (Zug zug : getMoeglicheZuegeFuerSpieler(spieler)) {
                if (!zug.von.equals(position)) {
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

        private boolean hatVerloren(Spieler spieler) {
            return getMoeglicheZuegeFuerSpieler(spieler).isEmpty();
        }

        @Override
        public int getBewertung(Spieler perspektive) {
            final int GEWONNEN_BEWERTUNG = 1000;
            final int VERLOREN_BEWERTUNG = -1000;
            final int WERT_STEIN = 1;
            final int WERT_DAME = WERT_STEIN*2;

            if (hatVerloren(perspektive)) {
                return VERLOREN_BEWERTUNG;
            }
            if (hatVerloren(perspektive.getGegner())) {
                return GEWONNEN_BEWERTUNG;
            }

            int steinePerspektive = countSteine(perspektive.getStein());
            int damenPerspektive = countSteine(perspektive.getDame());
            int insgesamtPerspektive = steinePerspektive*WERT_STEIN + damenPerspektive*WERT_DAME;

            int steineGegner = countSteine(perspektive.getGegner().getStein());
            int damenGegner = countSteine(perspektive.getGegner().getDame());
            int insgesamtGegner = steineGegner*WERT_STEIN + damenGegner*WERT_DAME;

            return insgesamtPerspektive - insgesamtGegner;
        }

        private static int calculateSize(PApplet applet) {
            return Math.min(applet.width, applet.height);
        }

        private static int calculateAbstandX(PApplet applet) {
            return (applet.width-calculateSize(applet)) / 2;
        }

        private static int calculateAbstandY(PApplet applet) {
            return (applet.height-calculateSize(applet)) / 2;
        }

        private static int calculateFeldSize(PApplet applet) {
            return calculateSize(applet) / SIZE;
        }

        private static int calculateSteinSize(PApplet applet) {
            return (calculateFeldSize(applet) / 3) * 2;
        }

        private static int calculateSteinAbstand(PApplet applet) {
            return (calculateFeldSize(applet) - calculateSteinSize(applet)) / 2;
        }

        private void draw(PApplet applet, Optional<Position> ausgewaehltePosition) {
            Set<Position> possibleMovePositions = new HashSet<>();
            if (ausgewaehltePosition.isPresent()) {
                for (Zug zug : getMoeglicheZuegeFuerPosition(ausgewaehltePosition.get())) {
                    possibleMovePositions.add(zug.nach);
                }
            }

            int abstandX = calculateAbstandX(applet);
            int abstandY = calculateAbstandY(applet);
            int feldSize = calculateFeldSize(applet);
            int steinSize = calculateSteinSize(applet);
            int steinAbstand = calculateSteinAbstand(applet);

            for (int zeile = 0; zeile < SIZE; zeile++) {
                for (int spalte = 0; spalte < SIZE; spalte++) {
                    int screenX = abstandX + feldSize*spalte;
                    int screenY = abstandY + feldSize*zeile;

                    if (!Position.isValid(zeile, spalte)) {
                        applet.fill(applet.color(255));
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
    }

    private static final class Position {
        private static boolean isValid(int zeile, int spalte) {
            return zeile >= 0 && zeile < Brett.SIZE && spalte >= 0 && spalte < Brett.SIZE && zeile%2 == spalte%2;
        }

        private static Optional<Position> fromMousePosition(PApplet applet) {
            int mouseXOffset = applet.mouseX - Brett.calculateAbstandX(applet);
            int mouseYOffset = applet.mouseY - Brett.calculateAbstandY(applet);
            int feldSize = Brett.calculateFeldSize(applet);
            int zeile = mouseYOffset / feldSize;
            int spalte = mouseXOffset / feldSize;
            if (!isValid(zeile, spalte)) {
                return Optional.empty();
            }
            return Optional.of(new Position(zeile, spalte));
        }

        private final int zeile;
        private final int spalte;

        private Position(int zeile, int spalte) {
            if (!isValid(zeile, spalte)) {
                throw new IllegalArgumentException();
            }

            this.zeile = zeile;
            this.spalte = spalte;
        }

        private Optional<Position> add(int zeilen, int spalten) {
            if (!isValid(zeile + zeilen, spalte + spalten)) {
                return Optional.empty();
            }
            return Optional.of(new Position(zeile + zeilen, spalte + spalten));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return zeile == position.zeile && spalte == position.spalte;
        }

        @Override
        public int hashCode() {
            return Objects.hash(zeile, spalte);
        }
    }

    private static final class Zug implements AI.Zug<Brett> {
        private final Position von;
        private final Position nach;
        private final List<Brett> schritte;

        private Zug(Position von, Position nach, List<Brett> schritte) {
            if (von == null || nach == null || schritte == null || schritte.isEmpty()) {
                throw new IllegalArgumentException();
            }

            this.von = von;
            this.nach = nach;
            this.schritte = List.copyOf(schritte);
        }

        @Override
        public Brett getErgebnis() {
            return schritte.get(schritte.size()-1);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Zug zug = (Zug) o;
            return Objects.equals(von, zug.von) && Objects.equals(nach, zug.nach) && Objects.equals(schritte, zug.schritte);
        }

        @Override
        public int hashCode() {
            return Objects.hash(von, nach, schritte);
        }
    }

    private static final class SpielerGegenSpielerSpiel extends Spiel.SpielerGegenSpieler {
        private final Spieler spielerObenSpieler;
        private final Spieler spielerUntenSpieler;
        private Brett aktuellesBrett = Brett.ANFANG;
        private Optional<Position> ausgewaehltePosition = Optional.empty();
        private Dame.Spieler amZug = Dame.Spieler.SPIELER_UNTEN;

        private SpielerGegenSpielerSpiel(PApplet applet, Spieler spieler1, Spieler spieler2) {
            super(applet);
            if (spieler1.punkte < spieler2.punkte) {
                spielerUntenSpieler = spieler1;
                spielerObenSpieler = spieler2;
            } else {
                spielerUntenSpieler = spieler2;
                spielerObenSpieler = spieler1;
            }
        }

        private void selectNewField() {
            Optional<Position> position = Position.fromMousePosition(applet);
            if (position.isEmpty()) {
                ausgewaehltePosition = Optional.empty();
                return;
            }
            Optional<Stein> stein = aktuellesBrett.getStein(position.get());
            if (stein.isEmpty() || stein.get().getSpieler() != amZug) {
                ausgewaehltePosition = Optional.empty();
                return;
            }

            ausgewaehltePosition = position;
        }

        private void zugMachen() {
            if (ausgewaehltePosition.isEmpty()) {
                return;
            }

            Optional<Position> position = Position.fromMousePosition(applet);
            if (position.isEmpty()) {
                return;
            }

            Set<Zug> moeglicheZuege = aktuellesBrett.getMoeglicheZuegeFuerPosition(ausgewaehltePosition.get());
            for (Zug moeglicherZug : moeglicheZuege) {
                if (moeglicherZug.nach.equals(position.get())) {
                    aktuellesBrett = moeglicherZug.getErgebnis();
                    amZug = amZug.getGegner();
                    ausgewaehltePosition = Optional.empty();
                    return;
                }
            }
        }

        @Override
        void mousePressed() {
            zugMachen();
            selectNewField();
        }

        @Override
        Optional<Optional<Spieler.Id>> draw() {
            applet.background(applet.color(0));
            aktuellesBrett.draw(applet, ausgewaehltePosition);

            if (aktuellesBrett.hatVerloren(Dame.Spieler.SPIELER_OBEN)) {
                return Optional.of(Optional.of(spielerUntenSpieler.id));
            }
            if (aktuellesBrett.hatVerloren(Dame.Spieler.SPIELER_UNTEN)) {
                return Optional.of(Optional.of(spielerObenSpieler.id));
            }
            return Optional.empty();
        }
    }

    private static final class SpielerGegenAISpiel extends Spiel.Einzelspieler {
        private static final Dame.Spieler COMPUTER = Dame.Spieler.SPIELER_OBEN;
        private static final Dame.Spieler MENSCH = Dame.Spieler.SPIELER_UNTEN;

        private Brett aktuellesBrett = Brett.ANFANG;
        private Optional<Position> ausgewaehltePosition = Optional.empty();

        private SpielerGegenAISpiel(PApplet applet, Spieler spieler) {
            super(applet);
        }

        private void selectNewField() {
            Optional<Position> position = Position.fromMousePosition(applet);
            if (position.isEmpty()) {
                ausgewaehltePosition = Optional.empty();
                return;
            }
            Optional<Stein> stein = aktuellesBrett.getStein(position.get());
            if (stein.isEmpty() || stein.get().getSpieler() != MENSCH) {
                ausgewaehltePosition = Optional.empty();
                return;
            }

            ausgewaehltePosition = position;
        }

        private void zugMachen() {
            if (ausgewaehltePosition.isEmpty()) {
                return;
            }

            Optional<Position> position = Position.fromMousePosition(applet);
            if (position.isEmpty()) {
                return;
            }

            Set<Zug> moeglicheZuege = aktuellesBrett.getMoeglicheZuegeFuerPosition(ausgewaehltePosition.get());
            for (Zug moeglicherZug : moeglicheZuege) {
                if (moeglicherZug.nach.equals(position.get())) {
                    Optional<Zug> antwort = AI.bestenNaechstenZugBerechnen(moeglicherZug.getErgebnis(), COMPUTER, 6);
                    if (antwort.isEmpty()) {
                        aktuellesBrett = moeglicherZug.getErgebnis();
                        ausgewaehltePosition = Optional.empty();
                        return;
                    }

                    aktuellesBrett = antwort.get().getErgebnis();
                    ausgewaehltePosition = Optional.empty();
                    return;
                }
            }
        }

        @Override
        void mousePressed() {
            zugMachen();
            selectNewField();
        }

        @Override
        Optional<Ergebnis> draw() {
            applet.background(applet.color(0));
            aktuellesBrett.draw(applet, ausgewaehltePosition);

            if (aktuellesBrett.hatVerloren(MENSCH)) {
                return Optional.of(Ergebnis.VERLOREN);
            }
            if (aktuellesBrett.hatVerloren(COMPUTER)) {
                return Optional.of(Ergebnis.GEWONNEN);
            }
            return Optional.empty();
        }
    }
}
