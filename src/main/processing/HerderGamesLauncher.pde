import herdergames.HerderGames;

HerderGames herderGames = new HerderGames(this);

void settings() {
    herderGames.settings();
}

void setup() {
    herderGames.setup();
}

void draw() {
    herderGames.draw();
}

void mousePressed() {
    herderGames.mousePressed();
}

void mouseReleased() {
    herderGames.mouseReleased();
}

void mouseWheel(MouseEvent event) {
    herderGames.mouseWheel(event);
}

void keyPressed() {
   herderGames.keyPressed();
}

void keyReleased() {
   herderGames.keyReleased();
}