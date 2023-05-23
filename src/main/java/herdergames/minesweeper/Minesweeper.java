// 💣

package herdergames.minesweeper;

import herdergames.spiel.EinzelspielerSpiel;
import herdergames.spiel.Spieler;
import processing.core.PApplet;

import java.util.Optional;

import static processing.core.PConstants.*;

public final class Minesweeper extends EinzelspielerSpiel {
    boolean einfach;
    boolean mittel;
    boolean schwierig;

    boolean auswahlen = true;
    boolean ende = false;
    boolean gewonnen = true;
    boolean maus = true;
    boolean firstClick = false;

    int felder;
    int fieldSize;
    int minen;
    int minenNochDa;
    int fahnen = 0;
    int entschaerft = 0;

    boolean[][] fahne;
    boolean[][] mine;
    boolean[][] numberVisible;
    boolean[][] entschaerfteMinen;
    int[][] umliegendeMinen;

    public Minesweeper(PApplet applet, Spieler spieler) {
        super(applet);
    }

    @Override
    public Optional<EinzelspielerSpiel.Ergebnis> draw() {
        applet.background(255);

        applet.rectMode(CENTER);
        applet.textAlign(CENTER, CENTER);
        applet.textSize(applet.width/20f);
        applet.noStroke();

        // Startmenü bei Beginn des Spiels öffnen

        if (!einfach && !mittel && !schwierig) {
            Startmenue start = new Startmenue();
            start.draw();
        }

        // Einfacher Modus: 8×8, 10 Minen

        if (einfach && auswahlen && !ende) {
            felder = 8;
            fieldSize = applet.height/10;
            minen = 10;
            mine = new boolean[felder][felder];
            numberVisible = new boolean[felder][felder];
            fahne = new boolean[felder][felder];
            entschaerfteMinen = new boolean[felder][felder];

            Mode easy = new Mode();
            easy.draw();
            auswahlen = false;
        }

        // Mittlerer Modus: 16×16, 40 Minen

        if (mittel && auswahlen && !ende) {
            felder = 16;
            fieldSize = applet.height/18;
            minen = 40;
            mine = new boolean[felder][felder];
            numberVisible = new boolean[felder][felder];
            fahne = new boolean[felder][felder];
            entschaerfteMinen = new boolean[felder][felder];

            Mode medium = new Mode();
            medium.draw();
            auswahlen = false;
        }

        // Schwieriger Modus: 24×24, 100 Minen

        if (schwierig && auswahlen && !ende) {
            felder = 24;
            fieldSize = applet.height/26;
            minen = 100;
            mine = new boolean[felder][felder];
            numberVisible = new boolean[felder][felder];
            fahne = new boolean[felder][felder];
            entschaerfteMinen = new boolean[felder][felder];

            Mode hard = new Mode();
            hard.draw();
            auswahlen = false;
        }

        // Class für wirkliches Spiel öffnen

        if (einfach || mittel || schwierig && !ende) {
            Spiel play = new Spiel();
            play.draw();
        }

        // Ergebnis öffnen bei Ende des Spiels

        if (ende) {
            if (gewonnen) {
                return Optional.of(EinzelspielerSpiel.Ergebnis.GEWONNEN);
            } else {
                return Optional.of(EinzelspielerSpiel.Ergebnis.VERLOREN);
            }
        }

        return Optional.empty();
    }

    @Override
    public void mousePressed() {
        maus = true;
    }

    class Startmenue {
        void draw() {
            // Auswahlfelder für Startmenü zeichnen und beschriften

            applet.fill(200);
            applet.rect(applet.width/2f, 1*(applet.height/5f), applet.width/2f, applet.height/5f);
            applet.rect(applet.width/2f, 2.5f*(applet.height/5f), applet.width/2f, applet.height/5f);
            applet.rect(applet.width/2f, 4f*(applet.height/5f), applet.width/2f, applet.height/5f);

            applet.fill(255);
            applet.text("EINFACH - 8×8", applet.width/2f, 1*(applet.height/5f));
            applet.text("MITTEL - 16×16", applet.width/2f, 2.5f*(applet.height/5f));
            applet.text("SCHWIERIG - 24×24", applet.width/2f, 4f*(applet.height/5f));

            // Kollisionserkennung für Mausklick auf den Auswahlfeldern für Spielmodi

            if (applet.mouseX <= applet.width - applet.width/4 && applet.mouseX >= applet.width/4) {
                if (applet.mouseY >= 1*(applet.height/10f) && applet.mouseY <= 3*(applet.height/10)) {

                    applet.fill(150);
                    applet.rect(applet.width/2f, 1*(applet.height/5f), applet.width/2f, applet.height/5f);
                    applet.fill(255);
                    applet.text("EINFACH - 8×8", applet.width/2f, 1*(applet.height/5f));

                    if (applet.mousePressed && (applet.mouseButton == LEFT)) {
                        einfach = true;
                    }
                }

                if (applet.mouseY >= 4*(applet.height/10) && applet.mouseY <= 6*(applet.height/10)) {

                    applet.fill(150);
                    applet.rect(applet.width/2f, 2.5f*(applet.height/5f), applet.width/2f, applet.height/5f);
                    applet.fill(255);
                    applet.text("MITTEL - 16×16", applet.width/2f, 2.5f*(applet.height/5f));

                    if (applet.mousePressed && (applet.mouseButton == LEFT)) {
                        mittel = true;
                    }
                }

                if (applet.mouseY >= 7*(applet.height/10) && applet.mouseY <= 9*(applet.height/10)) {

                    applet.fill(150);
                    applet.rect(applet.width/2f, 4*(applet.height/5f), applet.width/2f, applet.height/5f);
                    applet.fill(255);
                    applet.text("SCHWIERIG - 24×24", applet.width/2f, 4*(applet.height/5f));

                    if (applet.mousePressed && (applet.mouseButton == LEFT)) {
                        schwierig = true;
                    }
                }
            }
        }
    }

    class Mode {
        int x;
        int y;

        Mode() {
            umliegendeMinen = new int[felder][felder];

            // Minenfelder zuweisen, wenn Feld belegt, dann neues Feld

            for (int i = 0; i < minen; i++) {
                x = (int)applet.random(felder);
                y = (int)applet.random(felder);

                if (!mine[x][y]) {
                    mine[x][y] = true;
                } else {
                    i--;
                }
            }

            // Umliegende Minen für jedes Feld berechnen

            for (int i = 0; i < felder; i++) {
                for (int j = 0; j < felder; j++) {

                    if (j >= 1 && mine[i][j-1]) {
                        umliegendeMinen[i][j]++;
                    }
                    if (i >= 1 && mine[i-1][j]) {
                        umliegendeMinen[i][j]++;
                    }
                    if (j <= felder-2 && mine[i][j+1]) {
                        umliegendeMinen[i][j]++;
                    }
                    if (i <= felder-2 && mine[i+1][j]) {
                        umliegendeMinen[i][j]++;
                    }
                    if (j >= 1 && i <= felder-2 && mine[i+1][j-1]) {
                        umliegendeMinen[i][j]++;
                    }
                    if (i >= 1 && j <= felder-2 && mine[i-1][j+1]) {
                        umliegendeMinen[i][j]++;
                    }
                    if (j <= felder-2 && i <= felder-2 && mine[i+1][j+1]) {
                        umliegendeMinen[i][j]++;
                    }
                    if (i >= 1 && j >= 1 && mine[i-1][j-1]) {
                        umliegendeMinen[i][j]++;
                    }
                }
            }
            maus = false;
            firstClick = true;
            minenNochDa = minen;
        }

        void draw() {
        }
    }


    class Spiel {
        Spiel() {
        }

        void draw() {
            applet.background(255);

            for (int i = 0; i < felder; i++) {
                for (int j = 0; j < felder; j++) {

                    // Felder je nach Größe zeichnen
                    applet.stroke(150);
                    applet.strokeWeight(fieldSize/10f);
                    if (numberVisible[i][j]) {
                        // Wenn ein Feld ohne Mine angeklickt wurde ändert sich die Farbe
                        applet.fill(150);
                    } else {
                        applet.fill(200);
                    }
                    // Feld wird gezeichnet
                    applet.rect(applet.width/2f - fieldSize*(felder/2f) + 0.5f*fieldSize + i*fieldSize, j*fieldSize + 1.5f*fieldSize, fieldSize, fieldSize);


                    // Wenn sich Maus auf beliebigen Feld befindet (Kollisionsabfrage)
                    if (applet.mouseX > applet.width/2 - fieldSize*(felder/2) + i*fieldSize &&
                            applet.mouseX < applet.width/2 - fieldSize*(felder/2) + i*fieldSize + fieldSize &&
                            applet.mouseY > j*fieldSize + fieldSize &&
                            applet.mouseY < j*fieldSize + 2*fieldSize
                    ) {

                        // Klick mit linker Maustaste

                        if (applet.mousePressed && applet.mouseButton == LEFT && maus && !fahne[i][j]) {
                            if (mine[i][j]) {

                                if (firstClick) {
                                    // Falls beim ersten Klicken eine Mine getroffen wird

                                    fahne[i][j] = !fahne[i][j];
                                    // Wird nur in diesem Fall eine Markierungsfahne gesetzt und der Counter für verbliebene Markierungen geändert

                                    if (fahne[i][j]) {
                                        minenNochDa--;
                                        fahnen++;
                                    } else if (!fahne[i][j]) {
                                        minenNochDa++;
                                        fahnen--;
                                    }
                                    firstClick = false;
                                } else {
                                    // Falls sonst eine Mine getroffen wird, Spiel beenden

                                    gewonnen = false;
                                    ende = true;
                                }

                            } else {
                                numberVisible[i][j] = true;
                                // Feld wird sichtbar

                                if (umliegendeMinen[i][j] == 0) {
                                    // Wenn es keine umliegenden Minen gibt, werden direkt umliegende Felder auch sichtbar

                                    if (j >= 1) {
                                        numberVisible[i][j-1] = true;
                                    }
                                    if (j <= felder-2) {
                                        numberVisible[i][j+1] = true;
                                    }
                                    if (i >= 1) {
                                        numberVisible[i-1][j] = true;
                                    }
                                    if (i <= felder-2) {
                                        numberVisible[i+1][j] = true;
                                    }
                                    if (i >= 1 && j >= 1) {
                                        numberVisible[i-1][j-1] = true;
                                    }
                                    if (i >= 1 && j <= felder-2) {
                                        numberVisible[i-1][j+1] = true;
                                    }
                                    if (i <= felder-2 && j >= 1) {
                                        numberVisible[i+1][j-1] = true;
                                    }
                                    if (i <= felder-2 && j <= felder-2) {
                                        numberVisible[i+1][j+1] = true;
                                    }
                                }

                                firstClick = false;
                            }
                            maus = false;
                        }

                        // Klick mit rechter Maustaste

                        if (applet.mousePressed && applet.mouseButton == RIGHT && !numberVisible[i][j] && maus) {
                            firstClick = false;
                            fahne[i][j] = !fahne[i][j];
                            maus = false;

                            // Counter für verbliebene Markierungen ändern

                            if (fahne[i][j]) {
                                minenNochDa--;
                                fahnen++;
                            } else if (!fahne[i][j]) {
                                minenNochDa++;
                                fahnen--;
                            }
                        }
                    }

                    // Umliegende Minenanzahl anzeigen

                    if (!mine[i][j] && numberVisible[i][j] && !fahne[i][j] && umliegendeMinen[i][j] > 0) {
                        applet.textSize(fieldSize/1.5f);

                        // Farbe je nach Anzahl auwählen

                        if (umliegendeMinen[i][j] == 1) {
                            applet.fill(0, 0, 220);
                        } else if (umliegendeMinen[i][j] == 2) {
                            applet.fill(0, 120, 0);
                        } else if (umliegendeMinen[i][j] == 3) {
                            applet.fill(255, 0, 0);
                        } else if (umliegendeMinen[i][j] == 4) {
                            applet.fill(0, 0, 80);
                        } else if (umliegendeMinen[i][j] == 5) {
                            applet.fill(150, 40, 0);
                        } else if (umliegendeMinen[i][j] == 6) {
                            applet.fill(0, 150, 150);
                        } else if (umliegendeMinen[i][j] == 7) {
                            applet.fill(0, 0, 0);
                        } else if (umliegendeMinen[i][j] == 8) {
                            applet.fill(100, 100, 100);
                        }
                        applet.text(umliegendeMinen[i][j], applet.width/2f - fieldSize*(felder/2f) + 0.5f*fieldSize + i*fieldSize, j*fieldSize + 1.5f*fieldSize);
                    }

                    // Markierungsfahnen anzeigen

                    if (fahne[i][j]) {
                        applet.stroke(0);
                        applet.strokeWeight(fieldSize/15f);
                        applet.strokeCap(SQUARE);
                        applet.line(
                                applet.width/2f - fieldSize*(felder/2f) + 0.4f*fieldSize + i*fieldSize,
                                j*fieldSize + 1.15f*fieldSize,
                                applet.width/2f - fieldSize*(felder/2f) + 0.4f*fieldSize + i*fieldSize,
                                j*fieldSize + 1.85f*fieldSize
                        );
                        applet.strokeCap(ROUND);
                        applet.noStroke();
                        applet.fill(255, 0, 0);
                        applet.triangle(
                                applet.width/2f - fieldSize*(felder/2f) + 0.43f*fieldSize + i*fieldSize,
                                j*fieldSize + 1.2f*fieldSize,
                                applet.width/2f - fieldSize*(felder/2f) + 0.43f*fieldSize + i*fieldSize,
                                j*fieldSize + 1.5f*fieldSize,
                                applet.width/2f - fieldSize*(felder/2f) + 0.8f*fieldSize + i*fieldSize,
                                j*fieldSize + 1.35f*fieldSize
                        );
                    }

                    // Wenn Fahne auf Minenfeld gesetzt wurde, entschärfte Minen hochzählen

                    if (mine[i][j] && fahne[i][j] && !entschaerfteMinen[i][j]) {
                        entschaerfteMinen[i][j] = true;
                        entschaerft++;
                    }

                    // Wenn so viele Fahnen wie Minen gesetzt wurden und alle Minen "entschärft" wurden
                    if (fahnen == minen && entschaerft == minen) {
                        gewonnen = true;
                        ende = true;
                    }
                }
            }


            // Übrige Minenanzahl oben ausgeben

            applet.textSize(fieldSize/2f);
            applet.fill(0);
            if (minenNochDa > 0) {
                applet.text("Minen: " + minen + "   Fehlende Markierungen: " + minenNochDa, applet.width/2f, 0.5f*fieldSize);
            } else if (minenNochDa == 0) {
                applet.text("Minen: " + minen + "   Da scheint etwas falsch zu sein", applet.width/2f, 0.5f*fieldSize);
            } else {
                applet.text("Minen: " + minen + "   " + (minenNochDa*(-1)) + " Markierungen zu viel", applet.width/2f, 0.5f*fieldSize);
            }
        }
    }
}
