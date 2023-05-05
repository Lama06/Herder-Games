package herdergames.util;

import processing.core.PApplet;

import java.util.Objects;

public final class Kreis {
    public final float mittelpunktX;
    public final float mittelpunktY;
    public final float radius;

    public Kreis(float mittelpunktX, float mittelpunktY, float radius) {
        this.mittelpunktX = mittelpunktX;
        this.mittelpunktY = mittelpunktY;
        this.radius = radius;
    }

    public boolean kollidiertMit(Kreis kreis) {
        return PApplet.dist(mittelpunktX, mittelpunktY, kreis.mittelpunktX, kreis.mittelpunktY) <= radius + kreis.radius;
    }

    public boolean beruehrtBildschirmRand() {
        return mittelpunktX-radius <= 0 || mittelpunktX+radius >= 1 || mittelpunktY-radius <= 0 || mittelpunktY+radius >= 1;
    }

    public boolean istWegVomBildschirm() {
        return mittelpunktX+radius <= 0 || mittelpunktX-radius >= 1 || mittelpunktY+radius <= 0 || mittelpunktY-radius >= 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kreis kreis = (Kreis) o;
        return Float.compare(kreis.mittelpunktX, mittelpunktX) == 0 && Float.compare(kreis.mittelpunktY, mittelpunktY) == 0 && Float.compare(kreis.radius, radius) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mittelpunktX, mittelpunktY, radius);
    }
}