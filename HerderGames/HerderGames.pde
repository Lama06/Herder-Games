MiniSpiel currentSpiel = new Dame.SpielSpielerGegenSpieler(this);

void setup() {
    fullScreen();
}

void draw() {
    currentSpiel.draw();
}
