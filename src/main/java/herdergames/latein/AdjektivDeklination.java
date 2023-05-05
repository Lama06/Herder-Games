package herdergames.latein;

import java.util.Optional;

abstract class AdjektivDeklination {
    abstract Optional<Adjektiv> parse(AdjektivWoerterbuchEintrag eintrag);
}
