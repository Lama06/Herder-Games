package herdergames.util;

import herdergames.spiel.Spiel;
import processing.core.PApplet;
import processing.core.PConstants;

import java.util.*;

public final class Steuerung {
    private final PApplet applet;
    private final Spiel.Spieler.Id spieler;
    private final Set<Richtung> gedrueckt = new HashSet<>();
    private Optional<Richtung> zuletztGedrueckt = Optional.empty();

    public Steuerung(PApplet applet, Spiel.Spieler.Id spieler) {
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
            zuletztGedrueckt.ifPresent(zuletztGedrueckt -> {
                if (gedrueckt.contains(zuletztGedrueckt)) {
                    return;
                }
                List<Richtung> gedruecktListe = gedrueckt.stream().toList();
                if (gedruecktListe.isEmpty()) {
                    return;
                }
                this.zuletztGedrueckt = Optional.of(gedruecktListe.get(applet.choice(gedruecktListe.size())));
            });
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
            public boolean istTasteGedrueckt(PApplet applet, Spiel.Spieler.Id spieler) {
                switch (spieler) {
                    case SPIELER_1:
                        return applet.key == 'w';
                    case SPIELER_2:
                        return applet.key == 't';
                    case SPIELER_3:
                        return applet.key == 'i';
                    case SPIELER_4:
                        return applet.key == PConstants.CODED && applet.keyCode == PConstants.UP;
                    default:
                        throw new IllegalArgumentException();
                }
            }

            @Override
            public String getTasteName(Spiel.Spieler.Id spieler) {
                switch (spieler) {
                    case SPIELER_1:
                        return "W";
                    case SPIELER_2:
                        return "T";
                    case SPIELER_3:
                        return "I";
                    case SPIELER_4:
                        return "Pfeiltaste Hoch";
                    default:
                        throw new IllegalArgumentException();
                }
            }
        },
        LINKS(-1, 0) {
            @Override
            public boolean istTasteGedrueckt(PApplet applet, Spiel.Spieler.Id spieler) {
                switch (spieler) {
                    case SPIELER_1:
                        return applet.key == 'a';
                    case SPIELER_2:
                        return applet.key == 'f';
                    case SPIELER_3:
                        return applet.key == 'j';
                    case SPIELER_4:
                        return applet.key == PConstants.CODED && applet.keyCode == PConstants.LEFT;
                    default:
                        throw new IllegalArgumentException();
                }
            }

            @Override
            public String getTasteName(Spiel.Spieler.Id spieler) {
                switch (spieler) {
                    case SPIELER_1:
                        return "A";
                    case SPIELER_2:
                        return "F";
                    case SPIELER_3:
                        return "J";
                    case SPIELER_4:
                        return "Pfeiltaste Links";
                    default:
                        throw new IllegalArgumentException();
                }
            }
        },
        RECHTS(1, 0) {
            @Override
            public boolean istTasteGedrueckt(PApplet applet, Spiel.Spieler.Id spieler) {
                switch (spieler) {
                    case SPIELER_1:
                        return applet.key == 'd';
                    case SPIELER_2:
                        return applet.key == 'h';
                    case SPIELER_3:
                        return applet.key == 'l';
                    case SPIELER_4:
                        return applet.key == PConstants.CODED && applet.keyCode == PConstants.RIGHT;
                    default:
                        throw new IllegalArgumentException();
                }
            }

            @Override
            public String getTasteName(Spiel.Spieler.Id spieler) {
                switch (spieler) {
                    case SPIELER_1:
                        return "D";
                    case SPIELER_2:
                        return "H";
                    case SPIELER_3:
                        return "L";
                    case SPIELER_4:
                        return "Pfeiltaste Rechts";
                    default:
                        throw new IllegalArgumentException();
                }
            }
        },
        UNTEN(0, 1) {
            @Override
            public boolean istTasteGedrueckt(PApplet applet, Spiel.Spieler.Id spieler) {
                switch (spieler) {
                    case SPIELER_1:
                        return applet.key == 's';
                    case SPIELER_2:
                        return applet.key == 'g';
                    case SPIELER_3:
                        return applet.key == 'k';
                    case SPIELER_4:
                        return applet.key == PConstants.CODED && applet.keyCode == PConstants.DOWN;
                    default:
                        throw new IllegalArgumentException();
                }
            }

            @Override
            public String getTasteName(Spiel.Spieler.Id spieler) {
                switch (spieler) {
                    case SPIELER_1:
                        return "S";
                    case SPIELER_2:
                        return "G";
                    case SPIELER_3:
                        return "K";
                    case SPIELER_4:
                        return "Pfeiltaste Unten";
                    default:
                        throw new IllegalArgumentException();
                }
            }
        };

        public static Optional<Richtung> getGedrueckt(PApplet applet, Spiel.Spieler.Id spieler) {
            return Arrays.stream(Richtung.values()).filter(richtung -> richtung.istTasteGedrueckt(applet, spieler)).findFirst();
        }

        public final int x;
        public final int y;

        Richtung(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public abstract boolean istTasteGedrueckt(PApplet applet, Spiel.Spieler.Id spieler);

        public abstract String getTasteName(Spiel.Spieler.Id spieler);
    }
}
