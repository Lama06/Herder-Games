package herdergames.latein;

enum Numerus {
    SINGULAR("Singular"),
    PLURAL("Plural");

    private final String string;

    Numerus(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }
}
