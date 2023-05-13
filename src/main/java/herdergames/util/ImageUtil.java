package herdergames.util;

import processing.core.PApplet;
import processing.core.PImage;

public final class ImageUtil {
    /**
     * Meine eigene Implementierung von Image Skalierung, die viel schneller ist als die von Processing
     */
    public static void imageVollbildZeichnen(PApplet applet, PImage image) {
        applet.loadPixels();
        image.loadPixels();

        for (int appletX = 0; appletX < applet.width; appletX++) {
            for (int appletY = 0; appletY < applet.height; appletY++) {
                float prozentX = (float) appletX / (float) applet.width;
                float prozentY = (float) appletY / (float) applet.height;
                int imageX = (int) (prozentX * (float) image.width);
                int imageY = (int) (prozentY * (float) image.height);
                int farbe = image.pixels[imageY*image.width + imageX];
                applet.pixels[appletY*applet.width + appletX] = farbe;
            }
        }

        applet.updatePixels();
    }
}
