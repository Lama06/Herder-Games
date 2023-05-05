package herdergames.tetris;

import herdergames.spiel.Spiel;
import herdergames.spiel.Spieler;
import herdergames.util.Steuerung;
import processing.core.PApplet;
import processing.core.PConstants;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// In diesem Spiel habe ichs ein bischen mit funktionaler Programmierung übertrieben

public final class Tetris extends Spiel.Mehrspieler {
    private final List<SpielBrett> spielBretter = new ArrayList<>();
    private final List<Spieler.Id> rangliste = new ArrayList<>();

    public Tetris(PApplet applet, Set<Spieler> alleSpieler) {
        super(applet);
        List<Spieler> spielerSortiert = new ArrayList<>(alleSpieler);
        spielerSortiert.sort(Comparator.comparing(Spieler::id));
        spielerSortiert.stream().map(SpielBrett::new).forEach(spielBretter::add);
    }

    @Override
    public Optional<List<Spieler.Id>> draw() {
        applet.background(applet.color(0));

        for (int i = 0; i < spielBretter.size(); i++) {
            spielBretter.get(i).draw(i);
        }

        spielBretter.stream().filter(SpielBrett::hatVerloren).forEach(brett -> rangliste.add(0, brett.spieler.id()));
        spielBretter.removeIf(SpielBrett::hatVerloren);
        if (spielBretter.isEmpty()) {
            return Optional.of(rangliste);
        }

        return Optional.empty();
    }

    @Override
    public void keyPressed() {
        spielBretter.forEach(SpielBrett::keyPressed);
    }

    private float getKastenSize() {
        return Math.min(
                (float) applet.width / (SpielBrett.BREITE * spielBretter.size()),
                (float) applet.height / SpielBrett.HOEHE
        );
    }

    private float getSpielBrettBreite() {
        return getKastenSize() * SpielBrett.BREITE;
    }

    private float getSpielBrettHoehe() {
        return getKastenSize() * SpielBrett.HOEHE;
    }

    private float getBreiteGesamt() {
        return getSpielBrettBreite() * spielBretter.size();
    }

    private float getHoeheGesamt() {
        return getSpielBrettHoehe();
    }

    private float getAbstandHorizontal() {
        return (applet.width - getBreiteGesamt()) / 2;
    }

    private float getAbstandVertikal() {
        return (applet.height - getHoeheGesamt()) / 2;
    }

    private final class SpielBrett {
        private static final int HOEHE = 20;
        private static final int BREITE = 10;

        private final Spieler spieler;
        private FallendeForm fallendeForm = new FallendeForm();
        private final Set<GefallenesElement> gefalleneElemente = new HashSet<>();

        private SpielBrett(Spieler spieler) {
            this.spieler = spieler;
        }

        private float getScreenX(int index) {
            return getAbstandHorizontal() + getSpielBrettBreite() * index;
        }

        private float getScreenY() {
            return getAbstandVertikal();
        }

        private void drawBackground(int index) {
            float breite = getSpielBrettBreite();
            float hoehe = getSpielBrettHoehe();
            applet.stroke(0);
            applet.strokeWeight(3);
            applet.fill(255);
            applet.rectMode(PConstants.CORNER);
            applet.rect(getScreenX(index), getScreenY(), breite, hoehe);
        }

        private void draw(int index) {
            drawBackground(index);

            fallendeForm.draw(index);
            gefalleneElemente.forEach(element -> element.draw(index));

            if (fallendeForm.amBoden()) {
                fallendeForm.zuGefallenenElementenKonvertieren();
                fallendeForm = new FallendeForm();
            }

            volleZeilenEntfernen();
        }

        private void keyPressed() {
            fallendeForm.keyPressed();
        }

        private boolean istZeileVoll(int zeile) {
            return IntStream.range(0, BREITE).allMatch(spalte -> getGefallenesElement(spalte, zeile).isPresent());
        }

        private void volleZeileEntfernen(int zeile) {
            gefalleneElemente.removeIf(element -> element.y == zeile);

            // Zeilen darüber eins nach unten schieben
            for (int zeileDrueber = zeile-1; zeileDrueber >= 0; zeileDrueber--) {
                getGefalleneElementeInZeile(zeileDrueber).forEach(element -> element.y++);
            }
        }

        private void volleZeilenEntfernen() {
            for (int zeile = 0; zeile < HOEHE; zeile++) {
                if (istZeileVoll(zeile)) {
                    volleZeileEntfernen(zeile);
                }
            }
        }

        private Optional<GefallenesElement> getGefallenesElement(int x, int y) {
            return gefalleneElemente.stream().filter(element -> element.x == x && element.y == y).findAny();
        }

        private Stream<GefallenesElement> getGefalleneElementeInZeile(int zeile) {
            return gefalleneElemente.stream().filter(element -> element.y == zeile);
        }

        private boolean hatVerloren() {
            return gefalleneElemente.stream().anyMatch(GefallenesElement::ueberBrett);
        }

        private final class FallendeForm {
            private static final int MOVE_DELAY = 15;

            private int x = BREITE/2;
            private int y = 0;
            private int nextMove = MOVE_DELAY;
            private final int farbe = applet.color((int) applet.random(255), (int) applet.random(255), (int) applet.random(255));
            private final Set<Element> elemente;

            private FallendeForm() {
                List<Supplier<Set<Element>>> formen = List.of(
                        // Quadrat
                        () -> Set.of(
                                new Element(0, 0),
                                new Element(1, 0),
                                new Element(0, 1),
                                new Element(1, 1)
                        ),
                        // Stange (Keine Ahnung wie das heißt)
                        () -> Set.of(
                                new Element(0, 0),
                                new Element(0, 1),
                                new Element(0, 2),
                                new Element(0, 3)
                        ),
                        // L
                        () -> Set.of(
                                new Element(0, 0),
                                new Element(0, 1),
                                new Element(0, 2),
                                new Element(1, 2)
                        ),
                        () -> Set.of(
                                new Element(-1, 0),
                                new Element(0, 0),
                                new Element(1, 0),
                                new Element(0, -1)
                        )
                );

                this.elemente = formen.get(applet.choice(formen.size())).get();
            }

            private boolean amBoden() {
                return elemente.stream().anyMatch(Element::amBoden);
            }

            private void zuGefallenenElementenKonvertieren() {
                elemente.forEach(Element::zuGefallenemElementKonvertieren);
            }

            private void move() {
                if (nextMove > 0) {
                    nextMove--;
                    return;
                }
                y++;
                nextMove = MOVE_DELAY;
            }

            private void draw(int index) {
                move();
                elemente.forEach(element -> element.draw(index));
            }

            private int getKleinstesX() {
                return elemente.stream().mapToInt(Element::getAbsoluteXPosition).min().orElseThrow(IllegalStateException::new);
            }

            private int getGroesstesX() {
                return elemente.stream().mapToInt(Element::getAbsoluteXPosition).max().orElseThrow(IllegalStateException::new);
            }

            private void drehen() {
                elemente.forEach(Element::drehen);

                // Durch das drehen ist ein Teil der Form teilweise außerhalb des Bilschirms
                // Dann die Form wieder reinrücken
                if (getKleinstesX() < 0) {
                    x -= getKleinstesX();
                }
                if (getGroesstesX() >= BREITE) {
                    x -= getGroesstesX() - (BREITE-1);
                }
            }

            private void keyPressed() {
                if (Steuerung.Richtung.LINKS.istTasteGedrueckt(applet, spieler.id()) && getKleinstesX() != 0) {
                    x--;
                } else if (Steuerung.Richtung.RECHTS.istTasteGedrueckt(applet, spieler.id()) && getGroesstesX() != BREITE-1) {
                    x++;
                } else if (Steuerung.Richtung.UNTEN.istTasteGedrueckt(applet, spieler.id())) {
                    y++;
                } else if (Steuerung.Richtung.OBEN.istTasteGedrueckt(applet, spieler.id())) {
                    drehen();
                }
            }

            private final class Element {
                private int relativeXPosition;
                private int relativeYPosition;

                private Element(int relativeXPosition, int relativeYPosition) {
                    this.relativeXPosition = relativeXPosition;
                    this.relativeYPosition = relativeYPosition;
                }

                private int getAbsoluteXPosition() {
                    return x + relativeXPosition;
                }

                private int getAbsoluteYPosition() {
                    return y + relativeYPosition;
                }

                private boolean amBoden() {
                    if (getAbsoluteYPosition() == HOEHE-1) {
                        return true;
                    }

                    return gefalleneElemente.stream().anyMatch(element -> element.x == getAbsoluteXPosition() && element.y == getAbsoluteYPosition()+1);
                }

                private void zuGefallenemElementKonvertieren() {
                    gefalleneElemente.add(new GefallenesElement(getAbsoluteXPosition(), getAbsoluteYPosition(), farbe));
                }

                @SuppressWarnings("SuspiciousNameCombination") // Mein Code ist zu Schlau für Intellij Idea
                private void drehen() {
                    int alteRealtiveXPosition = relativeXPosition;
                    int alteRelativeYPosition = relativeYPosition;
                    relativeXPosition = -alteRelativeYPosition;
                    relativeYPosition = alteRealtiveXPosition;
                }

                private void draw(int index) {
                    float brettX = SpielBrett.this.getScreenX(index);
                    float brettY = SpielBrett.this.getScreenY();
                    float kastenSize = getKastenSize();

                    float screenX = brettX + getAbsoluteXPosition() * kastenSize;
                    float screenY = brettY + getAbsoluteYPosition() * kastenSize;

                    applet.rectMode(PConstants.CENTER);
                    applet.strokeWeight(1);
                    applet.stroke(0);
                    applet.fill(farbe);
                    applet.rectMode(PConstants.CORNER);
                    applet.rect(screenX, screenY, kastenSize, kastenSize);
                }
            }
        }

        private final class GefallenesElement {
            private final int x;
            private int y;
            private final int farbe;

            private GefallenesElement(int x, int y, int farbe) {
                this.x = x;
                this.y = y;
                this.farbe = farbe;
            }

            private void draw(int index) {
                float brettX = getScreenX(index);
                float brettY = getScreenY();
                float kastenSize = getKastenSize();

                float screenX = brettX + x * kastenSize;
                float screenY = brettY + y * kastenSize;

                applet.strokeWeight(1);
                applet.stroke(0);
                applet.fill(farbe);
                applet.rectMode(PConstants.CORNER);
                applet.rect(screenX, screenY, kastenSize, kastenSize);
            }

            private boolean ueberBrett() {
                return y < 0;
            }
        }
    }
}
