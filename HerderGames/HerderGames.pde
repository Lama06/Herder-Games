MiniSpiel currentSpiel = new Dame.SpielerGegenAISpiel(this);

void setup() {
    fullScreen();
}

void draw() {
    currentSpiel.draw();
}
