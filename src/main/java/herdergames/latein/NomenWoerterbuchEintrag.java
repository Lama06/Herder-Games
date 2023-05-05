package herdergames.latein;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

record NomenWoerterbuchEintrag(String nominativ, Optional<String> genitiv, Optional<Genus> genus) {
    static Optional<NomenWoerterbuchEintrag> parse(String text) {
        String[] elemente = text.split(" ");

        if (elemente.length == 1) {
            return Optional.of(new NomenWoerterbuchEintrag(elemente[0], Optional.empty(), Optional.empty()));
        }

        if (elemente.length == 2) {
            return Optional.of(new NomenWoerterbuchEintrag(elemente[0], Optional.of(elemente[1]), Optional.empty()));
        }

        if (elemente.length == 3) {
            if (elemente[2].length() != 1) {
                return Optional.empty();
            }
            char genusBuchstabe = elemente[2].charAt(0);
            Optional<Genus> genus = Genus.parse(genusBuchstabe);
            if (genus.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(new NomenWoerterbuchEintrag(elemente[0], Optional.of(elemente[1]), genus));
        }

        return Optional.empty();
    }

    NomenWoerterbuchEintrag {
        Objects.requireNonNull(nominativ);
        Objects.requireNonNull(genitiv);
        Objects.requireNonNull(genus);
    }

    Optional<Nomen> zuNomen() {
        return Stream.of(
                        KonsNomenDeklinationMF.INSTANCE,
                        KonsNomenDeklinationN.INSTANCE,
                        ONomenDeklinationMF.INSTANCE,
                        ONomenDeklinationN.INSTANCE,
                        ANomenDeklination.INSTANCE,
                        UNomenDeklinations.INSTANCE,
                        ENomenDeklination.INSTANCE
                ).map(nomenDeklination -> nomenDeklination.parse(this))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny();
    }
}
