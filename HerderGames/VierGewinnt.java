import processing.core.PApplet;

import java.util.*;

final class VierGewinnt {
    private VierGewinnt() {}

    private enum VerschiebungHorizontal {
        LINKS(-1),
        KEINE(0),
        RECHTS(1);

        private final int verschiebung;

        VerschiebungHorizontal(int verschiebung) {
            this.verschiebung = verschiebung;
        }
    }

    private enum VerschiebungVertikal {
        OBEN(-1),
        KEINE(0),
        UNTEN(1);

        private final int verschiebung;

        VerschiebungVertikal(int verschiebung) {
            this.verschiebung = verschiebung;
        }
    }

    private enum Spieler implements AI.Spieler<Spieler> {
        SPIELER_1,
        SPIELER_2;

        @Override
        public Spieler getGegner() {
            switch (this) {
                case SPIELER_1:
                    return SPIELER_2;
                case SPIELER_2:
                    return SPIELER_1;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private int getColor(PApplet applet) {
            switch (this) {
                case SPIELER_1:
                    return applet.color(66, 135, 245);
                case SPIELER_2:
                    return applet.color(250, 110, 22);
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    private static final class Brett implements AI.Brett<Brett, Zug, Spieler> {
        private static final int WIDTH = 7;
        private static final int HEIGHT = 6;

        private static final Brett LEER = createLeer();
        private static Brett createLeer() {
            List<Optional<Spieler>> leereZeile = new ArrayList<>();
            for (int spalte = 0; spalte < WIDTH; spalte++) {
                leereZeile.add(Optional.empty());
            }

            List<List<Optional<Spieler>>> zeilen = new ArrayList<>(HEIGHT);
            for (int zeile = 0; zeile < HEIGHT; zeile++) {
                zeilen.add(leereZeile);
            }

            return new Brett(zeilen);
        }

        private final List<List<Optional<Spieler>>> zeilen;

        private Brett(List<List<Optional<Spieler>>> zeilen) {
            if (zeilen.size() != HEIGHT) {
                new Exception().printStackTrace();
                throw new IllegalArgumentException();
            }

            for (List<Optional<Spieler>> zeile : zeilen) {
                if (zeile.size() != WIDTH) {
                    throw new IllegalArgumentException();
                }
            }

            List<List<Optional<Spieler>>> zeilenKopien = new ArrayList<>(HEIGHT);
            for (List<Optional<Spieler>> zeile : zeilen) {
                zeilenKopien.add(List.copyOf(zeile));
            }

            this.zeilen = zeilenKopien;
        }

        private Optional<Spieler> getStein(Position position) {
            return zeilen.get(position.zeile).get(position.spalte);
        }

        private Brett mitStein(Position position, Optional<Spieler> stein) {
            List<List<Optional<Spieler>>> neueZeilen = new ArrayList<>(HEIGHT);
            for (int zeile = 0; zeile < HEIGHT; zeile++) {
                if (position.zeile == zeile) {
                    List<Optional<Spieler>> neueZeile = new ArrayList<>(zeilen.get(zeile));
                    neueZeile.set(position.spalte, stein);
                    neueZeilen.add(neueZeile);
                } else {
                    neueZeilen.add(zeilen.get(zeile));
                }
            }
            return new Brett(neueZeilen);
        }

        private OptionalInt getReiheFortschritt(
                Spieler spieler,
                Position startPosition,
                VerschiebungHorizontal verschiebungHorizontal,
                VerschiebungVertikal verschiebungVertikal
        ) {
            int result = 0;

            for (int i = 0; i < 4; i++) {
                Optional<Position> position = startPosition.add(
                        verschiebungVertikal.verschiebung*i,
                        verschiebungHorizontal.verschiebung*i
                );
                if (position.isEmpty()) {
                    return OptionalInt.empty();
                }
                Optional<Spieler> stein = getStein(position.get());
                if (stein.isEmpty()) {
                    continue;
                }
                if (stein.get() != spieler) {
                    return OptionalInt.empty();
                }
                result++;
            }

            return OptionalInt.of(result);
        }

        private int countReihen(
                Spieler spieler,
                int fortschritt,
                VerschiebungHorizontal verschiebungHorizontal,
                VerschiebungVertikal verschiebungVertikal
        ) {
            int result = 0;
            for (int zeile = 0; zeile < HEIGHT; zeile++) {
                for (int spalte = 0; spalte < WIDTH; spalte++) {
                    OptionalInt reiheFortschritt = getReiheFortschritt(
                            spieler,
                            new Position(zeile, spalte),
                            verschiebungHorizontal,
                            verschiebungVertikal
                    );
                    if (reiheFortschritt.isPresent() && reiheFortschritt.getAsInt() == fortschritt) {
                        result++;
                    }
                }
            }
            return result;
        }

        private int countReihen(Spieler spieler, int fortschritt) {
            int inZeilen = countReihen(spieler, fortschritt, VerschiebungHorizontal.RECHTS, VerschiebungVertikal.KEINE);
            int inSpalten = countReihen(spieler, fortschritt, VerschiebungHorizontal.KEINE, VerschiebungVertikal.UNTEN);
            int diagonalRechts = countReihen(spieler, fortschritt, VerschiebungHorizontal.RECHTS, VerschiebungVertikal.UNTEN);
            int diagonalLinks = countReihen(spieler, fortschritt, VerschiebungHorizontal.LINKS, VerschiebungVertikal.UNTEN);
            return inZeilen + inSpalten + diagonalRechts + diagonalLinks;
        }

        private boolean hatGewonnen(Spieler spieler) {
            return countReihen(spieler, 4) >= 1;
        }

        private OptionalInt getUntersteFreiheZeile(int spalte) {
            if (!Position.isValidSpalte(spalte)) {
                throw new IllegalArgumentException();
            }

            for (int zeile = HEIGHT-1; zeile >= 0; zeile--) {
                if (getStein(new Position(zeile, spalte)).isEmpty()) {
                    return OptionalInt.of(zeile);
                }
            }

            return OptionalInt.empty();
        }

        @Override
        public Set<Zug> getMoeglicheZuegeFuerSpieler(Spieler spieler) {
            if (hatGewonnen(Spieler.SPIELER_1) || hatGewonnen(Spieler.SPIELER_2)) {
                return Collections.emptySet();
            }

            Set<Zug> result = new HashSet<>();

            for (int spalte = 0; spalte < WIDTH; spalte++) {
                OptionalInt zeile = getUntersteFreiheZeile(spalte);
                if (zeile.isEmpty()) {
                    continue;
                }
                Position position = new Position(zeile.getAsInt(), spalte);
                Brett neuesBrett = mitStein(position, Optional.of(spieler));
                result.add(new Zug(spalte, neuesBrett));
            }

            return result;
        }

        @Override
        public int getBewertung(Spieler perspektive) {
            final int GEWONNEN_BEWERTUNG = 10000;
            final int VERLOREN_BEWERTUNG = -10000;

            final int REIHE3_BEWERTUNG = 10;

            if (hatGewonnen(perspektive)) {
                return GEWONNEN_BEWERTUNG;
            }
            if (hatGewonnen(perspektive.getGegner())) {
                return VERLOREN_BEWERTUNG;
            }

            int perspektiveReihen3 = countReihen(perspektive, 3);
            int perspektiveBewertung = perspektiveReihen3*REIHE3_BEWERTUNG;

            int gegnerReihen3 = countReihen(perspektive.getGegner(), 3);
            int gegnerBewertung = gegnerReihen3*REIHE3_BEWERTUNG;

            return perspektiveBewertung - gegnerBewertung;
        }

        private static int getFeldSize(PApplet applet) {
            return Math.min(applet.width / WIDTH, applet.height / HEIGHT);
        }

        private static int getSteinSize(PApplet applet) {
            return (getFeldSize(applet) / 3) * 2;
        }

        private static int getSteinAbstand(PApplet applet) {
            return (getFeldSize(applet) - getSteinSize(applet)) / 2;
        }

        private static int getBreite(PApplet applet) {
            return getFeldSize(applet) * WIDTH;
        }

        private static int getHoehe(PApplet applet) {
            return getFeldSize(applet) * HEIGHT;
        }

        private static int getAbstandHorizontal(PApplet applet) {
            return (applet.width - getBreite(applet)) / 2;
        }

        private static int getAbstandVertikal(PApplet applet) {
            return (applet.height - getHoehe(applet)) / 2;
        }

        private void drawStein(PApplet applet, Position position) {
            Optional<Spieler> stein = getStein(position);

            int abstandHorizontal = getAbstandHorizontal(applet);
            int abstandVertikal = getAbstandVertikal(applet);
            int feldSize = getFeldSize(applet);
            int steinAbstand = getSteinAbstand(applet);
            int steinSize = getSteinSize(applet);

            int steinX = abstandHorizontal + feldSize*position.spalte + steinAbstand;
            int steinY = abstandVertikal + feldSize*position.zeile + steinAbstand;

            applet.ellipseMode(PApplet.CORNER);
            applet.stroke(0);
            applet.strokeWeight(2);
            applet.fill(stein.map(spieler -> spieler.getColor(applet)).orElseGet(() -> applet.color(0)));
            applet.circle(steinX, steinY, steinSize);
        }

        private void draw(PApplet applet) {
            applet.background(applet.color(255));

            int abstandHorizontal = getAbstandHorizontal(applet);
            int abstandVertikal = getAbstandVertikal(applet);
            int breite = getBreite(applet);
            int hoehe = getHoehe(applet);

            applet.fill(3, 26, 94);
            applet.noStroke();
            applet.rect(abstandHorizontal, abstandVertikal, breite, hoehe);

            for (int zeile = 0; zeile < HEIGHT; zeile++) {
                for (int spalte = 0; spalte < WIDTH; spalte++) {
                    drawStein(applet, new Position(zeile, spalte));
                }
            }
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
    }

    private static final class Position {
        private static boolean isValidZeile(int zeile) {
            return zeile >=0 && zeile < Brett.HEIGHT;
        }

        private static boolean isValidSpalte(int spalte) {
            return spalte >= 0 && spalte < Brett.WIDTH;
        }

        private static boolean isValid(int zeile, int spalte) {
            return isValidZeile(zeile) && isValidSpalte(spalte);
        }

        private static Optional<Position> create(int zeile, int spalte) {
            if (!isValid(zeile, spalte)) {
                return Optional.empty();
            }
            return Optional.of(new Position(zeile, spalte));
        }

        private static OptionalInt spalteFromMausPosition(PApplet applet) {
            int abstandHorizontal = Brett.getAbstandHorizontal(applet);
            int steinSize = Brett.getFeldSize(applet);
            int mouseXOffset = applet.mouseX - abstandHorizontal;
            int spalte = mouseXOffset / steinSize;
            if (!isValidSpalte(spalte)) {
                return OptionalInt.empty();
            }
            return OptionalInt.of(spalte);
        }

        private final int zeile;
        private final int spalte;

        private Position(int zeile, int spalte) {
            if (!isValid(zeile, spalte)) {
                new Exception().printStackTrace();
                throw new IllegalArgumentException();
            }

            this.zeile = zeile;
            this.spalte = spalte;
        }

        private Optional<Position> add(int zeilen, int spalten) {
            return create(zeile + zeilen, spalte + spalten);
        }
    }

    private static final class Zug implements AI.Zug<Brett> {
        private final int spalte;
        private final Brett ergebnis;

        private Zug(int spalte, Brett ergebnis) {
            this.spalte = spalte;
            this.ergebnis = ergebnis;
        }

        @Override
        public Brett getErgebnis() {
            return ergebnis;
        }
    }

    static final class SpielerGegenSpielerSpiel extends MiniSpiel {
        private Brett aktuellesBrett = Brett.LEER;
        private Spieler amZug;

        SpielerGegenSpielerSpiel(PApplet applet) {
            super(applet);
            amZug = applet.random(1) > 0.5 ? Spieler.SPIELER_1 : Spieler.SPIELER_2;
        }

        @Override
        void mousePressed() {
            OptionalInt mausSpalte = Position.spalteFromMausPosition(applet);
            if (mausSpalte.isEmpty()) {
                return;
            }

            Set<Zug> moeglicheZuege = aktuellesBrett.getMoeglicheZuegeFuerSpieler(amZug);
            for (Zug moeglicherZug : moeglicheZuege) {
                if (moeglicherZug.spalte == mausSpalte.getAsInt()) {
                    aktuellesBrett = moeglicherZug.ergebnis;
                    amZug = amZug.getGegner();
                    return;
                }
            }
        }

        @Override
        void draw() {
            aktuellesBrett.draw(applet);
        }
    }

    static final class SpielerGegenAISpiel extends MiniSpiel {
        private static final int AI_TIEFE = 6;
        private static final Spieler MENSCH = Spieler.SPIELER_1;
        private static final Spieler COMPUTER = Spieler.SPIELER_2;

        // In der Mitte anzufangen ist in Vier Gewinnt immer eine gute Iddee.
        // Unsere AI guckt aber nicht weit genug in die Zukunft, um das zu verstehen, also geben wir ihr einen kleinen Tipp.
        private Brett aktuellesBrett = Brett.LEER.mitStein(new Position(5, 3), Optional.of(COMPUTER));

        SpielerGegenAISpiel(PApplet applet) {
            super(applet);
        }

        @Override
        void mousePressed() {
            OptionalInt mausSpalte = Position.spalteFromMausPosition(applet);
            if (mausSpalte.isEmpty()) {
                return;
            }

            Set<Zug> moeglicheZuege = aktuellesBrett.getMoeglicheZuegeFuerSpieler(MENSCH);
            for (Zug moeglicherZug : moeglicheZuege) {
                if (moeglicherZug.spalte == mausSpalte.getAsInt()) {
                    Optional<Zug> antwort = AI.bestenNaechstenZugBerechnen(moeglicherZug.ergebnis, COMPUTER, AI_TIEFE);
                    if (antwort.isEmpty()) {
                        aktuellesBrett = moeglicherZug.ergebnis;
                        return;
                    }
                    aktuellesBrett = antwort.get().ergebnis;
                    return;
                }
            }
        }

        @Override
        void draw() {
            aktuellesBrett.draw(applet);
        }
    }
}
