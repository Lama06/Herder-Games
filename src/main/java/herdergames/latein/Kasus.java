package herdergames.latein;

enum Kasus {
    NOMINATIV("Nominativ"),
    GENITIV("Genitiv"),
    DATIV("Dativ"),
    AKKUSATIV("Akkusativ"),
    ABLATIV("Ablativ"),
    VOKATIV("Vokativ");

    private final String string;

    Kasus(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }
}
