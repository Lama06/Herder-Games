package herdergames.ai;

public interface Spieler<Self extends Spieler<Self>> {
    Self getGegner();
}
