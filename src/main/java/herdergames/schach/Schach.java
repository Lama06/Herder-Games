package herdergames.schach;

import herdergames.spiel.EinzelspielerSpiel;
import herdergames.ai.AI;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.util.*;

public final class Schach {
    private static PImage WEISS_BAUER;
    private static PImage WEISS_LAEUFER;
    private static PImage WEISS_SPRINGER;
    private static PImage WEISS_TURM;
    private static PImage WEISS_DAME;
    private static PImage WEISS_KOENIG;

    private static PImage SCHWARZ_BAUER;
    private static PImage SCHWARZ_LAEUFER;
    private static PImage SCHWARZ_SPRINGER;
    private static PImage SCHWARZ_TURM;
    private static PImage SCHWARZ_DAME;
    private static PImage SCHWARZ_KOENIG;

    public static void init(PApplet applet) {
        WEISS_BAUER = applet.loadImage("schach/weiss/bauer.png");
        WEISS_LAEUFER = applet.loadImage("schach/weiss/laeufer.png");
        WEISS_SPRINGER = applet.loadImage("schach/weiss/springer.png");
        WEISS_TURM = applet.loadImage("schach/weiss/turm.png");
        WEISS_DAME = applet.loadImage("schach/weiss/dame.png");
        WEISS_KOENIG = applet.loadImage("schach/weiss/koenig.png");

        SCHWARZ_BAUER = applet.loadImage("schach/schwarz/bauer.png");
        SCHWARZ_LAEUFER = applet.loadImage("schach/schwarz/laeufer.png");
        SCHWARZ_SPRINGER = applet.loadImage("schach/schwarz/springer.png");
        SCHWARZ_TURM = applet.loadImage("schach/schwarz/turm.png");
        SCHWARZ_DAME = applet.loadImage("schach/schwarz/dame.png");
        SCHWARZ_KOENIG = applet.loadImage("schach/schwarz/koenig.png");
    }

    private Schach() {}

    private static abstract class Verschiebung1D {
        final int verschiebung;

        private Verschiebung1D(int verschiebung) {
            this.verschiebung = verschiebung;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Verschiebung1D that = (Verschiebung1D) o;
            return verschiebung == that.verschiebung;
        }

        @Override
        public int hashCode() {
            return Objects.hash(verschiebung);
        }

        private static final class Vertikal extends Verschiebung1D {
            private static final Vertikal OBEN = new Vertikal(-1);
            private static final Vertikal KEINE = new Vertikal(0);
            private static final Vertikal UNTEN = new Vertikal(1);

            private Vertikal(int verschiebung) {
                super(verschiebung);
            }

            private Vertikal doppelt() {
                return new Vertikal(verschiebung * 2);
            }
        }

        private static final class Horizontal extends Verschiebung1D {
            private static final Horizontal LINKS = new Horizontal(-1);
            private static final Horizontal KEINE = new Horizontal(0);
            private static final Horizontal RECHTS = new Horizontal(1);

            private static final Set<Horizontal> LINKS_RECHTS = Set.of(LINKS, RECHTS);

            private Horizontal(int verschiebung) {
                super(verschiebung);
            }

            private Horizontal doppelt() {
                return new Horizontal(verschiebung * 2);
            }
        }
    }

    private static class Verschiebung2D {
        private static final Set<Verschiebung2D> LAEUFER_VERSCHIEBUNGEN = Set.of(
                new Verschiebung2D(Verschiebung1D.Vertikal.OBEN, Verschiebung1D.Horizontal.LINKS),
                new Verschiebung2D(Verschiebung1D.Vertikal.OBEN, Verschiebung1D.Horizontal.RECHTS),
                new Verschiebung2D(Verschiebung1D.Vertikal.UNTEN, Verschiebung1D.Horizontal.RECHTS),
                new Verschiebung2D(Verschiebung1D.Vertikal.UNTEN, Verschiebung1D.Horizontal.LINKS)
        );

        private static final Set<Verschiebung2D> SPRINGER_VERSCHIEBUNGEN = Set.of(
                new Verschiebung2D(Verschiebung1D.Vertikal.OBEN.doppelt(), Verschiebung1D.Horizontal.LINKS),
                new Verschiebung2D(Verschiebung1D.Vertikal.OBEN.doppelt(), Verschiebung1D.Horizontal.RECHTS),

                new Verschiebung2D(Verschiebung1D.Vertikal.OBEN, Verschiebung1D.Horizontal.LINKS.doppelt()),
                new Verschiebung2D(Verschiebung1D.Vertikal.UNTEN, Verschiebung1D.Horizontal.LINKS.doppelt()),

                new Verschiebung2D(Verschiebung1D.Vertikal.UNTEN.doppelt(), Verschiebung1D.Horizontal.LINKS),
                new Verschiebung2D(Verschiebung1D.Vertikal.UNTEN.doppelt(), Verschiebung1D.Horizontal.RECHTS),

                new Verschiebung2D(Verschiebung1D.Vertikal.OBEN, Verschiebung1D.Horizontal.RECHTS.doppelt()),
                new Verschiebung2D(Verschiebung1D.Vertikal.UNTEN, Verschiebung1D.Horizontal.RECHTS.doppelt())
        );

        private static final Set<Verschiebung2D> TURM_VERSCHIEBUNGEN = Set.of(
                new Verschiebung2D(Verschiebung1D.Vertikal.OBEN, Verschiebung1D.Horizontal.KEINE),
                new Verschiebung2D(Verschiebung1D.Vertikal.UNTEN, Verschiebung1D.Horizontal.KEINE),
                new Verschiebung2D(Verschiebung1D.Vertikal.KEINE, Verschiebung1D.Horizontal.LINKS),
                new Verschiebung2D(Verschiebung1D.Vertikal.KEINE, Verschiebung1D.Horizontal.RECHTS)
        );

        private static final Set<Verschiebung2D> ALLE_VERSCHIEBUNGEN = Set.of(
                new Verschiebung2D(Verschiebung1D.Vertikal.OBEN, Verschiebung1D.Horizontal.LINKS),
                new Verschiebung2D(Verschiebung1D.Vertikal.OBEN, Verschiebung1D.Horizontal.KEINE),
                new Verschiebung2D(Verschiebung1D.Vertikal.OBEN, Verschiebung1D.Horizontal.RECHTS),

                new Verschiebung2D(Verschiebung1D.Vertikal.KEINE, Verschiebung1D.Horizontal.LINKS),
                new Verschiebung2D(Verschiebung1D.Vertikal.KEINE, Verschiebung1D.Horizontal.KEINE),
                new Verschiebung2D(Verschiebung1D.Vertikal.KEINE, Verschiebung1D.Horizontal.RECHTS),

                new Verschiebung2D(Verschiebung1D.Vertikal.UNTEN, Verschiebung1D.Horizontal.LINKS),
                new Verschiebung2D(Verschiebung1D.Vertikal.UNTEN, Verschiebung1D.Horizontal.KEINE),
                new Verschiebung2D(Verschiebung1D.Vertikal.UNTEN, Verschiebung1D.Horizontal.RECHTS)
        );

        private final Verschiebung1D.Vertikal vertikal;
        private final Verschiebung1D.Horizontal horizontal;

        private Verschiebung2D(Verschiebung1D.Vertikal vertikal, Verschiebung1D.Horizontal horizontal) {
            this.vertikal = vertikal;
            this.horizontal = horizontal;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Verschiebung2D that = (Verschiebung2D) o;
            return Objects.equals(vertikal, that.vertikal) && Objects.equals(horizontal, that.horizontal);
        }

        @Override
        public int hashCode() {
            return Objects.hash(vertikal, horizontal);
        }
    }

    private enum Spieler implements herdergames.ai.Spieler<Spieler> {
        SCHWARZ,
        WEISS;

        @Override
        public Spieler getGegner() {
            switch (this) {
                case SCHWARZ:
                    return WEISS;
                case WEISS:
                    return SCHWARZ;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private Verschiebung1D.Vertikal getBauerBewegenVerschiebung() {
            switch (this) {
                case SCHWARZ:
                    return Verschiebung1D.Vertikal.OBEN;
                case WEISS:
                    return Verschiebung1D.Vertikal.UNTEN;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private int getBauernUmwandlungsZeile() {
            switch (this) {
                case SCHWARZ:
                    return 0;
                case WEISS:
                    return 7;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private int getBauernStartZeile() {
            switch (this) {
                case SCHWARZ:
                    return 6;
                case WEISS:
                    return 1;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    private enum Figur {
        BAUER(1),
        LAEUFER(3),
        SPRINGER(3),
        TURM(5),
        DAME(8),
        KOENIG(0);

        private final int wert;

        Figur(int wert) {
            this.wert = wert;
        }
    }

    private static final class Stein {
        private final Spieler spieler;
        private final Figur figur;

        private Stein(Spieler spieler, Figur figur) {
            this.spieler = spieler;
            this.figur = figur;
        }

        private PImage getImage() {
            switch (spieler) {
                case WEISS:
                    switch (figur) {
                        case BAUER:
                            return WEISS_BAUER;
                        case LAEUFER:
                            return WEISS_LAEUFER;
                        case SPRINGER:
                            return WEISS_SPRINGER;
                        case TURM:
                            return WEISS_TURM;
                        case DAME:
                            return WEISS_DAME;
                        case KOENIG:
                            return WEISS_KOENIG;
                        default:
                            throw new IllegalArgumentException();
                    }
                case SCHWARZ:
                    switch (figur) {
                        case BAUER:
                            return SCHWARZ_BAUER;
                        case LAEUFER:
                            return SCHWARZ_LAEUFER;
                        case SPRINGER:
                            return SCHWARZ_SPRINGER;
                        case TURM:
                            return SCHWARZ_TURM;
                        case DAME:
                            return SCHWARZ_DAME;
                        case KOENIG:
                            return SCHWARZ_KOENIG;
                        default:
                            throw new IllegalArgumentException();
                    }
                default:
                    throw new IllegalArgumentException();
            }
        }

        private void draw(PApplet applet, int x, int y, int width, int height) {
            applet.imageMode(PConstants.CORNER);
            applet.image(getImage(), x, y, width, height);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Stein stein = (Stein) o;
            return spieler == stein.spieler && figur == stein.figur;
        }

        @Override
        public int hashCode() {
            return Objects.hash(spieler, figur);
        }
    }

    private static class Position {
        private static boolean isValid(int zeile, int spalte) {
            return zeile >= 0 && zeile < Brett.SIZE && spalte >= 0 && spalte < Brett.SIZE;
        }

        private static Optional<Position> create(int zeile, int spalte) {
            if (!isValid(zeile, spalte)) {
                return Optional.empty();
            }
            return Optional.of(new Position(zeile, spalte));
        }

        private static Optional<Position> fromMousePosition(PApplet applet) {
            int mouseXOffset = applet.mouseX - Brett.getAbstandHorizontal(applet);
            int mouseYOffset = applet.mouseY - Brett.getAbstandVertikal(applet);
            int feldSize = Brett.getFeldSize(applet);
            int zeile = mouseYOffset / feldSize;
            int spalte = mouseXOffset / feldSize;
            return Position.create(zeile, spalte);
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
            return create(zeile + zeilen, spalte + spalten);
        }

        private boolean isSchwarz() {
            return zeile%2 == spalte%2;
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

    private static final class Brett implements herdergames.ai.Brett<Brett, Zug, Spieler> {
        private static final int SIZE = 8;

        private static final Brett ANFANG = createAnfang();
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

        private final List<List<Optional<Stein>>> zeilen;

        private Brett(List<List<Optional<Stein>>> zeilen) {
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

        private Optional<Stein> getStein(Position position) {
            return zeilen.get(position.zeile).get(position.spalte);
        }

        private Brett mitSteinen(Map<Position, Optional<Stein>> neueSteine) {
            List<List<Optional<Stein>>> neueZeilen = new ArrayList<>(SIZE);
            for (int zeile = 0; zeile < SIZE; zeile++) {
                boolean zeileVeraendert = false;
                for (Position position : neueSteine.keySet()) {
                    if (position.zeile == zeile) {
                        zeileVeraendert = true;
                        break;
                    }
                }

                if (zeileVeraendert) {
                    List<Optional<Stein>> neueZeile = new ArrayList<>(zeilen.get(zeile));
                    for (Position position : neueSteine.keySet()) {
                        if (position.zeile != zeile) {
                            continue;
                        }
                        neueZeile.set(position.spalte, neueSteine.get(position));
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

        private Optional<Zug> getBauerDoppeltBewegenZug(Position startPosition) {
            Optional<Stein> stein = getStein(startPosition);
            if (stein.isEmpty() || stein.get().figur != Figur.BAUER) {
                return Optional.empty();
            }
            Spieler spieler = stein.get().spieler;
            if (startPosition.zeile != spieler.getBauernStartZeile()) {
                return Optional.empty();
            }

            Optional<Position> uebersprungenePosition = startPosition.add(
                    spieler.getBauerBewegenVerschiebung().verschiebung,
                    0
            );
            if (uebersprungenePosition.isEmpty()) {
                return Optional.empty();
            }
            if (getStein(uebersprungenePosition.get()).isPresent()) {
                return Optional.empty();
            }

            Optional<Position> neuePosition = startPosition.add(
                    spieler.getBauerBewegenVerschiebung().verschiebung*2,
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
            if (stein.isEmpty() || stein.get().figur != Figur.BAUER) {
                return Optional.empty();
            }
            Spieler spieler = stein.get().spieler;

            Optional<Position> neuePosition = startPosition.add(
                    spieler.getBauerBewegenVerschiebung().verschiebung,
                    0
            );
            if (neuePosition.isEmpty()) {
                return Optional.empty();
            }
            if (getStein(neuePosition.get()).isPresent()) {
                return Optional.empty();
            }

            boolean umwandlung = neuePosition.get().zeile == spieler.getBauernUmwandlungsZeile();
            Figur neueFigur = umwandlung ? Figur.DAME : Figur.BAUER;

            Brett neuesBrett = mitSteinen(Map.of(
                    startPosition, Optional.empty(),
                    neuePosition.get(), Optional.of(new Stein(spieler, neueFigur))
            ));
            return Optional.of(new Zug(startPosition, neuePosition.get(), neuesBrett));
        }

        private Set<Zug> getMoeglicheBauerSchlagenZuege(Position startPosition) {
            Optional<Stein> stein = getStein(startPosition);
            if (stein.isEmpty() || stein.get().figur != Figur.BAUER) {
                return Collections.emptySet();
            }
            Spieler spieler = stein.get().spieler;

            Set<Zug> result = new HashSet<>();

            for (Verschiebung1D.Horizontal verschiebungHorizontal : Verschiebung1D.Horizontal.LINKS_RECHTS) {
                Optional<Position> neuePosition = startPosition.add(
                        spieler.getBauerBewegenVerschiebung().verschiebung,
                        verschiebungHorizontal.verschiebung
                );
                if (neuePosition.isEmpty()) {
                    continue;
                }
                Optional<Stein> schlagenStein = getStein(neuePosition.get());
                if (schlagenStein.isEmpty() || schlagenStein.get().spieler == spieler) {
                    continue;
                }

                boolean umwandlung = neuePosition.get().zeile == spieler.getBauernUmwandlungsZeile();
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
            if (stein.isEmpty() || stein.get().figur != figur) {
                return Collections.emptySet();
            }
            Spieler spieler = stein.get().spieler;

            Set<Zug> result = new HashSet<>();

            verschiebungen:
            for (Verschiebung2D verschiebung : verschiebungen) {
                for (int anzahlVerschiebungen = 1; anzahlVerschiebungen <= maxVerschiebungen; anzahlVerschiebungen++) {
                    Optional<Position> neuePosition = startPosition.add(
                            verschiebung.vertikal.verschiebung*anzahlVerschiebungen,
                            verschiebung.horizontal.verschiebung*anzahlVerschiebungen
                    );
                    if (neuePosition.isEmpty()) {
                        continue;
                    }
                    Optional<Stein> schlagenStein = getStein(neuePosition.get());
                    if (schlagenStein.isPresent() && schlagenStein.get().spieler == spieler) {
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
                if (gegnerZug.ergebnis.countSteine(new Stein(spieler, Figur.KOENIG)) == 0) {
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
                    if (stein.isEmpty() || stein.get().spieler != spieler) {
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
                result.removeIf(zug -> zug.ergebnis.imSchach(spieler));
            }

            return result;
        }

        @Override
        public Set<Zug> getMoeglicheZuegeFuerSpieler(Spieler spieler) {
            return getMoeglicheZuegeFuerSpieler(spieler, true);
        }

        private Set<Zug> getMoeglicheZuegeFuerPosition(Position position) {
            Optional<Stein> stein = getStein(position);
            if (stein.isEmpty()) {
                return Collections.emptySet();
            }
            Spieler spieler = stein.get().spieler;

            Set<Zug> zuege = new HashSet<>(getMoeglicheZuegeFuerSpieler(spieler));
            zuege.removeIf(zug -> !zug.von.equals(position));
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

        private boolean hatVerloren(Spieler spieler) {
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

        private static int getSize(PApplet applet) {
            return Math.min(applet.width, applet.height);
        }

        private static int getAbstandHorizontal(PApplet applet) {
            return (applet.width- getSize(applet)) / 2;
        }

        private static int getAbstandVertikal(PApplet applet) {
            return (applet.height- getSize(applet)) / 2;
        }

        private static int getFeldSize(PApplet applet) {
            return getSize(applet) / SIZE;
        }

        private static int getSteinSize(PApplet applet) {
            return (getFeldSize(applet) / 3) * 2;
        }

        private static int getSteinAbstand(PApplet applet) {
            return (getFeldSize(applet) - getSteinSize(applet)) / 2;
        }

        private void draw(PApplet applet, Optional<Position> ausgewaehltePosition) {
            applet.background(255);

            Set<Position> possibleMovePositions = new HashSet<>();
            if (ausgewaehltePosition.isPresent()) {
                for (Zug zug : getMoeglicheZuegeFuerPosition(ausgewaehltePosition.get())) {
                    possibleMovePositions.add(zug.nach);
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

                    int screenX = abstandX + feldSize*spalte;
                    int screenY = abstandY + feldSize*zeile;

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
                    stein.get().draw(applet, screenX+steinAbstand, screenY+steinAbstand, steinSize, steinSize);
                }
            }
        }
    }

    private static final class Zug implements herdergames.ai.Zug<Brett> {
        private final Position von;
        private final Position nach;
        private final Brett ergebnis;

        private Zug(Position von, Position nach, Brett ergebnis) {
            this.von = von;
            this.nach = nach;
            this.ergebnis = ergebnis;
        }

        @Override
        public Brett ergebnis() {
            return ergebnis;
        }
    }


    public static final class SpielerGegenSpielerSpiel extends herdergames.spiel.SpielerGegenSpielerSpiel {
        private final herdergames.spiel.Spieler weiss;
        private final herdergames.spiel.Spieler schwarz;
        private Brett aktuellesBrett = Brett.ANFANG;
        private Optional<Position> ausgewaehltePosition = Optional.empty();
        private Schach.Spieler amZug = Schach.Spieler.WEISS;

        public SpielerGegenSpielerSpiel(PApplet applet, herdergames.spiel.Spieler spieler1, herdergames.spiel.Spieler spieler2) {
            super(applet);
            if (spieler1.punkte() < spieler2.punkte()) {
                weiss = spieler1;
                schwarz = spieler2;
            } else {
                weiss = spieler2;
                schwarz = spieler1;
            }
        }

        private void selectNewField() {
            Optional<Position> position = Position.fromMousePosition(applet);
            if (position.isEmpty()) {
                ausgewaehltePosition = Optional.empty();
                return;
            }
            Optional<Stein> stein = aktuellesBrett.getStein(position.get());
            if (stein.isEmpty() || stein.get().spieler != amZug) {
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
                    aktuellesBrett = moeglicherZug.ergebnis();
                    amZug = amZug.getGegner();
                    ausgewaehltePosition = Optional.empty();
                    return;
                }
            }
        }

        @Override
        public void mousePressed() {
            zugMachen();
            selectNewField();
        }

        @Override
        public Optional<Optional<herdergames.spiel.Spieler.Id>> draw() {
            aktuellesBrett.draw(applet, ausgewaehltePosition);

            if (aktuellesBrett.hatVerloren(Schach.Spieler.WEISS)) {
                return Optional.of(Optional.of(schwarz.id()));
            }
            if (aktuellesBrett.hatVerloren(Schach.Spieler.SCHWARZ)) {
                return Optional.of(Optional.of(weiss.id()));
            }

            return Optional.empty();
        }
    }

    public static final class SpielerGegenAISpiel extends EinzelspielerSpiel {
        private static final int AI_DEPTH = 2;

        private static final Schach.Spieler COMPUTER = Schach.Spieler.WEISS;
        private static final Schach.Spieler MENSCH = Schach.Spieler.SCHWARZ;

        private Brett aktuellesBrett = Brett.ANFANG;
        private Optional<Position> ausgewaehltePosition = Optional.empty();

        public SpielerGegenAISpiel(PApplet applet, herdergames.spiel.Spieler spieler) {
            super(applet);
        }

        private void selectNewField() {
            Optional<Position> position = Position.fromMousePosition(applet);
            if (position.isEmpty()) {
                ausgewaehltePosition = Optional.empty();
                return;
            }
            Optional<Stein> stein = aktuellesBrett.getStein(position.get());
            if (stein.isEmpty() || stein.get().spieler != MENSCH) {
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
                    Optional<Zug> antwort = AI.bestenNaechstenZugBerechnen(moeglicherZug.ergebnis(), COMPUTER, AI_DEPTH);
                    if (antwort.isEmpty()) {
                        aktuellesBrett = moeglicherZug.ergebnis();
                        ausgewaehltePosition = Optional.empty();
                        return;
                    }

                    aktuellesBrett = antwort.get().ergebnis();
                    ausgewaehltePosition = Optional.empty();
                    return;
                }
            }
        }

        @Override
        public void mousePressed() {
            zugMachen();
            selectNewField();
        }

        @Override
        public Optional<Ergebnis> draw() {
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
