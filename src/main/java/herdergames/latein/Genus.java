package herdergames.latein;

import java.util.Optional;

enum Genus {
    MASKULINUM,
    FEMININUM,
    NEUTRUM;

    static Optional<Genus> parse(char buchstabe) {
        return switch (buchstabe) {
            case 'm' -> Optional.of(MASKULINUM);
            case 'f' -> Optional.of(FEMININUM);
            case 'n' -> Optional.of(NEUTRUM);
            default -> Optional.empty();
        };
    }
}
