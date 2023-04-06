import processing.core.PApplet;
import processing.core.PConstants;

import java.util.*;

final class TitleScreen {
    private static final List<Spiel.Factory> SPIELE = List.of(
            Dame.SPIELER_GEGEN_SPIELER_FACTORY,
            Dame.SPIELER_GEGEN_AI_FACTORY,

            VierGewinnt.SPIELER_GEGEN_SPIELER_FACTORY,
            VierGewinnt.SPIELER_GEGEN_AI_FACTORY,

            TicTacToe.SPIELER_GEGEN_SPIELER_FACTORY,
            TicTacToe.SPIELER_GEGEN_AI_FACTORY,

            Schach.SPIELER_GEGEN_SPIELER_FACTORY,
            Schach.SPIELER_GEGEN_AI_FACTORY,

            FlappyOinky.FACTORY,

            Baelle.FACTORY
    );

    private final PApplet applet;
    private final Set<GameButton> buttons = new HashSet<>();
    private final Set<SpielerStatus> spielerStatusAnzeigen = new HashSet<>();
    private final Map<Spiel.Spieler.Id, Spiel.Spieler> alleSpieler = new HashMap<>();
    private final List<Spiel.Spieler.Id> aktivierteSpielerIds = new ArrayList<>();

    private boolean uiEnabled = false;
    private Spiel aktuellesSpiel;

    TitleScreen(PApplet applet) {
        this.applet = applet;

        int zeile = GameButton.ZEILEN - (SPIELE.size()/GameButton.SPALTEN) - 1;
        int spalte = 0;
        for (Spiel.Factory factory : SPIELE) {
            buttons.add(new GameButton(factory, zeile, spalte));
            spalte++;
            if (spalte >= GameButton.SPALTEN) {
                zeile++;
                spalte = 0;
            }
        }

        for (Spiel.Spieler.Id spielerId : Spiel.Spieler.Id.values()) {
            spielerStatusAnzeigen.add(new SpielerStatus(spielerId));
        }
    }

    void settings() {
        applet.size(800, 800);
        applet.fullScreen();
    }

    void setup() {
        Schach.init(applet);
        FlappyOinky.init(applet);
    }

    private void toggleUi() {
        if (applet.key != ' ') {
            return;
        }

        uiEnabled = !uiEnabled;
    }

    void keyPressed() {
        if (aktuellesSpiel != null) {
            aktuellesSpiel.keyPressed();
            return;
        }

        for (SpielerStatus spielerStatus : spielerStatusAnzeigen) {
            if (spielerStatus.keyPressed()) {
                return;
            }
        }

        toggleUi();
    }

    void keyReleased() {
        if (aktuellesSpiel != null) {
            aktuellesSpiel.keyReleased();
            return;
        }
    }

    void mousePressed() {
        if (aktuellesSpiel != null) {
            aktuellesSpiel.mousePressed();
            return;
        }

        for (GameButton button : buttons) {
            button.mousePressed();
        }

        for (SpielerStatus spielerStatus : spielerStatusAnzeigen) {
            spielerStatus.mousePressed();
        }
    }

    private void drawAktuellesSpiel() {
        if (aktuellesSpiel instanceof Spiel.Einzelspieler) {
            Spiel.Einzelspieler einzelspielerSpiel = (Spiel.Einzelspieler) aktuellesSpiel;
            Optional<Spiel.Einzelspieler.Ergebnis> ergebnis = einzelspielerSpiel.draw();
            if (ergebnis.isEmpty()) {
                return;
            }

            aktuellesSpiel = null;

            if (aktivierteSpielerIds.size() != 1) {
                throw new IllegalStateException();
            }
            Spiel.Spieler.Id spielerId = aktivierteSpielerIds.get(0);

            int punkte;
            switch (ergebnis.get()) {
                case GEWONNEN:
                    punkte = 1;
                    break;
                case UNENTSCHIEDEN:
                    punkte = 0;
                    break;
                case VERLOREN:
                    punkte = -1;
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            alleSpieler.put(spielerId, alleSpieler.get(spielerId).addPunkte(punkte));
        } else if (aktuellesSpiel instanceof Spiel.SpielerGegenSpieler) {
            Spiel.SpielerGegenSpieler spielerGegenSpielerSpiel = (Spiel.SpielerGegenSpieler) aktuellesSpiel;
            Optional<Optional<Spiel.Spieler.Id>> result = spielerGegenSpielerSpiel.draw();
            if (result.isEmpty()) {
                return;
            }

            aktuellesSpiel = null;

            Optional<Spiel.Spieler.Id> gewinnerId = result.get();
            if (gewinnerId.isEmpty()) {
                return;
            }
            Spiel.Spieler gewinnerSpieler = alleSpieler.get(gewinnerId.get());
            gewinnerSpieler = gewinnerSpieler.addPunkte(1);
            alleSpieler.put(gewinnerId.get(), gewinnerSpieler);
        } else {
            Spiel.Mehrspieler mehspielerSpiel = (Spiel.Mehrspieler) aktuellesSpiel;
            Optional<List<Spiel.Spieler.Id>> rangliste = mehspielerSpiel.draw();
            if (rangliste.isEmpty()) {
                return;
            }

            aktuellesSpiel = null;

            int punkte = rangliste.get().size() - 1;
            for (int i = 0; i < rangliste.get().size(); i++, punkte--) {
                Spiel.Spieler.Id spielerId = rangliste.get().get(i);
                Spiel.Spieler spieler = alleSpieler.get(spielerId);
                spieler = spieler.addPunkte(punkte);
                alleSpieler.put(spielerId, spieler);
            }
        }
    }

    void draw() {
        if (aktuellesSpiel != null) {
            applet.pushStyle();
            drawAktuellesSpiel();
            applet.popStyle();
            return;
        }

        applet.pushStyle();

        applet.background(255);

        for (GameButton button : buttons) {
            button.draw();
        }

        for (SpielerStatus spielerStatus : spielerStatusAnzeigen) {
            spielerStatus.draw();
        }

        applet.popStyle();
    }

    private final class GameButton {
        private static final int ZEILEN = 10;
        private static final int SPALTEN = 6;
        private static final float HEIGHT = 1f / ZEILEN;
        private static final float WIDTH = 1f / SPALTEN;
        private static final float INNER_HEIGHT = HEIGHT * (2f / 3f);
        private static final float INNER_WIDTH = WIDTH * (2f / 3f);
        private static final float MARGIN_X = (WIDTH - INNER_WIDTH) / 2;
        private static final float MARGIN_Y = (HEIGHT - INNER_HEIGHT) / 2;
        private static final float MAX_SPEED = 0.015f;

        private final Spiel.Factory factory;
        private final int zeile;
        private final int spalte;

        private float currentX;
        private float currentY;

        private GameButton(Spiel.Factory factory, int zeile, int spalte) {
            this.factory = factory;
            this.zeile = zeile;
            this.spalte = spalte;
            currentX = getOffScreenXPosition();
            currentY = getOffScreenYPosition();
        }

        private void move() {
            float distanceX = Math.abs(currentX - getTargetXPosition());
            float velocityX = Math.min(MAX_SPEED, distanceX);
            if (currentX < getTargetXPosition()) {
                currentX += velocityX;
            } else {
                currentX -= velocityX;
            }

            float distanceY = Math.abs(currentY - getTargetYPosition());
            float velocityY = Math.min(MAX_SPEED, distanceY);
            if (currentY < getTargetYPosition()) {
                currentY += velocityY;
            } else {
                currentY -= velocityY;
            }
        }

        private boolean isHovered() {
            float breite = INNER_WIDTH * applet.width;
            float hoehe = INNER_HEIGHT * applet.height;
            float xStart = currentX * applet.width;
            float xEnde = xStart + breite;
            float yStart = currentY * applet.height;
            float yEnde = yStart + hoehe;
            return applet.mouseX >= xStart && applet.mouseX <= xEnde && applet.mouseY >= yStart && applet.mouseY <= yEnde;
        }

        private void mousePressed() {
            if (!isHovered()) {
                return;
            }

            if (factory instanceof Spiel.Einzelspieler.Factory) {
                Spiel.Einzelspieler.Factory factory = (Spiel.Einzelspieler.Factory) this.factory;
                if (aktivierteSpielerIds.size() != 1) {
                    return;
                }
                Spiel.Spieler spieler = alleSpieler.get(aktivierteSpielerIds.get(0));
                aktuellesSpiel = factory.neuesSpiel(applet, spieler);
            } else if (factory instanceof Spiel.SpielerGegenSpieler.Factory) {
                Spiel.SpielerGegenSpieler.Factory factory = (Spiel.SpielerGegenSpieler.Factory) this.factory;
                if (aktivierteSpielerIds.size() != 2) {
                    return;
                }
                Spiel.Spieler spieler1 = alleSpieler.get(aktivierteSpielerIds.get(0));
                Spiel.Spieler spieler2 = alleSpieler.get(aktivierteSpielerIds.get(1));
                aktuellesSpiel = factory.neuesSpiel(applet, spieler1, spieler2);
            } else {
                Spiel.Mehrspieler.Factory factory = (Spiel.Mehrspieler.Factory) this.factory;
                if (aktivierteSpielerIds.isEmpty() || !factory.checkAnzahlSpieler(aktivierteSpielerIds.size())) {
                    return;
                }
                Set<Spiel.Spieler> aktivierteSpieler = new HashSet<>();
                for (Spiel.Spieler.Id aktivierterSpielerId : aktivierteSpielerIds) {
                    aktivierteSpieler.add(alleSpieler.get(aktivierterSpielerId));
                }
                aktuellesSpiel = factory.neuesSpiel(applet, aktivierteSpieler);
            }
        }

        private void draw() {
            move();

            applet.rectMode(PConstants.CORNER);
            applet.fill(getFillColor());
            applet.rect(
                    currentX * applet.width,
                    currentY * applet.height,
                    INNER_WIDTH * applet.width,
                    INNER_HEIGHT * applet.height
            );

            applet.textAlign(PConstants.CENTER, PConstants.CENTER);
            applet.fill(applet.color(255));
            applet.text(
                    factory.name,
                    (currentX + INNER_WIDTH/2) * applet.width,
                    (currentY + INNER_HEIGHT/2) * applet.height
            );
        }

        private float getOffScreenXPosition() {
            if (spalte < SPALTEN/2) {
                return -WIDTH;
            }
            return 1;
        }

        private float getTargetXPosition() {
            if (!uiEnabled) {
                return getOffScreenXPosition();
            }

            return spalte * WIDTH + MARGIN_X;
        }

        private float getOffScreenYPosition() {
            if (zeile < ZEILEN/2) {
                return -HEIGHT;
            }
            return 1;
        }

        private float getTargetYPosition() {
            if (!uiEnabled) {
                return getOffScreenYPosition();
            }

            return zeile * HEIGHT + MARGIN_Y;
        }

        private int getFillColor() {
            if (isHovered()) {
                return applet.color(0, 255, 0);
            }
            return applet.color(0);
        }
    }

    private final class SpielerStatus {
        private static final float HEIGHT = 0.02f;
        private static final float MARGIN = 0.02f;

        private final Spiel.Spieler.Id spielerId;

        private SpielerStatus(Spiel.Spieler.Id spielerId) {
            this.spielerId = spielerId;
        }

        private boolean isHovered() {
            float xStart = getXPosition() * applet.width;
            float yStart = getYPosition() * applet.height;
            float height = HEIGHT * applet.height;
            float width = getWidth();
            if (getTextAlignX() == PConstants.RIGHT) {
                xStart -= width;
            }
            float xEnd = xStart + width;
            float yEnd = yStart + height;

            return applet.mouseX >= xStart && applet.mouseX <= xEnd && applet.mouseY >= yStart && applet.mouseY <= yEnd;
        }

        private void draw() {
            if (!uiEnabled) {
                return;
            }

            float x = getXPosition() * applet.width;
            float y = getYPosition() * applet.height;

            applet.fill(getTextColor());
            applet.textSize(HEIGHT * applet.height);
            applet.textAlign(getTextAlignX(), PConstants.TOP);
            applet.text(getText(), x, y);
        }

        private void spielerHinzufuegenEntfernen() {
            if (applet.key != PConstants.ENTER) {
                return;
            }

            if (alleSpieler.containsKey(spielerId)) {
                alleSpieler.remove(spielerId);
                aktivierteSpielerIds.remove(spielerId);
            } else {
                alleSpieler.put(spielerId, new Spiel.Spieler(spielerId, spielerId.toString(), 0));
                aktivierteSpielerIds.add(spielerId);
            }
        }

        private void namenSchreiben() {
            if (applet.key == PConstants.CODED) {
                return;
            }
            if (applet.key == PConstants.TAB) {
                return;
            }
            if (applet.key == PConstants.ENTER) {
                return;
            }
            if (applet.key == PConstants.RETURN) {
                return;
            }
            if (applet.key == PConstants.BACKSPACE) {
                return;
            }

            if (!alleSpieler.containsKey(spielerId)) {
                return;
            }
            Spiel.Spieler spieler = alleSpieler.get(spielerId);

            String neuerName = spieler.name + applet.key;
            spieler = spieler.mitNamen(neuerName);

            alleSpieler.put(spielerId, spieler);
        }

        private void namenLoeschen() {
            if (applet.key != PConstants.BACKSPACE) {
                return;
            }

            if (!alleSpieler.containsKey(spielerId)) {
                return;
            }
            Spiel.Spieler spieler = alleSpieler.get(spielerId);

            if (spieler.name.isEmpty()) {
                return;
            }

            String neuerName = spieler.name.substring(0, spieler.name.length()-1);
            spieler = spieler.mitNamen(neuerName);
            alleSpieler.put(spielerId, spieler);
        }

        private boolean keyPressed() {
            if (!isHovered() || !uiEnabled) {
                return false;
            }

            spielerHinzufuegenEntfernen();
            namenSchreiben();
            namenLoeschen();

            return true;
        }

        private void spielerAktivierenDeaktivieren() {
            if (!alleSpieler.containsKey(spielerId)) {
                return;
            }

            if (aktivierteSpielerIds.contains(spielerId)) {
                aktivierteSpielerIds.remove(spielerId);
            } else {
                aktivierteSpielerIds.add(spielerId);
            }
        }

        private void mousePressed() {
            if (!isHovered() || !uiEnabled) {
                return;
            }

            spielerAktivierenDeaktivieren();
        }

        private int getTextAlignX() {
            switch (spielerId) {
                case SPIELER_1:
                case SPIELER_2:
                    return PConstants.LEFT;
                case SPIELER_3:
                case SPIELER_4:
                    return PConstants.RIGHT;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private float getXPosition() {
            switch (spielerId) {
                case SPIELER_1:
                case SPIELER_2:
                    return MARGIN;
                case SPIELER_3:
                case SPIELER_4:
                    return 1-MARGIN;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private float getYPosition() {
            switch (spielerId) {
                case SPIELER_1:
                case SPIELER_3:
                    return MARGIN;
                case SPIELER_2:
                case SPIELER_4:
                    return HEIGHT+MARGIN*2;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private String getText() {
            if (!alleSpieler.containsKey(spielerId)) {
                return "Keiner";
            }

            Spiel.Spieler spieler = TitleScreen.this.alleSpieler.get(spielerId);
            return spieler.name + ": " + spieler.punkte;
        }

        private int getTextColor() {
            if (isHovered()) {
                return applet.color(255, 0, 0);
            }
            if (aktivierteSpielerIds.contains(spielerId)) {
                return applet.color(0, 255, 0);
            }
            return applet.color(0);
        }

        private float getWidth() {
            applet.textSize(HEIGHT * applet.height);
            return applet.textWidth(getText());
        }
    }
}
