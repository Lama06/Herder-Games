import java.util.Objects;

final class Rechteck {
    final float x;
    final float y;
    final float breite;
    final float hoehe;

    Rechteck(float x, float y, float breite, float hoehe) {
        this.x = x;
        this.y = y;
        this.breite = breite;
        this.hoehe = hoehe;
    }

    boolean kollidiertMit(Rechteck anderes) {
        float thisMinX = x;
        float thisMaxX = x + breite;
        float anderesMinX = anderes.x;
        float anderesMaxX = anderes.x + anderes.breite;

        float thisMinY = y;
        float thisMaxY = y + hoehe;
        float anderesMinY = anderes.y;
        float anderesMaxY = anderes.y + anderes.hoehe;

        if (thisMaxX < anderesMinX || thisMinX > anderesMaxX) {
            return false;
        }

        if (thisMaxY < anderesMinY || thisMinY > anderesMaxY) {
            return false;
        }

        return true;
    }

    boolean beruehrtBildschirmRand() {
        return x <= 0 || x+breite >= 1 || y <= 0 || y+hoehe >= 1;
    }

    boolean istWegVomBildschirm() {
        return x+breite <= 0 || x >= 1 || y+hoehe <= 0 || y >= 1;
    }

    float getXMitte() {
        return x + breite/2;
    }

    float getYMitte() {
        return y + hoehe/2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rechteck rechteck = (Rechteck) o;
        return Float.compare(rechteck.x, x) == 0 && Float.compare(rechteck.y, y) == 0 && Float.compare(rechteck.breite, breite) == 0 && Float.compare(rechteck.hoehe, hoehe) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, breite, hoehe);
    }
}