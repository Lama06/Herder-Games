package herdergames.util;

import herdergames.spiel.Spieler;
import processing.core.PApplet;
import processing.core.PConstants;

import java.util.*;

public final class Steuerung {
    private final PApplet applet;
    private final Spieler.Id spieler;
    private final Set<Richtung> gedrueckt = new HashSet<>();
    private Optional<Richtung> zuletztGedrueckt = Optional.empty();

    public Steuerung(PApplet applet, Spieler.Id spieler) {
        this.applet = applet;
        this.spieler = spieler;
    }

    public void keyPressed() {
        Richtung.getGedrueckt(applet, spieler).ifPresent(richtung -> {
            gedrueckt.add(richtung);
            zuletztGedrueckt = Optional.of(richtung);
        });
    }

    public void keyReleased() {
        Richtung.getGedrueckt(applet, spieler).ifPresent(richtung -> {
            gedrueckt.remove(richtung);

            if (zuletztGedrueckt.isPresent() && zuletztGedrueckt.get() == richtung && !gedrueckt.isEmpty()) {
                List<Richtung> gedruecktListe = gedrueckt.stream().toList();
                this.zuletztGedrueckt = Optional.of(gedruecktListe.get(applet.choice(gedruecktListe.size())));
            }
        });
    }

    public int getXRichtung() {
        return gedrueckt.stream().mapToInt(richtung -> richtung.x).sum();
    }

    public int getYRichtung() {
        return gedrueckt.stream().mapToInt(richtung -> richtung.y).sum();
    }

    public boolean istLinksGedrueckt() {
        return gedrueckt.contains(Richtung.LINKS);
    }

    public boolean istRechtsGedrueckt() {
        return gedrueckt.contains(Richtung.RECHTS);
    }

    public boolean istObenGedrueckt() {
        return gedrueckt.contains(Richtung.OBEN);
    }

    public boolean istUntenGedrueckt() {
        return gedrueckt.contains(Richtung.UNTEN);
    }

    public Optional<Richtung> getZuletztGedrueckt() {
        return zuletztGedrueckt;
    }

    public enum Richtung {
        OBEN(0, -1) {
            @Override
            public boolean istTasteGedrueckt(PApplet applet, Spieler.Id spieler) {
                return switch (spieler) {
                    case SPIELER_1 -> applet.key == 'w';
                    case SPIELER_2 -> applet.key == 't';
                    case SPIELER_3 -> applet.key == 'i';
                    case SPIELER_4 -> applet.key == PConstants.CODED && applet.keyCode == PConstants.UP;
                };
            }

            @Override
            public String getTasteName(Spieler.Id spieler) {
                return switch (spieler) {
                    case SPIELER_1 -> "W";
                    case SPIELER_2 -> "T";
                    case SPIELER_3 -> "I";
                    case SPIELER_4 -> "Pfeiltaste Hoch";
                };
            }
        },
        LINKS(-1, 0) {
            @Override
            public boolean istTasteGedrueckt(PApplet applet, Spieler.Id spieler) {
                return switch (spieler) {
                    case SPIELER_1 -> applet.key == 'a';
                    case SPIELER_2 -> applet.key == 'f';
                    case SPIELER_3 -> applet.key == 'j';
                    case SPIELER_4 -> applet.key == PConstants.CODED && applet.keyCode == PConstants.LEFT;
                };
            }

            @Override
            public String getTasteName(Spieler.Id spieler) {
                return switch (spieler) {
                    case SPIELER_1 -> "A";
                    case SPIELER_2 -> "F";
                    case SPIELER_3 -> "J";
                    case SPIELER_4 -> "Pfeiltaste Links";
                };
            }
        },
        RECHTS(1, 0) {
            @Override
            public boolean istTasteGedrueckt(PApplet applet, Spieler.Id spieler) {
                return switch (spieler) {
                    case SPIELER_1 -> applet.key == 'd';
                    case SPIELER_2 -> applet.key == 'h';
                    case SPIELER_3 -> applet.key == 'l';
                    case SPIELER_4 -> applet.key == PConstants.CODED && applet.keyCode == PConstants.RIGHT;
                };
            }

            @Override
            public String getTasteName(Spieler.Id spieler) {
                return switch (spieler) {
                    case SPIELER_1 -> "D";
                    case SPIELER_2 -> "H";
                    case SPIELER_3 -> "L";
                    case SPIELER_4 -> "Pfeiltaste Rechts";
                };
            }
        },
        UNTEN(0, 1) {
            @Override
            public boolean istTasteGedrueckt(PApplet applet, Spieler.Id spieler) {
                return switch (spieler) {
                    case SPIELER_1 -> applet.key == 's';
                    case SPIELER_2 -> applet.key == 'g';
                    case SPIELER_3 -> applet.key == 'k';
                    case SPIELER_4 -> applet.key == PConstants.CODED && applet.keyCode == PConstants.DOWN;
                };
            }

            @Override
            public String getTasteName(Spieler.Id spieler) {
                return switch (spieler) {
                    case SPIELER_1 -> "S";
                    case SPIELER_2 -> "G";
                    case SPIELER_3 -> "K";
                    case SPIELER_4 -> "Pfeiltaste Unten";
                };
            }
        };

        public static Optional<Richtung> getGedrueckt(PApplet applet, Spieler.Id spieler) {
            return Arrays.stream(Richtung.values()).filter(richtung -> richtung.istTasteGedrueckt(applet, spieler)).findFirst();
        }

        public final int x;
        public final int y;

        Richtung(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public abstract boolean istTasteGedrueckt(PApplet applet, Spieler.Id spieler);

        public abstract String getTasteName(Spieler.Id spieler);
    }
}
