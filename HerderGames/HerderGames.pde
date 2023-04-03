MiniSpiel currentSpiel = null;

void setup() {
    size(700, 800);
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
            }
        }

        return;
    }

    currentSpiel.draw();
}
