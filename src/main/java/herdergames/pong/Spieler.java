package herdergames.pong;

import processing.core.PApplet;

final class Spieler {
    int xPos;
    int yPos;
    int breite;
    int hoehe;

    Spieler(PApplet applet, int pxPos) {
        yPos = applet.height / 2;
        xPos = pxPos;
        breite = 50;
        hoehe = 200;
    }
}
