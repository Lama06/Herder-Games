package herdergames.vier_gewinnt;

import processing.core.PApplet;

import java.util.*;

record Brett(List<List<Optional<Spieler>>> zeilen) implements herdergames.ai.Brett<Brett, Zug, Spieler> {
    static final int WIDTH = 7;
    static final int HEIGHT = 6;

    static final Brett LEER = createLeer();

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

    Brett(List<List<Optional<Spieler>>> zeilen) {
        if (zeilen.size() != HEIGHT) {
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
        return zeilen.get(position.zeile()).get(position.spalte());
    }

    Brett mitStein(Position position, Optional<Spieler> stein) {
        List<List<Optional<Spieler>>> neueZeilen = new ArrayList<>(HEIGHT);
        for (int zeile = 0; zeile < HEIGHT; zeile++) {
            if (position.zeile() == zeile) {
                List<Optional<Spieler>> neueZeile = new ArrayList<>(zeilen.get(zeile));
                neueZeile.set(position.spalte(), stein);
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
                    verschiebungVertikal.verschiebung * i,
                    verschiebungHorizontal.verschiebung * i
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

    boolean hatGewonnen(Spieler spieler) {
        return countReihen(spieler, 4) >= 1;
    }

    private OptionalInt getUntersteFreiheZeile(int spalte) {
        if (!Position.isValidSpalte(spalte)) {
            throw new IllegalArgumentException();
        }

        for (int zeile = HEIGHT - 1; zeile >= 0; zeile--) {
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
        int perspektiveBewertung = perspektiveReihen3 * REIHE3_BEWERTUNG;

        int gegnerReihen3 = countReihen(perspektive.getGegner(), 3);
        int gegnerBewertung = gegnerReihen3 * REIHE3_BEWERTUNG;

        return perspektiveBewertung - gegnerBewertung;
    }

    static int getFeldSize(PApplet applet) {
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

    static int getAbstandHorizontal(PApplet applet) {
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

        int steinX = abstandHorizontal + feldSize * position.spalte() + steinAbstand;
        int steinY = abstandVertikal + feldSize * position.zeile() + steinAbstand;

        applet.ellipseMode(PApplet.CORNER);
        applet.stroke(0);
        applet.strokeWeight(2);
        applet.fill(stein.map(spieler -> spieler.getColor(applet)).orElseGet(() -> applet.color(255)));
        applet.circle(steinX, steinY, steinSize);
    }

    void draw(PApplet applet) {
        applet.background(applet.color(0));

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
