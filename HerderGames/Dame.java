import processing.core.PApplet;

import java.util.*;

final class Dame {
    private Dame() {}

    enum RichtungHorizontal {
        LINKS(-1),
        RECHTS(1);

        final int offset;

        RichtungHorizontal(int offset) {
            this.offset = offset;
        }
    }

    enum RichtungVertikal {
        OBEN(-1),
        UNTEN(1);

        final int offset;

        RichtungVertikal(int offset) {
            this.offset = offset;
        }
    }

    enum Spieler {
        SPIELER_1(Stein.STEIN_SPIELER_1, Stein.DAME_SPIELER_1, RichtungVertikal.UNTEN, 7),
        SPIELER_2(Stein.STEIN_SPIELER_2, Stein.DAME_SPIELER_2, RichtungVertikal.OBEN, 0);

        final Stein stein;
        final Stein dame;
        final RichtungVertikal moveDirection;
        final int dameZeile;

        Spieler(Stein stein, Stein dame, RichtungVertikal moveDirection, int dameZeile) {
            this.stein = stein;
            this.dame = dame;
            this.moveDirection = moveDirection;
            this.dameZeile = dameZeile;
        }

        Spieler getGegner() {
            switch (this) {
                case SPIELER_1:
                    return SPIELER_2;
                case SPIELER_2:
                    return SPIELER_1;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    enum Stein {
        STEIN_SPIELER_1(Spieler.SPIELER_1),
        STEIN_SPIELER_2(Spieler.SPIELER_2),
        DAME_SPIELER_1(Spieler.SPIELER_1),
        DAME_SPIELER_2(Spieler.SPIELER_2);

        final Spieler spieler;

        Stein(Spieler spieler) {
            this.spieler = spieler;
        }

        boolean isDame() {
            return this == DAME_SPIELER_1 || this == DAME_SPIELER_2;
        }

        boolean isStein() {
            return this == STEIN_SPIELER_1 || this == STEIN_SPIELER_2;
        }

        int getColor(PApplet applet) {
            switch (this) {
                case STEIN_SPIELER_1:
                    return applet.color(66, 176, 245);
                case DAME_SPIELER_1:
                    return applet.color(1, 44, 71);
                case STEIN_SPIELER_2:
                    return applet.color(245, 103, 32);
                case DAME_SPIELER_2:
                    return applet.color(196, 14, 35);
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    static final class Brett {
        static final int SIZE = 8;

        static final Brett EMPTY = createEmpty();
        static final Brett ANFANG = createAnfang();

        static Brett createEmpty() {
            List<List<Optional<Stein>>> zeilen = new ArrayList<>();
            for (int zeileIndex = 0; zeileIndex < SIZE; zeileIndex++) {
                List<Optional<Stein>> zeile = new ArrayList<>();
                for (int spalte = 0; spalte < SIZE/2; spalte++) {
                    zeile.add(Optional.empty());
                }
                zeilen.add(zeile);
            }
            return new Brett(zeilen);
        }

        static Brett createAnfang() {
            List<Optional<Stein>> zeileSpieler1 = List.of(
                    Optional.of(Stein.STEIN_SPIELER_1),
                    Optional.of(Stein.STEIN_SPIELER_1),
                    Optional.of(Stein.STEIN_SPIELER_1),
                    Optional.of(Stein.STEIN_SPIELER_1)
            );

            List<Optional<Stein>> zeileLeer = List.of(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()
            );

            List<Optional<Stein>> zeileSpieler2 = List.of(
                    Optional.of(Stein.STEIN_SPIELER_2),
                    Optional.of(Stein.STEIN_SPIELER_2),
                    Optional.of(Stein.STEIN_SPIELER_2),
                    Optional.of(Stein.STEIN_SPIELER_2)
            );

            return new Brett(List.of(
                    zeileSpieler1,
                    zeileSpieler1,
                    zeileSpieler1,
                    zeileLeer,
                    zeileLeer,
                    zeileSpieler2,
                    zeileSpieler2,
                    zeileSpieler2
            ));
        }

        final List<List<Optional<Stein>>> zeilen;

        Brett(List<List<Optional<Stein>>> zeilen) {
            if (zeilen.size() != SIZE) {
                throw new IllegalArgumentException();
            }

            for (List<Optional<Stein>> zeile : zeilen) {
                if (zeile.size() != SIZE/2) {
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
            return zeilen.get(position.zeile).get(position.spalte/2);
        }

        Brett withSteinen(Map<Position, Optional<Stein>> neueFelder) {
            List<List<Optional<Stein>>> neueZeilen = new ArrayList<>();
            for (int zeile = 0; zeile < SIZE; zeile++) {
                List<Optional<Stein>> neueZeile = new ArrayList<>(zeilen.get(zeile));
                neueZeilen.add(neueZeile);
            }

            for (Position position : neueFelder.keySet()) {
                neueZeilen.get(position.zeile).set(position.spalte/2, neueFelder.get(position));
            }

            return new Brett(neueZeilen);
        }

        List<Zug> getPossibleSteinBewgenZuege(Position position) {
            Optional<Stein> stein = getStein(position);
            if (stein.isEmpty()) {
                return Collections.emptyList();
            }
            Spieler spieler = stein.get().spieler;

            List<Zug> result = new ArrayList<>();

            for (RichtungHorizontal richtungHorizontal : RichtungHorizontal.values()) {
                Optional<Position> neuePosition = position.add(spieler.moveDirection.offset, richtungHorizontal.offset);
                if (neuePosition.isEmpty()) {
                    continue;
                }
                if (getStein(neuePosition.get()).isPresent()) {
                    continue;
                }
                Stein neuerStein = neuePosition.get().zeile == spieler.dameZeile ? spieler.dame : spieler.stein;

                Brett neuesBrett = withSteinen(Map.of(
                        position, Optional.empty(),
                        neuePosition.get(), Optional.of(neuerStein)
                ));
                result.add(new Zug(position, neuePosition.get(), List.of(neuesBrett)));
            }

            return result;
        }

        List<Zug> getPossibleSteinSchlagenZuege(Position position, boolean backwards) {
            Optional<Stein> stein = getStein(position);
            if (stein.isEmpty()) {
                return Collections.emptyList();
            }
            Spieler spieler = stein.get().spieler;

            List<Zug> result = new ArrayList<>();

            RichtungVertikal[] richtungenVertikal;
            if (backwards) {
                richtungenVertikal = RichtungVertikal.values();
            } else {
                richtungenVertikal = new RichtungVertikal[]{spieler.moveDirection};
            }

            for (RichtungVertikal richtungVertikal : richtungenVertikal) {
                for (RichtungHorizontal richtungHorizontal : RichtungHorizontal.values()) {
                    Optional<Position> schlagenPosition = position.add(richtungVertikal.offset, richtungHorizontal.offset);
                    if (schlagenPosition.isEmpty()) {
                        continue;
                    }
                    Optional<Stein> schlagenStein = getStein(schlagenPosition.get());
                    if (schlagenStein.isEmpty() || schlagenStein.get().spieler == spieler) {
                        continue;
                    }

                    Optional<Position> neuePosition = position.add(richtungVertikal.offset*2, richtungHorizontal.offset*2);
                    if (neuePosition.isEmpty()) {
                        continue;
                    }
                    if (getStein(neuePosition.get()).isPresent()) {
                        continue;
                    }
                    Stein neuerStein = neuePosition.get().zeile == spieler.dameZeile ? spieler.dame : spieler.stein;

                    Brett neuesBrett = withSteinen(Map.of(
                            position, Optional.empty(),
                            schlagenPosition.get(), Optional.empty(),
                            neuePosition.get(), Optional.of(neuerStein)
                    ));

                    List<Zug> folgendeZuege = neuesBrett.getPossibleSteinSchlagenZuege(neuePosition.get(), true);
                    if (folgendeZuege.isEmpty()) {
                        result.add(new Zug(position, neuePosition.get(), List.of(neuesBrett)));
                    } else {
                        for (Zug folgenderZug : folgendeZuege) {
                            List<Brett> schritte = new ArrayList<>();
                            schritte.add(neuesBrett);
                            schritte.addAll(folgenderZug.schritte);
                            result.add(new Zug(position, folgenderZug.nach, schritte));
                        }
                    }
                }
            }

            return result;
        }

        List<Zug> getPossibleDameBewegenZuege(Position position) {
            return Collections.emptyList(); // TODO
        }

        List<Zug> getPossibleDameSchlagenZuege(Position position) {
            return Collections.emptyList(); // TODO
        }

        List<Zug> getPossibleZuegeForSpieler(Spieler spieler) {
            List<Zug> result = new ArrayList<>();

            for (int zeile = 0; zeile < SIZE; zeile++) {
                for (int spalte = 0; spalte < SIZE; spalte++) {
                    if (!Position.isValid(spalte, zeile)) {
                        continue;
                    }
                    Position position = new Position(zeile, spalte);
                    Optional<Stein> stein = getStein(position);
                    if (stein.isEmpty() || stein.get().spieler != spieler) {
                        continue;
                    }

                    result.addAll(getPossibleSteinSchlagenZuege(position, false));
                    result.addAll(getPossibleDameSchlagenZuege(position));
                }
            }

            if (!result.isEmpty()) {
                return result;
            }

            for (int zeile = 0; zeile < SIZE; zeile++) {
                for (int spalte = 0; spalte < SIZE; spalte++) {
                    if (!Position.isValid(spalte, zeile)) {
                        continue;
                    }
                    Position position = new Position(zeile, spalte);
                    Optional<Stein> stein = getStein(position);
                    if (stein.isEmpty() || stein.get().spieler != spieler) {
                        continue;
                    }

                    result.addAll(getPossibleSteinBewgenZuege(position));
                    result.addAll(getPossibleDameBewegenZuege(position));
                }
            }

            return result;
        }

        List<Zug> getPossibleZuegeForPosition(Position position) {
            Optional<Stein> stein = getStein(position);
            if (stein.isEmpty()) {
                return Collections.emptyList();
            }
            Spieler spieler = stein.get().spieler;

            List<Zug> result = new ArrayList<>();
            for (Zug zug : getPossibleZuegeForSpieler(spieler)) {
                if (!zug.von.equals(position)) {
                    continue;
                }

                result.add(zug);
            }
            return result;
        }

        static int calculateSize(PApplet applet) {
            return Math.min(applet.width, applet.height);
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

        static int calculateAbstandX(PApplet applet) {
            return (applet.width-calculateSize(applet)) / 2;
        }

        static int calculateAbstandY(PApplet applet) {
            return (applet.height-calculateSize(applet)) / 2;
        }

        void draw(PApplet applet) {
            applet.pushStyle();

            int feldSize = calculateFeldSize(applet);

            for (int zeile = 0; zeile < SIZE; zeile++) {
                for (int spalte = 0; spalte < SIZE; spalte++) {
                    int screenX = calculateAbstandX(applet) + feldSize*spalte;
                    int screenY = calculateAbstandY(applet) + feldSize*zeile;

                    if (!Position.isValid(zeile, spalte)) {
                        applet.fill(applet.color(255));
                        applet.rect(screenX, screenY, feldSize, feldSize);
                        continue;
                    }
                    Position position = new Position(zeile, spalte);

                    applet.fill(applet.color(0));
                    applet.rect(screenX, screenY, feldSize, feldSize);

                    int steinSize = calculateSteinSize(applet);
                    int steinAbstand = calculateSteinAbstand(applet);
                    Optional<Stein> stein = getStein(position);
                    if (stein.isEmpty()) {
                        continue;
                    }
                    int steinColor = stein.get().getColor(applet);

                    applet.fill(steinColor);
                    applet.rect(screenX + steinAbstand, screenY + steinAbstand, steinSize, steinSize);
                }
            }

            applet.popStyle();
        }
    }

    static final class Position {
        static boolean isValid(int zeile, int spalte) {
            return zeile >= 0 && zeile < Brett.SIZE && spalte >= 0 && spalte < Brett.SIZE && zeile%2 == spalte%2;
        }

        final int zeile;
        final int spalte;

        Position(int zeile, int spalte) {
            if (!isValid(zeile, spalte)) {
                throw new IllegalArgumentException();
            }

            this.zeile = zeile;
            this.spalte = spalte;
        }

        Optional<Position> add(int zeilen, int spalten) {
            if (!isValid(zeile + zeilen, spalte + spalten)) {
                return Optional.empty();
            }
            return Optional.of(new Position(zeile + zeilen, spalten + spalten));
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

    static final class Zug {
        final Position von;
        final Position nach;
        final List<Brett> schritte;

        Zug(Position von, Position nach, List<Brett> schritte) {
            if (schritte.isEmpty()) {
                throw new IllegalArgumentException();
            }

            this.von = von;
            this.nach = nach;
            this.schritte = List.copyOf(schritte);
        }
    }

    static final class SpielSpielerGegenSpieler extends Spiel {
        private Brett aktuellesBrett = Brett.ANFANG;

        SpielSpielerGegenSpieler(PApplet applet) {
            super(applet);
        }

        @Override
        void draw() {
            applet.background(applet.color(255));
            aktuellesBrett.draw(applet);
        }
    }
}
