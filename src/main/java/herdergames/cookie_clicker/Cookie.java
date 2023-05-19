package herdergames.cookie_clicker;

import processing.core.PApplet;

final class Cookie {
    float x;
    float y;
    float speed;
    float size;

    Cookie(PApplet applet) {
        y = 0;
        x = applet.random(0, applet.width / 2f);
        speed = applet.random(applet.height / 800f, applet.height / 200f);
        size = applet.random(applet.height / 80f, applet.height / 40f);
    }
}
