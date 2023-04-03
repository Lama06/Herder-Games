MiniSpiel currentSpiel = null;

void setup() {
    fullScreen();
}

void settings() {
    size(int(random(400, 100)), int(random(400, 1000)));
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
            }
        }

        return;
    }

    pushStyle();
    currentSpiel.draw();
    popStyle();
}
