package herdergames.util;

public record Rechteck(float x, float y, float breite, float hoehe) {
    public boolean kollidiertMit(Rechteck anderes) {
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

    public boolean beruehrtBildschirmRand() {
        return x <= 0 || x + breite >= 1 || y <= 0 || y + hoehe >= 1;
    }

    public boolean istWegVomBildschirm() {
        return x + breite <= 0 || x >= 1 || y + hoehe <= 0 || y >= 1;
    }

    public float getXMitte() {
        return x + breite / 2;
    }

    public float getYMitte() {
        return y + hoehe / 2;
    }
}