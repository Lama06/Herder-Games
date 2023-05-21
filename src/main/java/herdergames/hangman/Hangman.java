package herdergames.hangman;

import herdergames.spiel.EinzelspielerSpiel;
import herdergames.spiel.Spieler;
import processing.core.PApplet;
import processing.core.PConstants;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class Hangman extends EinzelspielerSpiel {
    private static final List<String> WOERTER = List.of(
            "Sanguini",
            "Harry Potter",
            "Sirius Black",
            "Remus Lupin",
            "Hermine Granger",
            "James Potter",
            "Lily Potter",
            "Regulus Black",
            "Orion Black",
            "Walburga Black",
            "Hope Lupin",
            "Lyall Lupin",
            "Peter Pettrigrew",
            "Tom Riddle",
            "Voldemort",
            "Albus Dumbledore",
            "Phineas Nigellus Black",
            "Horace Slughorn",
            "Severus Snape",
            "Minerva McGonagall",
            "Gellert Grindelwald",
            "Rita Kimmkorn"
    );

    private final List<Runnable> HANGMAN_SCHRITTE = List.of(
            () -> {
                // Hügel
                applet.ellipseMode(PConstants.CENTER);
                applet.strokeWeight(0.01f);
                applet.stroke(0);
                applet.fill(0, 200, 0);
                applet.arc(
                        0.5f,
                        1f,
                        0.5f,
                        1f / 3f,
                        PApplet.radians(180),
                        PApplet.radians(360)
                );
            },
            () -> {
                // Galgen
                applet.stroke(0);
                applet.strokeWeight(0.01f);
                applet.line(0.5f, 5f / 6f, 0.5f, 0f);
            },
            () -> {
                applet.stroke(0);
                applet.strokeWeight(0.01f);
                applet.line(0.5f, 0, 0.2f, 0);
            },
            () -> {
                applet.ellipseMode(PConstants.CENTER);
                applet.strokeWeight(0.01f);
                applet.stroke(0);
                applet.noFill();
                applet.arc(
                        0.5f,
                        0,
                        0.1f,
                        0.1f,
                        PApplet.radians(90),
                        PApplet.radians(180)
                );
            },
            () -> {
                applet.stroke(0);
                applet.strokeWeight(0.01f);
                applet.line(0.2f, 0f, 0.2f, 0.2f);
            },
            () -> {
                // Kopf
                applet.ellipseMode(PConstants.CENTER);
                applet.strokeWeight(0.01f);
                applet.stroke(0f);
                applet.circle(0.2f, 0.225f, 0.05f);
            },
            () -> {
                // Bauch
                applet.stroke(0);
                applet.strokeWeight(0.015f);
                applet.line(0.2f, 0.25f, 0.2f, 0.5f);
            },
            () -> {
                // Bein links
                applet.stroke(0);
                applet.strokeWeight(0.0125f);
                applet.line(0.2f, 0.5f, 0.1f, 0.65f);
            },
            () -> {
                // Bein rechts
                applet.stroke(0);
                applet.strokeWeight(0.0125f);
                applet.line(0.2f, 0.5f, 0.3f, 0.65f);
            },
            () -> {
                // Arm links
                applet.stroke(0);
                applet.strokeWeight(0.01f);
                applet.line(0.2f, 0.35f, 0.1f, 0.5f);
            },
            () -> {
                // Arm rechts
                applet.stroke(0);
                applet.strokeWeight(0.01f);
                applet.line(0.2f, 0.35f, 0.3f, 0.5f);
            }
    );

    private final String wort;
    private final Set<Character> entdeckteBuchstaben = new HashSet<>();

    public Hangman(PApplet applet, Spieler spieler) {
        super(applet);
        wort = WOERTER.get(applet.choice(WOERTER.size()));
    }

    private int fehlerZaehlen() {
        return (int) entdeckteBuchstaben.stream()
                .filter(buchstabe -> !wort.contains(Character.toString(Character.toLowerCase(buchstabe))))
                .filter(buchstabe -> !wort.contains(Character.toString(Character.toUpperCase(buchstabe))))
                .count();
    }

    private boolean gewonnen() {
        return wort.chars()
                .allMatch(buchstabe -> buchstabe == ' ' || entdeckteBuchstaben.contains(Character.toLowerCase((char) buchstabe)));
    }

    private void drawWort() {
        String angezeigtesWort = wort.chars()
                .map(buchstabe -> buchstabe == ' ' || entdeckteBuchstaben.contains(Character.toLowerCase((char) buchstabe)) ? buchstabe : '_')
                .mapToObj(Character::toString)
                .collect(Collectors.joining());

        applet.fill(0);
        applet.textSize(0.1f * applet.height);
        float breite = applet.textWidth(angezeigtesWort);
        float x = applet.width / 2f - breite / 2f;
        for (int buchstabeIndex = 0; buchstabeIndex < wort.length(); buchstabeIndex++) {
            char buchstabe = angezeigtesWort.charAt(buchstabeIndex);
            applet.text(buchstabe, x, 0.1f * applet.height);
            x += applet.textWidth(buchstabe);
        }
    }

    private void drawHangman() {
        float size = 0.8f * applet.height;
        float x = 0.5f * applet.width - size / 2f;
        float y = 0.2f * applet.height;

        applet.pushMatrix();
        applet.translate(x, y);
        applet.scale(size, size);

        int anzahlFehler = fehlerZaehlen();
        for (int i = 0; i < anzahlFehler; i++) {
            HANGMAN_SCHRITTE.get(i).run();
        }

        applet.popMatrix();
    }

    @Override
    public Optional<Ergebnis> draw() {
        if (fehlerZaehlen() > HANGMAN_SCHRITTE.size()) {
            return Optional.of(Ergebnis.VERLOREN);
        }
        if (gewonnen()) {
            return Optional.of(Ergebnis.GEWONNEN);
        }

        applet.background(255);

        drawWort();
        drawHangman();

        return Optional.empty();
    }

    @Override
    public void keyPressed() {
        if (applet.key == PConstants.CODED || applet.key == ' ') {
            return;
        }

        entdeckteBuchstaben.add(Character.toLowerCase(applet.key));
    }
}
