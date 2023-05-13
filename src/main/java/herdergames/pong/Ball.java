package herdergames.pong;

import processing.core.PApplet;

final class Ball {
    int xPos;
    int yPos;
    int durchmesser;
    float xSpeed;
    float ySpeed;

    Ball(PApplet applet) {
        xPos = applet.width / 2;
        yPos = applet.height / 2;
        xSpeed = 10;
        ySpeed = 4;
        durchmesser = 50;
    }
}
