package herdergames.break_out;

import herdergames.spiel.Spieler;
import herdergames.util.PartikelManager;
import processing.core.PApplet;
import processing.core.PConstants;

import java.util.*;
import java.util.stream.Stream;

final class Welt {
    private final BreakOut spiel;
    final PApplet applet;
    final Spieler spieler;
    final List<Entity> entities = new ArrayList<>();
    final Map<UpgradeType, Integer> aktivierteUpgrades = new EnumMap<>(UpgradeType.class);
    final PartikelManager partikelManager;

    Welt(BreakOut spiel, Spieler spieler) {
        this.spiel = spiel;
        this.spieler = spieler;
        applet = spiel.applet;
        partikelManager = new PartikelManager(applet);

        entities.add(new Platform(this));
        entities.add(new Ball(this));

        boolean geradeReihe = true;
        for (float y = 0; y + Stein.HOEHE < 0.4f; y += Stein.HOEHE + Stein.ABSTAND_Y, geradeReihe = !geradeReihe) {
            for (float x = geradeReihe ? Stein.ABSTAND_X : 0; x + Stein.BREITE < 1; x += Stein.BREITE + Stein.ABSTAND_X) {
                entities.add(new Stein(this, x, y));
            }
        }
    }

    private void umrissMalen() {
        applet.noFill();
        applet.stroke(0);
        applet.strokeWeight(4);
        applet.rectMode(PConstants.CORNER);
        applet.rect(getXStart() * applet.width, 0, getBreite() * applet.width, applet.height);
    }

    private void upgradesTicken() {
        Iterator<Map.Entry<UpgradeType, Integer>> upgradeIterator = aktivierteUpgrades.entrySet().iterator();
        while (upgradeIterator.hasNext()) {
            Map.Entry<UpgradeType, Integer> upgradeEintrag = upgradeIterator.next();

            if (upgradeEintrag.getValue() == 0) {
                upgradeIterator.remove();
                continue;
            }

            upgradeEintrag.setValue(upgradeEintrag.getValue() - 1);
        }
    }

    void draw() {
        upgradesTicken();

        partikelManager.draw();

        List<Runnable> nachDrawCallbacks = new ArrayList<>();
        for (Entity entity : entities) {
            nachDrawCallbacks.addAll(entity.draw());
        }
        for (Runnable nachDrawCallback : nachDrawCallbacks) {
            nachDrawCallback.run();
        }

        umrissMalen();
    }

    void keyPressed() {
        entities.forEach(Entity::keyPressed);
    }

    void keyReleased() {
        entities.forEach(Entity::keyReleased);
    }

    private int getIndex() {
        return spiel.welten.indexOf(this);
    }

    float getBreite() {
        return 1f / spiel.welten.size();
    }

    float getXStart() {
        return getIndex() * getBreite();
    }

    boolean hatVerloren() {
        return requireEntity(Ball.class).istUntenRaus();
    }

    @SuppressWarnings("unchecked")
    <T> Stream<T> getEntites(Class<T> type) {
        return entities.stream()
                .filter(entity -> type == entity.getClass())
                .map(entity -> (T) entity);
    }

    <T> Optional<T> getEntity(Class<T> type) {
        return getEntites(type).findAny();
    }

    <T> T requireEntity(Class<T> type) {
        return getEntity(type).orElseThrow();
    }
}
