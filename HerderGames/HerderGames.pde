Screen currentScreen = new SpielScreen(this, new Dame.SpielSpielerGegenSpieler(this));

void setup() {
    fullScreen();
}

void draw() {
    currentScreen = currentScreen.draw();
}
