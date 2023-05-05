package herdergames.latein;

import java.util.Map;
import java.util.Optional;

abstract class StammNomenDeklination extends NomenDeklination {
    abstract boolean istGenitivNotwendig();

    abstract Map<Numerus, Map<Kasus, String>> getEndungen();

    Optional<String> getStammAbhaengigeEndung(Numerus numerus, Kasus kasus, String stamm) {
        return Optional.empty();
    }

    @Override
    Optional<Nomen> parseImpl(String nominativ, Optional<String> genitiv, Genus genus) {
        String nominativEndung = getEndungen().get(Numerus.SINGULAR).get(Kasus.NOMINATIV);
        String genitivEndung = getEndungen().get(Numerus.SINGULAR).get(Kasus.GENITIV);

        if (!nominativ.endsWith(nominativEndung)) {
            return Optional.empty();
        }
        String stamm = nominativ.substring(0, nominativ.length() - nominativEndung.length());

        if (istGenitivNotwendig() && genitiv.isEmpty()) {
            return Optional.empty();
        }
        if (genitiv.isPresent() && !genitiv.get().equals(stamm + genitivEndung)) {
            return Optional.empty();
        }

        return Optional.of(new StammNomen(genus, this, stamm));
    }
}
