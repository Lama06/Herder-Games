package herdergames.perfektion;

enum ImperfektionType {
    BEWEGEN(BewegenImperfektion.FACTORY),
    ROTIEREN(RotierenImperfektion.FACTORY),
    FARBE(FarbeImperfektion.FACTORY);

    final ImperfektionFactory factory;

    ImperfektionType(ImperfektionFactory factory) {
        this.factory = factory;
    }
}
