package herdergames.break_out;

import herdergames.spiel.MehrspielerSpiel;
import herdergames.spiel.Spieler;
import processing.core.PApplet;

import java.util.*;

public final class BreakOut extends MehrspielerSpiel {
    final List<Welt> welten = new ArrayList<>();
    private final List<Spieler.Id> rangliste = new ArrayList<>();

    public BreakOut(PApplet applet, Set<Spieler> alleSpieler) {
        super(applet);
        List<Spieler> spielerSortiert = alleSpieler.stream().sorted(Comparator.comparing(Spieler::id)).toList();
        for (Spieler spieler : spielerSortiert) {
            welten.add(new Welt(this, spieler));
        }
    }

    @Override
    public Optional<List<Spieler.Id>> draw() {
        applet.background(255);

        Iterator<Welt> weltIterator = welten.iterator();
        while (weltIterator.hasNext()) {
            Welt welt = weltIterator.next();
            if (welt.hatVerloren()) {
                weltIterator.remove();
                rangliste.add(0, welt.spieler.id());
                continue;
            }
            welt.draw();
        }

        if (welten.isEmpty()) {
            return Optional.of(rangliste);
        }

        return Optional.empty();
    }

    @Override
    public void keyPressed() {
        welten.forEach(Welt::keyPressed);
    }

    @Override
    public void keyReleased() {
        welten.forEach(Welt::keyReleased);
    }
}
