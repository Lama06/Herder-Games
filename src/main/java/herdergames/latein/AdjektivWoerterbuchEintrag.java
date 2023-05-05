package herdergames.latein;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

record AdjektivWoerterbuchEintrag(String ersteForm, Optional<String> zweiteForm, Optional<String> dritteForm) {
    static Optional<AdjektivWoerterbuchEintrag> parse(String text) {
        String[] elemente = text.split(" ");

        if (elemente.length == 1) {
            return Optional.of(new AdjektivWoerterbuchEintrag(elemente[0], Optional.empty(), Optional.empty()));
        }

        if (elemente.length == 2) {
            return Optional.of(new AdjektivWoerterbuchEintrag(elemente[0], Optional.of(elemente[1]), Optional.empty()));
        }

        if (elemente.length == 3) {
            return Optional.of(new AdjektivWoerterbuchEintrag(elemente[0], Optional.of(elemente[1]), Optional.of(elemente[2])));
        }

        return Optional.empty();
    }

    AdjektivWoerterbuchEintrag {
        Objects.requireNonNull(ersteForm);
        Objects.requireNonNull(zweiteForm);
        Objects.requireNonNull(dritteForm);
    }

    Optional<Adjektiv> zuAdjektiv() {
        return Stream.of(
                        AOAdjektivDeklination.INSTANCE,
                        KonsAdjektivDeklination.INSTANCE,
                        HicDeklination.INSTANCE,
                        IlleDeklination.INSTANCE,
                        IsDeklination.INSTANCE
                ).map(adjektivDeklination -> adjektivDeklination.parse(this))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny();
    }
}
