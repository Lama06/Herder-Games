package herdergames.schach;

enum Figur {
    BAUER(1, 'b'),
    LAEUFER(3, 'l'),
    SPRINGER(3, 's'),
    TURM(5, 't'),
    DAME(8, 'd'),
    KOENIG(0, 'k');

    final int wert;
    final char buchstabe;

    Figur(int wert, char buchstabe) {
        this.wert = wert;
        this.buchstabe = buchstabe;
    }
}
