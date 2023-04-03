MiniSpiel currentSpiel = new Dame.SpielerGegenSpielerSpiel(this);

void setup() {
    size(700, 800);
}

void draw() {
    currentSpiel.draw();
}
