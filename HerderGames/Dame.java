import processing.core.PApplet;

import java.util.*;

final class Dame {
    private Dame() {}

    private enum RichtungHorizontal {
        LINKS(-1),
        RECHTS(1);

        private final int offset;

        RichtungHorizontal(int offset) {
            this.offset = offset;
        }
    }

    private enum RichtungVertikal {
        OBEN(-1),
        UNTEN(1);

        private final int offset;

        RichtungVertikal(int offset) {
            this.offset = offset;
        }
    }

    private enum Spieler implements AI.Spieler<Spieler> {
        SPIELER_OBEN,
        SPIELER_UNTEN;

        private Stein getStein() {
            switch (this) {
                case SPIELER_OBEN:
                    return Stein.STEIN_SPIELER_1;
                case SPIELER_UNTEN:
                    return Stein.STEIN_SPIELER_2;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private Stein getDame() {
            switch (this) {
                case SPIELER_OBEN:
                    return Stein.DAME_SPIELER_1;
                case SPIELER_UNTEN:
                    return Stein.DAME_SPIELER_2;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private RichtungVertikal getMoveDirection() {
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
        STEIN_SPIELER_1,
        STEIN_SPIELER_2,
        DAME_SPIELER_1,
        DAME_SPIELER_2;

        private boolean isDame() {
            return this == DAME_SPIELER_1 || this == DAME_SPIELER_2;
        }

        boolean isStein() {
            return this == STEIN_SPIELER_1 || this == STEIN_SPIELER_2;
        }

        private Spieler getSpieler() {
            switch (this) {
                case STEIN_SPIELER_1:
                case DAME_SPIELER_1:
                    return Spieler.SPIELER_OBEN;
                case STEIN_SPIELER_2:
                case DAME_SPIELER_2:
                    return Spieler.SPIELER_UNTEN;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private int getColor(PApplet applet) {
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

    private static final class Brett implements AI.Brett<Brett, Zug, Spieler> {
        private static final int SIZE = 8;

        private static final Brett ANFANG = createAnfang();

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

            List<List<Optional<Stein>>> zeilenKopien = new ArrayList<>();
            for (List<Optional<Stein>> zeile : zeilen) {
                zeilenKopien.add(List.copyOf(zeile));
            }

            this.zeilen = List.copyOf(zeilenKopien);
        }

        private Optional<Stein> getStein(Position position) {
            return zeilen.get(position.zeile).get(position.spalte/2);
        }

        private Brett withSteinen(Map<Position, Optional<Stein>> neueFelder) {
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

        private Set<Zug> getPossibleSteinBewgenZuege(Position startPposition) {
            Optional<Stein> stein = getStein(startPposition);
            if (stein.isEmpty() || !stein.get().isStein()) {
                return Collections.emptySet();
            }
            Spieler spieler = stein.get().getSpieler();

            Set<Zug> result = new HashSet<>();

            for (RichtungHorizontal richtungHorizontal : RichtungHorizontal.values()) {
                Optional<Position> neuePosition = startPposition.add(spieler.getMoveDirection().offset, richtungHorizontal.offset);
                if (neuePosition.isEmpty()) {
                    continue;
                }
                if (getStein(neuePosition.get()).isPresent()) {
                    continue;
                }
                Stein neuerStein = neuePosition.get().zeile == spieler.getDameZeile() ? spieler.getDame() : spieler.getStein();

                Brett neuesBrett = withSteinen(Map.of(
                        startPposition, Optional.empty(),
                        neuePosition.get(), Optional.of(neuerStein)
                ));
                result.add(new Zug(startPposition, neuePosition.get(), List.of(neuesBrett)));
            }

            return result;
        }

        private Set<Zug> getPossibleSteinSchlagenZuege(Position startPosition, boolean backwards) {
            Optional<Stein> stein = getStein(startPosition);
            if (stein.isEmpty() || !stein.get().isStein()) {
                return Collections.emptySet();
            }
            Spieler spieler = stein.get().getSpieler();

            Set<Zug> result = new HashSet<>();

            RichtungVertikal[] richtungenVertikal;
            if (backwards) {
                richtungenVertikal = RichtungVertikal.values();
            } else {
                richtungenVertikal = new RichtungVertikal[]{spieler.getMoveDirection()};
            }

            for (RichtungVertikal richtungVertikal : richtungenVertikal) {
                for (RichtungHorizontal richtungHorizontal : RichtungHorizontal.values()) {
                    Optional<Position> schlagenPosition = startPosition.add(richtungVertikal.offset, richtungHorizontal.offset);
                    if (schlagenPosition.isEmpty()) {
                        continue;
                    }
                    Optional<Stein> schlagenStein = getStein(schlagenPosition.get());
                    if (schlagenStein.isEmpty() || schlagenStein.get().getSpieler() == spieler) {
                        continue;
                    }

                    Optional<Position> neuePosition = startPosition.add(richtungVertikal.offset*2, richtungHorizontal.offset*2);
                    if (neuePosition.isEmpty()) {
                        continue;
                    }
                    if (getStein(neuePosition.get()).isPresent()) {
                        continue;
                    }
                    Stein neuerStein = neuePosition.get().zeile == spieler.getDameZeile() ? spieler.getDame() : spieler.getStein();

                    Brett neuesBrett = withSteinen(Map.of(
                            startPosition, Optional.empty(),
                            schlagenPosition.get(), Optional.empty(),
                            neuePosition.get(), Optional.of(neuerStein)
                    ));

                    Set<Zug> folgendeZuege = neuesBrett.getPossibleSteinSchlagenZuege(neuePosition.get(), true);
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

        private Set<Zug> getPossibleDameBewegenZuege(Position startPosition) {
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
                                richtungVertikal.offset*anzahlFelder,
                                richtungHorizontal.offset*anzahlFelder
                        );
                        if (neuePosition.isEmpty()) {
                            continue;
                        }
                        if (getStein(neuePosition.get()).isPresent()) {
                            continue richtungHorizontal;
                        }

                        Brett neuesBrett = withSteinen(Map.of(
                                startPosition, Optional.empty(),
                                neuePosition.get(), Optional.of(spieler.getDame())
                        ));
                        result.add(new Zug(startPosition, neuePosition.get(), List.of(neuesBrett)));
                    }
                }
            }

            return result;
        }

        private Set<Zug> getPossibleDameSchlagenZuege(Position startPosition) {
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
                                richtungVertikal.offset*anzahlFelder,
                                richtungHorizontal.offset*anzahlFelder
                        );
                        if (schlagenPosition.isEmpty()) {
                            continue;
                        }
                        Optional<Stein> schlagenStein = getStein(schlagenPosition.get());
                        if (schlagenStein.isEmpty()) {
                            continue;
                        }
                        if (schlagenStein.get().getSpieler() == spieler) {
                            break richtungHorizontal;
                        }

                        Optional<Position> neuePosition = schlagenPosition.get().add(richtungVertikal.offset, richtungHorizontal.offset);
                        if (neuePosition.isEmpty()) {
                            continue;
                        }
                        if (getStein(neuePosition.get()).isPresent()) {
                            continue richtungHorizontal;
                        }

                        Brett neuesBrett = withSteinen(Map.of(
                                startPosition, Optional.empty(),
                                schlagenPosition.get(), Optional.empty(),
                                neuePosition.get(), Optional.of(spieler.getDame())
                        ));
                        Set<Zug> folgendeZuege = neuesBrett.getPossibleDameSchlagenZuege(neuePosition.get());
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
        public Set<Zug> getPossibleZuegeForSpieler(Spieler spieler) {
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

                    result.addAll(getPossibleSteinSchlagenZuege(position, false));
                    result.addAll(getPossibleDameSchlagenZuege(position));
                }
            }

            if (!result.isEmpty()) {
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

                    result.addAll(getPossibleSteinBewgenZuege(position));
                    result.addAll(getPossibleDameBewegenZuege(position));
                }
            }

            return result;
        }

        private Set<Zug> getPossibleZuegeForPosition(Position position) {
            Optional<Stein> stein = getStein(position);
            if (stein.isEmpty()) {
                return Collections.emptySet();
            }
            Spieler spieler = stein.get().getSpieler();

            Set<Zug> result = new HashSet<>();
            for (Zug zug : getPossibleZuegeForSpieler(spieler)) {
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

        @Override
        public int getBewertung(Spieler perspektive) {
            if (hatVerloren(perspektive)) {
                return -1000;
            }
            if (hatVerloren(perspektive.getGegner())) {
                return 1000;
            }

            int steinePerspektive = countSteine(perspektive.getStein());
            int damenPerspektive = countSteine(perspektive.getDame());
            int insgesamtPerspektive = steinePerspektive + damenPerspektive*2;

            int steineGegner = countSteine(perspektive.getGegner().getStein());
            int damenGegner = countSteine(perspektive.getGegner().getDame());
            int insgesamtGegner = steineGegner + damenGegner*2;

            return insgesamtPerspektive - insgesamtGegner;
        }

        private boolean hatVerloren(Spieler spieler) {
            return getPossibleZuegeForSpieler(spieler).isEmpty();
        }

        private static int calculateSize(PApplet applet) {
            return Math.min(applet.width, applet.height);
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

        private static int calculateAbstandX(PApplet applet) {
            return (applet.width-calculateSize(applet)) / 2;
        }

        private static int calculateAbstandY(PApplet applet) {
            return (applet.height-calculateSize(applet)) / 2;
        }

        private void draw(PApplet applet, Optional<Position> selectedPositon) {
            applet.pushStyle();

            Set<Position> possibleMovePositions = new HashSet<>();
            if (selectedPositon.isPresent()) {
                for (Zug zug : getPossibleZuegeForPosition(selectedPositon.get())) {
                    possibleMovePositions.add(zug.nach);
                }
            }

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

                    int backgroundColor;
                    if (selectedPositon.isPresent() && selectedPositon.get().equals(position)) {
                        backgroundColor = applet.color(161, 2, 118);
                    } else if (possibleMovePositions.contains(position)) {
                        backgroundColor = applet.color(245, 59, 194);
                    } else {
                        backgroundColor = applet.color(0);
                    }
                    applet.fill(backgroundColor);
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
        public Brett getResult() {
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


    static final class SpielerGegenSpielerSpiel extends MiniSpiel {
        private Brett aktuellesBrett = Brett.ANFANG;
        private Optional<Position> selectedPosition = Optional.empty();
        private Spieler amZug;

        SpielerGegenSpielerSpiel(PApplet applet) {
            super(applet);
            amZug = applet.random(1) > 0.5 ? Spieler.SPIELER_UNTEN : Spieler.SPIELER_OBEN;
        }

        private void selectNewField() {
            if (!applet.mousePressed) {
                return;
            }

            Optional<Position> position = Position.fromMousePosition(applet);
            if (position.isEmpty()) {
                selectedPosition = Optional.empty();
                return;
            }
            Optional<Stein> stein = aktuellesBrett.getStein(position.get());
            if (stein.isEmpty() || stein.get().getSpieler() != amZug) {
                selectedPosition = Optional.empty();
                return;
            }

            selectedPosition = position;
        }

        private void zugMachen() {
            if (!applet.mousePressed) {
                return;
            }

            if (selectedPosition.isEmpty()) {
                return;
            }

            Optional<Position> position = Position.fromMousePosition(applet);
            if (position.isEmpty()) {
                return;
            }

            Set<Zug> possibleZuege = aktuellesBrett.getPossibleZuegeForPosition(selectedPosition.get());
            for (Zug possibleZug : possibleZuege) {
                if (possibleZug.nach.equals(position.get())) {
                    aktuellesBrett = possibleZug.getResult();
                    amZug = amZug.getGegner();
                    selectedPosition = Optional.empty();
                    return;
                }
            }
        }

        @Override
        void draw() {
            zugMachen();
            selectNewField();

            applet.background(applet.color(0));
            aktuellesBrett.draw(applet, selectedPosition);
        }
    }

    static final class SpielerGegenAISpiel extends MiniSpiel {
        private static final Spieler COMPUTER = Spieler.SPIELER_OBEN;
        private static final Spieler MENSCH = Spieler.SPIELER_UNTEN;
        private Brett aktuellesBrett = Brett.ANFANG;
        private Optional<Position> selectedPosition = Optional.empty();

        private void selectNewField() {
            if (!applet.mousePressed) {
                return;
            }

            Optional<Position> position = Position.fromMousePosition(applet);
            if (position.isEmpty()) {
                selectedPosition = Optional.empty();
                return;
            }
            Optional<Stein> stein = aktuellesBrett.getStein(position.get());
            if (stein.isEmpty() || stein.get().getSpieler() != MENSCH) {
                selectedPosition = Optional.empty();
                return;
            }

            selectedPosition = position;
        }

        private void zugMachen() {
            if (!applet.mousePressed) {
                return;
            }

            if (selectedPosition.isEmpty()) {
                return;
            }

            Optional<Position> position = Position.fromMousePosition(applet);
            if (position.isEmpty()) {
                return;
            }

            Set<Zug> possibleZuege = aktuellesBrett.getPossibleZuegeForPosition(selectedPosition.get());
            for (Zug possibleZug : possibleZuege) {
                if (possibleZug.nach.equals(position.get())) {
                    Zug antowrt = AI.calculateBestZug(possibleZug.getResult(), COMPUTER, 6);

                    aktuellesBrett = antowrt.getResult();
                    selectedPosition = Optional.empty();
                    return;
                }
            }
        }

        SpielerGegenAISpiel(PApplet applet) {
            super(applet);
        }

        @Override
        void draw() {
            zugMachen();
            selectNewField();

            applet.background(applet.color(0));
            aktuellesBrett.draw(applet, selectedPosition);
        }
    }
}
