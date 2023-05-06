package herdergames.schach;

enum Figur {
    BAUER(1),
    LAEUFER(3),
    SPRINGER(3),
    TURM(5),
    DAME(8),
    KOENIG(0);

    final int wert;

    Figur(int wert) {
        this.wert = wert;
    }
}
