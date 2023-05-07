package herdergames.pacman;

import herdergames.spiel.MehrspielerSpiel;
import herdergames.spiel.Spieler;
import herdergames.util.Rechteck;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.util.*;

public final class PacmanSpiel extends MehrspielerSpiel {
    public static void init(PApplet applet) {
        spriteSheet = applet.loadImage("pacman/pacman.png");

        welt = spriteSheet.get(208, 225, 229, 252);

        welt.loadPixels();

        Set<Position> punktePositionen = new HashSet<>();
        Set<Position> superPunktePositionen = new HashSet<>();
        int punktFarbe = applet.color(255, 183, 174);
        int superPunktFarbe = applet.color(255, 242, 0);
        for (int x = 0; x < welt.width; x++) {
            for (int y = 0; y < welt.height; y++) {
                int pixelFarbe = welt.pixels[y*welt.width+x];
                if (pixelFarbe != punktFarbe && pixelFarbe != superPunktFarbe) {
                    continue;
                }

                welt.pixels[y*welt.width+x] = applet.color(0);
                welt.pixels[(y+1)*welt.width+x] = applet.color(0);
                welt.pixels[y*welt.width+x+1] = applet.color(0);
                welt.pixels[(y+1)*welt.width+x+1] = applet.color(0);

                if (pixelFarbe == punktFarbe) {
                    punktePositionen.add(new Position(x, y));
                } else {
                    superPunktePositionen.add(new Position(x, y));
                }
            }
        }
        PacmanSpiel.punktePositionen = Collections.unmodifiableSet(punktePositionen);
        PacmanSpiel.superPunktePositionen = Collections.unmodifiableSet(superPunktePositionen);

        welt.updatePixels();

        Set<Rechteck> mauerPositionen = new HashSet<>();
        int mauerFarbe = applet.color(33, 33, 255);
        for (int x = 0; x < welt.width; x++) {
            for (int y = 0; y < welt.height; y++) {
                if (welt.pixels[y*welt.width+x] != mauerFarbe) {
                    continue;
                }

                mauerPositionen.add(new Rechteck(x, y, 1, 1));
            }
        }
        PacmanSpiel.mauerPositionen = Collections.unmodifiableSet(mauerPositionen);
    }

    static final int SUPER_MODUS_ZEIT = 300;

    static PImage spriteSheet;
    static PImage welt;
    static Set<Position> punktePositionen;
    static Set<Position> superPunktePositionen;
    static Set<Rechteck> mauerPositionen;

    final List<Pacman> pacmans = new ArrayList<>();
    final List<Spieler.Id> totePacmans = new ArrayList<>();
    final List<Geist> geister = new ArrayList<>();
    final List<Punkt> punkte = new ArrayList<>();
    final List<SuperPunkt> superPunkte = new ArrayList<>();
    boolean superModus;
    int superModusVerbleibendeZeit;

    public PacmanSpiel(PApplet applet, Set<Spieler> alleSpieler) {
        super(applet);

        List<Spieler> spielerListe = new ArrayList<>(alleSpieler);
        int anzahlPacmans = 1;
        if (spielerListe.size() == 4) {
            anzahlPacmans = 2;
        }
        for (int i = 0; i < anzahlPacmans; i++) {
            Spieler spieler = spielerListe.remove(applet.choice(spielerListe.size()));
            pacmans.add(new Pacman(this, spieler));
        }
        for (Spieler spieler : spielerListe) {
            geister.add(new Geist(this, spieler));
        }

        for (Position punktPosition : punktePositionen) {
            punkte.add(new Punkt(this, punktPosition.x(), punktPosition.y()));
        }
        for (Position superPunktPosition : superPunktePositionen) {
            superPunkte.add(new SuperPunkt(this, superPunktPosition.x(), superPunktPosition.y()));
        }
    }

    private float getScale() {
        return Math.min((float) applet.width / (float) welt.width, (float) applet.height / (float) welt.height);
    }

    private float getWidth() {
        return getScale() * welt.width;
    }

    private float getHeight() {
        return getScale() * welt.height;
    }

    private float getAbstandHorizontal() {
        return ((float) applet.width - getWidth()) / 2;
    }

    private float getAbstandVertikal() {
        return ((float) applet.height - getHeight()) / 2;
    }

    private void tickSuperModus() {
        if (!superModus) {
            return;
        }

        superModusVerbleibendeZeit--;
        if (superModusVerbleibendeZeit <= 0) {
            superModus = false;
        }
    }

    @Override
    public Optional<List<Spieler.Id>> draw() {
        tickSuperModus();

        applet.background(0);

        applet.translate(getAbstandHorizontal(), getAbstandVertikal());
        applet.scale(getScale());

        applet.imageMode(PConstants.CORNER);
        applet.image(welt, 0, 0);

        for (Punkt punkt : punkte) {
            punkt.draw();
        }
        for (SuperPunkt superPunkt : superPunkte) {
            superPunkt.draw();
        }
        for (Pacman pacman : pacmans) {
            pacman.draw();
        }
        for (Geist geist : geister) {
            geist.draw();
        }

        if (punkte.isEmpty()) {
            List<Spieler.Id> rangliste = new ArrayList<>();
            for (Pacman pacman : pacmans) {
                rangliste.add(pacman.spieler.id());
            }
            rangliste.addAll(totePacmans);
            for (Geist geist : geister) {
                rangliste.add(geist.spieler.id());
            }
            return Optional.of(rangliste);
        }

        if (pacmans.isEmpty()) {
            List<Spieler.Id> rangliste = new ArrayList<>();
            for (Geist geist : geister) {
                rangliste.add(geist.spieler.id());
            }
            rangliste.addAll(totePacmans);
            return Optional.of(rangliste);
        }

        return Optional.empty();
    }

    @Override
    public void keyPressed() {
        for (Pacman pacman : pacmans) {
            pacman.keyPressed();
        }

        for (Geist geist : geister) {
            geist.keyPressed();
        }
    }

    @Override
    public void keyReleased() {
        for (Pacman pacman : pacmans) {
            pacman.keyReleased();
        }

        for (Geist geist : geister) {
            geist.keyReleased();
        }
    }
}
