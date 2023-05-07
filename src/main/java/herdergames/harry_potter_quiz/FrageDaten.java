package herdergames.harry_potter_quiz;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

record FrageDaten(String frage, String richtigeAntwort, List<String> falscheAntworten) {
    static final int ANTWORTEN = 4;

    static List<FrageDaten> loadFragen(PApplet applet, String path) {
        String[] zeilen = applet.loadStrings(path);
        List<FrageDaten> result = new ArrayList<>();
        for (int zeile = 0; zeile < zeilen.length; ) {
            String frage = zeilen[zeile++];
            String richtigeAntwort = zeilen[zeile++];
            List<String> falscheAntworten = new ArrayList<>();
            while (true) {
                if (zeile >= zeilen.length) {
                    break;
                }
                String falscheAntwort = zeilen[zeile++];
                if (falscheAntwort.isEmpty()) {
                    break;
                }
                falscheAntworten.add(falscheAntwort);
            }
            result.add(new FrageDaten(frage, richtigeAntwort, falscheAntworten));
        }
        return result;
    }

    FrageDaten(String frage, String richtigeAntwort, List<String> falscheAntworten) {
        if (falscheAntworten.size() != ANTWORTEN - 1) {
            throw new IllegalArgumentException();
        }

        this.frage = Objects.requireNonNull(frage);
        this.richtigeAntwort = Objects.requireNonNull(richtigeAntwort);
        this.falscheAntworten = List.copyOf(falscheAntworten);
    }
}
