package herdergames.schiffe_versenken;

import processing.core.PApplet;
import processing.core.PConstants;

import java.util.*;

record SpielBrett(List<List<Feld>> zeilen) {
    static final int SIZE = 10;

    private static final Map<Integer, Integer> SCHIFFE_LAENGEN = Map.of(
            1, 1,
            2, 2,
            3, 3,
            4, 2,
            5, 1
    );

    private static Map<Integer, Integer> getSchiffeLaengenInFeldern(Iterable<Feld> felder) {
        Map<Integer, Integer> schiffe = new HashMap<>();

        int aktuelleLaenge = 0;
        for (Feld feld : felder) {
            if (feld instanceof Feld.Schiff || feld instanceof Feld.Getroffen) {
                aktuelleLaenge++;
            } else if (aktuelleLaenge > 0) {
                if (aktuelleLaenge > 1) {
                    int akutelleAnzahl = schiffe.getOrDefault(aktuelleLaenge, 0);
                    schiffe.put(aktuelleLaenge, akutelleAnzahl + 1);
                }
                aktuelleLaenge = 0;
            }
        }
        if (aktuelleLaenge > 1) {
            int akutelleAnzahl = schiffe.getOrDefault(aktuelleLaenge, 0);
            schiffe.put(aktuelleLaenge, akutelleAnzahl + 1);
        }

        return schiffe;
    }

    private static Map<Integer, Integer> getSchiffeLaengenInZeile(List<List<Feld>> zeilen, int zeile) {
        return getSchiffeLaengenInFeldern(zeilen.get(zeile));
    }

    private static Map<Integer, Integer> getSchiffeLaengenInZeilen(List<List<Feld>> zeilen) {
        Map<Integer, Integer> schiffe = new HashMap<>();
        for (int zeile = 0; zeile < SIZE; zeile++) {
            getSchiffeLaengenInZeile(zeilen, zeile).forEach((laenge, anzahl) -> {
                int aktuelleAnzahl = schiffe.getOrDefault(laenge, 0);
                schiffe.put(laenge, aktuelleAnzahl + anzahl);
            });
        }
        return schiffe;
    }

    private static Map<Integer, Integer> getSchiffeLaengenInSpalte(List<List<Feld>> zeilen, int spalte) {
        List<Feld> felder = new ArrayList<>();
        for (List<Feld> zeile : zeilen) {
            felder.add(zeile.get(spalte));
        }
        return getSchiffeLaengenInFeldern(felder);
    }

    private static Map<Integer, Integer> getSchiffeLaengenInSpalten(List<List<Feld>> zeilen) {
        Map<Integer, Integer> schiffe = new HashMap<>();
        for (int spalte = 0; spalte < SIZE; spalte++) {
            getSchiffeLaengenInSpalte(zeilen, spalte).forEach((laenge, anzahl) -> {
                int aktuelleAnzahl = schiffe.getOrDefault(laenge, 0);
                schiffe.put(laenge, aktuelleAnzahl + anzahl);
            });
        }
        return schiffe;
    }

    private static int getAnzahlEinerSchiffe(List<List<Feld>> zeilen) {
        int anzahl = 0;

        for (int zeile = 0; zeile < SIZE; zeile++) {
            spalte:
            for (int spalte = 0; spalte < SIZE; spalte++) {
                Feld feld = zeilen.get(zeile).get(spalte);
                if (!(feld instanceof Feld.Schiff) && !(feld instanceof Feld.Getroffen)) {
                    continue;
                }

                Position position = new Position(zeile, spalte);
                for (Position nachbar : position.getDirekteNachbarn()) {
                    Feld nachbarFeld = zeilen.get(nachbar.zeile()).get(nachbar.spalte());
                    if (nachbarFeld instanceof Feld.Schiff || nachbarFeld instanceof Feld.Getroffen) {
                        continue spalte;
                    }
                }

                anzahl++;
            }
        }

        return anzahl;
    }

    private static Map<Integer, Integer> getSchiffeLaengen(List<List<Feld>> zeilen) {
        Map<Integer, Integer> schiffe = new HashMap<>();
        getSchiffeLaengenInZeilen(zeilen).forEach((laenge, anzahl) -> {
            int aktuelleAnzahl = schiffe.getOrDefault(laenge, 0);
            schiffe.put(laenge, aktuelleAnzahl + anzahl);
        });
        getSchiffeLaengenInSpalten(zeilen).forEach((laenge, anzahl) -> {
            int aktuelleAnzahl = schiffe.getOrDefault(laenge, 0);
            schiffe.put(laenge, aktuelleAnzahl + anzahl);
        });
        schiffe.put(1, getAnzahlEinerSchiffe(zeilen));
        return schiffe;
    }

    static boolean sindSchiffeLegal(List<List<Feld>> zeilen) {
        return getSchiffeLaengen(zeilen).equals(SCHIFFE_LAENGEN);
    }

    SpielBrett(List<List<Feld>> zeilen) {
        if (zeilen.size() != SIZE) {
            throw new IllegalArgumentException();
        }

        for (List<Feld> zeile : zeilen) {
            if (zeile.size() != SIZE) {
                throw new IllegalArgumentException();
            }
        }

        if (!sindSchiffeLegal(zeilen)) {
            throw new IllegalArgumentException();
        }

        List<List<Feld>> zeilenKopien = new ArrayList<>();
        for (List<Feld> zeile : zeilen) {
            zeilenKopien.add(List.copyOf(zeile));
        }
        this.zeilen = Collections.unmodifiableList(zeilenKopien);
    }

    Feld getFeld(Position position) {
        return zeilen.get(position.zeile()).get(position.spalte());
    }

    SpielBrett mitFeld(Position position, Feld neuesFeld) {
        List<List<Feld>> neueZeilen = new ArrayList<>();
        for (int zeile = 0; zeile < SIZE; zeile++) {
            if (position.zeile() == zeile) {
                List<Feld> neueZeile = new ArrayList<>(zeilen.get(zeile));
                neueZeile.set(position.spalte(), neuesFeld);
                neueZeilen.add(neueZeile);
                continue;
            }

            neueZeilen.add(zeilen.get(zeile));
        }
        return new SpielBrett(neueZeilen);
    }

    void draw(PApplet applet, float xStart, float yStart, float feldSize, boolean ausSichtDesGegners) {
        for (int zeile = 0; zeile < SIZE; zeile++) {
            for (int spalte = 0; spalte < SIZE; spalte++) {
                float x = xStart + feldSize * spalte;
                float y = yStart + feldSize * zeile;

                Feld feld = getFeld(new Position(zeile, spalte));
                feld.draw(applet, x, y, feldSize, ausSichtDesGegners);
            }
        }
    }

    record MausPressedErgebnis(SpielBrett neuesBrett, boolean zugBeendet) { }

    MausPressedErgebnis handleGegnerMausPressed(PApplet applet, float xOffset, float yOffset, float feldSize) {
        Optional<Position> mausPosition = Position.fromMausPosition(xOffset, yOffset, feldSize);
        if (mausPosition.isEmpty()) {
            return new MausPressedErgebnis(this, false);
        }
        Feld feld = getFeld(mausPosition.get());
        if (feld instanceof Feld.Leer leer) {
            if (applet.mouseButton == PConstants.RIGHT) {
                return new MausPressedErgebnis(mitFeld(mausPosition.get(), leer.markierungWechseln()), false);
            } else {
                return new MausPressedErgebnis(mitFeld(mausPosition.get(), new Feld.Leer(true)), true);
            }
        } else if (feld instanceof Feld.Schiff schiff) {
            if (applet.mouseButton == PConstants.RIGHT) {
                return new MausPressedErgebnis(mitFeld(mausPosition.get(), schiff.markierungWechseln()), false);
            } else {
                return new MausPressedErgebnis(mitFeld(mausPosition.get(), Feld.Getroffen.INSTANCE), false);
            }
        } else if (feld instanceof Feld.Getroffen) {
            return new MausPressedErgebnis(this, false);
        } else {
            throw new IllegalStateException();
        }
    }

    boolean hatVerloren() {
        for (int zeile = 0; zeile < SIZE; zeile++) {
            for (int spalte = 0; spalte < SIZE; spalte++) {
                if (getFeld(new Position(zeile, spalte)) instanceof Feld.Schiff) {
                    return false;
                }
            }
        }

        return true;
    }
}
