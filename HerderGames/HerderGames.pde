MiniSpiel currentSpiel = null;

void settings() {
    size(int(random(400, displayWidth)), int(random(400, displayHeight)));
    fullScreen();
}

void setup() {
    FlappyOinky.init(this);
}

void draw() {
    if (currentSpiel == null) {
        pushStyle();
        textAlign(CENTER);
        textSize(30);
        text("Spiel auswählen:", width/2, height/3);
        text("1: Dame - Spieler gegen Spieler", width/2, height/3 + 50);
        text("2: Dame - Spieler gegen AI", width/2, height/3 + 80);
        text("3: Dame - AI gegen AI", width/2, height/3 + 110);
        text("4: Tic Tac Toe - Spieler gegen Spieler", width/2, height/3 + 140);
        text("5: Tic Tac Toe - Spieler gegen AI", width/2, height/3 + 170);
        text("6: Vier Gewinnt - Spieler gegen Spieler", width/2, height/3+200);
        text("7: Vier Gewinnt - Spieler gegen AI", width/2, height/3+230);
        text("8: Flappy Oinky", width/2, height/3+260);
        popStyle();

        if (keyPressed) {
            switch (key) {
                case '1':
                    currentSpiel = new Dame.SpielerGegenSpielerSpiel(this);
                    break;
                case '2':
                    currentSpiel = new Dame.SpielerGegenAISpiel(this);
                    break;
                case '3':
                    currentSpiel = new Dame.AIGegenAISpiel(this);
                    break;
                case '4':
                    currentSpiel = new TicTacToe.SpielerGegenSpielerSpiel(this);
                    break;
                case '5':
                    currentSpiel = new TicTacToe.SpielerGegenAISpiel(this);
                    break;
                case '6':
                    currentSpiel = new VierGewinnt.SpielerGegenSpielerSpiel(this);
                    break;
                case '7':
                    currentSpiel = new VierGewinnt.SpielerGegenAISpiel(this);
                    break;
                case '8':
                    currentSpiel = new FlappyOinky.Spiel(this);
                    break;
            }
        }

        return;
    }

    pushStyle();
    currentSpiel.draw();
    popStyle();
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