import processing.core.PApplet;

import java.util.Objects;

final class Kreis {
    final float mittelpunktX;
    final float mittelpunktY;
    final float radius;

    Kreis(float mittelpunktX, float mittelpunktY, float radius) {
        this.mittelpunktX = mittelpunktX;
        this.mittelpunktY = mittelpunktY;
        this.radius = radius;
    }

    boolean kollidiertMit(Kreis kreis) {
        return PApplet.dist(mittelpunktX, mittelpunktY, kreis.mittelpunktX, kreis.mittelpunktY) <= radius + kreis.radius;
    }

    boolean beruehrtBildschirmRand() {
        return mittelpunktX-radius <= 0 || mittelpunktX+radius >= 1 || mittelpunktY-radius <= 0 || mittelpunktY+radius >= 1;
    }

    boolean istWegVomBildschirm() {
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