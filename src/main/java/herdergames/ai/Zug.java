package herdergames.ai;

public interface Zug<B extends Brett<B, ?, ?>> {
    B ergebnis();
}
