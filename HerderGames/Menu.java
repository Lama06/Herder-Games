import processing.core.PApplet;
import processing.core.PConstants;

final class Menu {
    private final PApplet applet;
    private MiniSpiel currentSpiel = null;

    Menu(PApplet applet) {
        this.applet = applet;
    }

    void settings() {
        applet.size((int) applet.random(400, applet.displayWidth), (int) applet.random(400, applet.displayHeight));
        //applet.fullScreen();
    }

    void setup() {
        applet.windowResizable(true);
        FlappyOinky.init(applet);
        Schach.init(applet);
    }

    void draw() {
        if (currentSpiel == null) {
            applet.pushStyle();
            applet.textAlign(PApplet.CENTER);
            applet.textSize(30);
            applet.text("Spiel auswählen:", applet.width / 2, applet.height / 3);
            applet.text("1: Dame - Spieler gegen Spieler", applet.width / 2, applet.height / 3 + 50);
            applet.text("2: Dame - Spieler gegen AI", applet.width / 2, applet.height / 3 + 80);
            applet.text("3: Dame - AI gegen AI", applet.width / 2, applet.height / 3 + 110);
            applet.text("4: Tic Tac Toe - Spieler gegen Spieler", applet.width / 2, applet.height / 3 + 140);
            applet.text("5: Tic Tac Toe - Spieler gegen AI", applet.width / 2, applet.height / 3 + 170);
            applet.text("6: Vier Gewinnt - Spieler gegen Spieler", applet.width / 2, applet.height / 3 + 200);
            applet.text("7: Vier Gewinnt - Spieler gegen AI", applet.width / 2, applet.height / 3 + 230);
            applet.text("8: Flappy Oinky", applet.width / 2, applet.height / 3 + 260);
            applet.text("9: Schach Spieler gegen Spieler", applet.width / 2, applet.height / 3 + 290);
            applet.text("a: Schach gegen AI", applet.width / 2, applet.height / 3 + 320);
            applet.text("b: Bälle", applet.width / 2, applet.height / 3 + 350);
            applet.popStyle();

            if (applet.keyPressed) {
                switch (applet.key) {
                    case '1':
                        currentSpiel = new Dame.SpielerGegenSpielerSpiel(applet);
                        break;
                    case '2':
                        currentSpiel = new Dame.SpielerGegenAISpiel(applet);
                        break;
                    case '3':
                        currentSpiel = new Dame.AIGegenAISpiel(applet);
                        break;
                    case '4':
                        currentSpiel = new TicTacToe.SpielerGegenSpielerSpiel(applet);
                        break;
                    case '5':
                        currentSpiel = new TicTacToe.SpielerGegenAISpiel(applet);
                        break;
                    case '6':
                        currentSpiel = new VierGewinnt.SpielerGegenSpielerSpiel(applet);
                        break;
                    case '7':
                        currentSpiel = new VierGewinnt.SpielerGegenAISpiel(applet);
                        break;
                    case '8':
                        currentSpiel = new FlappyOinky(applet);
                        break;
                    case '9':
                        currentSpiel = new Schach.SpielerGegenSpielerSpiel(applet);
                        break;
                    case 'a':
                        currentSpiel = new Schach.SpielerGegenAISpiel(applet);
                        break;
                    case 'b':
                        currentSpiel = new Baelle(applet);
                        break;
                }
            }
        }

        if (currentSpiel != null) {
            applet.pushStyle();
            currentSpiel.draw();
            applet.popStyle();
        }
    }

    void mousePressed() {
        if (currentSpiel != null) {
            currentSpiel.mousePressed();
        }
    }

    void keyPressed() {
        if (currentSpiel != null) {
            currentSpiel.keyPressed();
        }
    }

    void keyReleased() {
        if (currentSpiel != null) {
            currentSpiel.keyReleased();
        }
    }
}
