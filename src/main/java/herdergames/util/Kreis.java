package herdergames.util;

import processing.core.PApplet;

public record Kreis(float mittelpunktX, float mittelpunktY, float radius) {
    public boolean kollidiertMit(Kreis kreis) {
        return PApplet.dist(mittelpunktX, mittelpunktY, kreis.mittelpunktX, kreis.mittelpunktY) <= radius + kreis.radius;
    }

    public boolean beruehrtBildschirmRand() {
        return mittelpunktX - radius <= 0 || mittelpunktX + radius >= 1 || mittelpunktY - radius <= 0 || mittelpunktY + radius >= 1;
    }

    public boolean istWegVomBildschirm() {
        return mittelpunktX + radius <= 0 || mittelpunktX - radius >= 1 || mittelpunktY + radius <= 0 || mittelpunktY - radius >= 1;
    }
}