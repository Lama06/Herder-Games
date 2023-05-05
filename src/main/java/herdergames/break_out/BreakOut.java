package herdergames.break_out;

import herdergames.spiel.Spieler;
import herdergames.util.PartikelManager;
import herdergames.util.Rechteck;
import herdergames.spiel.Spiel;
import herdergames.util.Steuerung;
import processing.core.PApplet;
import processing.core.PConstants;

import java.util.*;
import java.util.stream.Stream;

public final class BreakOut extends Spiel.Mehrspieler {
    private final List<Welt> welten = new ArrayList<>();
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

    private static final class Welt {
        private final BreakOut spiel;
        private final PApplet applet;
        private final Spieler spieler;
        private final List<Entity> entities = new ArrayList<>();
        private final Map<UpgradeType, Integer> aktivierteUpgrades = new EnumMap<>(UpgradeType.class);
        private final PartikelManager partikelManager;

        private Welt(BreakOut spiel, Spieler spieler) {
            this.spiel = spiel;
            this.spieler = spieler;
            applet = spiel.applet;
            partikelManager = new PartikelManager(applet);

            entities.add(new Platform(this));
            entities.add(new Ball(this));

            boolean geradeReihe = true;
            for (float y = 0; y+Stein.HOEHE < 0.4f; y += Stein.HOEHE + Stein.ABSTAND_Y, geradeReihe = !geradeReihe) {
                for (float x = geradeReihe ? Stein.ABSTAND_X : 0; x+Stein.BREITE < 1; x += Stein.BREITE + Stein.ABSTAND_X) {
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

                upgradeEintrag.setValue(upgradeEintrag.getValue()-1);
            }
        }

        private void draw() {
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

        private void keyPressed() {
            entities.forEach(Entity::keyPressed);
        }

        private void keyReleased() {
            entities.forEach(Entity::keyReleased);
        }

        private int getIndex() {
            return spiel.welten.indexOf(this);
        }

        private float getBreite() {
            return 1f / spiel.welten.size();
        }

        private float getXStart() {
            return getIndex() * getBreite();
        }

        private boolean hatVerloren() {
            return requireEntity(Ball.class).istUntenRaus();
        }

        @SuppressWarnings("unchecked")
        private <T> Stream<T> getEntites(Class<T> type) {
            return entities.stream()
                    .filter(entity -> type == entity.getClass())
                    .map(entity -> (T) entity);
        }

        private <T> Optional<T> getEntity(Class<T> type) {
            return getEntites(type).findAny();
        }

        private <T> T requireEntity(Class<T> type) {
            return getEntity(type).orElseThrow();
        }
    }

    private static abstract class Entity {
        final Welt welt;
        final PApplet applet;

        private Entity(Welt welt) {
            this.welt = welt;
            applet = welt.applet;
        }

        abstract List<Runnable> draw();

        void keyPressed() { }

        void keyReleased() { }
    }

    private static final class Ball extends Entity {
        private static final float SIZE = 0.02f;
        private static final float GESCHWINDIGKEIT_Y = 0.006f;
        private static final float MAX_GESCHWINDIGKEIT_X = 0.004f;

        private float geschwindigkeitY = GESCHWINDIGKEIT_Y;
        private float geschwindigkeitX = 0;
        private float x = 0.5f;
        private float y = 0.5f;

        private Ball(Welt welt) {
            super(welt);
        }

        private boolean istUntenRaus() {
            return y >= 1;
        }

        private boolean kollidiertMitRandOben() {
            return y <= 0;
        }

        private boolean kollidiertMitRandLinksRechts() {
            return x <= 0 || x+SIZE >= 1;
        }

        private boolean kollidiertMitRand() {
            return kollidiertMitRandOben() || kollidiertMitRandLinksRechts();
        }

        private Optional<Platform> getKollidierendePlatform() {
            return welt.getEntites(Platform.class)
                    .filter((platform) -> platform.getRechteck().kollidiertMit(getRechteck()))
                    .findAny();
        }

        private Optional<Stein> getKollidierendenStein() {
            return welt.getEntites(Stein.class)
                    .filter(stein -> stein.getRechteck().kollidiertMit(getRechteck()))
                    .findAny();
        }

        private void vonRandAbprallen() {
            if (kollidiertMitRandOben()) {
                geschwindigkeitY *= -1;
            }
            if (kollidiertMitRandLinksRechts()) {
                geschwindigkeitX *= -1;
            }
        }

        private void vonPlatformAbprallen(Platform platform) {
            Rechteck platformRechteck = platform.getRechteck();

            float platformXMitte = platformRechteck.getXMitte();
            float neueGeschwindigkeitX = MAX_GESCHWINDIGKEIT_X * (Math.abs(getRechteck().getXMitte() - platformXMitte) / (platformRechteck.breite/2));
            if (getRechteck().getXMitte() < platformXMitte) {
                neueGeschwindigkeitX *= -1;
            }

            geschwindigkeitX = neueGeschwindigkeitX;
            geschwindigkeitY *= -1;
        }

        private List<Runnable> vonSteinAbprallen(Stein stein) {
            geschwindigkeitY *= -1;

            welt.partikelManager.spawnPartikel(
                    welt.getXStart() + x*welt.getBreite(),
                    y,
                    50
            );

            return List.of(() -> {
                welt.entities.remove(stein);
                stein.upgrade.ifPresent(upgradeType -> welt.entities.add(new FallendesUpgrade(welt, upgradeType, stein.x, stein.y)));
            });
        }

        private List<Runnable> bewegen() {
            boolean kollidiertMitRandVorher = kollidiertMitRand();
            Optional<Platform> kollidierendePlatformVorher = getKollidierendePlatform();
            Optional<Stein> kollidierenderSteinVorher = getKollidierendenStein();

            x += geschwindigkeitX;
            y += geschwindigkeitY;

            boolean kollidiertMitRandNachher = kollidiertMitRand();
            Optional<Platform> kollidierendePlatformNachher = getKollidierendePlatform();
            Optional<Stein> kollidierenderSteinNachher = getKollidierendenStein();

            if (!kollidiertMitRandVorher && kollidiertMitRandNachher) {
                vonRandAbprallen();
            }

            if (kollidierendePlatformVorher.isEmpty() && kollidierendePlatformNachher.isPresent()) {
                vonPlatformAbprallen(kollidierendePlatformNachher.get());
            }

            if (kollidierenderSteinVorher.isEmpty() && kollidierenderSteinNachher.isPresent()) {
                return vonSteinAbprallen(kollidierenderSteinNachher.get());
            }

            return Collections.emptyList();
        }

        @Override
        List<Runnable> draw() {
            applet.ellipseMode(PConstants.CORNER);
            applet.noStroke();
            applet.fill(applet.color(255, 0, 0));
            float size = Math.min(SIZE * welt.getBreite() * applet.width, SIZE * applet.height);
            applet.circle(
                    (welt.getXStart() + x*welt.getBreite()) * applet.width,
                    y * applet.height,
                    size
            );

            return bewegen();
        }

        private Rechteck getRechteck() {
            return new Rechteck(x, y, SIZE, SIZE);
        }
    }

    private static final class Platform extends Entity {
        private static final float BREITE = 0.1f;
        private static final float HOEHE = 0.02f;
        private static final float BEWEGUNGS_GESCHWINDIGKEIT = 0.006f;
        private static final float Y = 1-HOEHE*2;

        private final Steuerung steuerung;
        private float x = 0.5f-BREITE/2;

        private Platform(Welt welt) {
            super(welt);
            steuerung = new Steuerung(applet, welt.spieler.id());
        }

        private void bewegen() {
            float geschwindigkeit = (welt.aktivierteUpgrades.containsKey(UpgradeType.SCHNELLER) ? 2 : 1) * BEWEGUNGS_GESCHWINDIGKEIT;
            if (steuerung.istLinksGedrueckt() && x > 0) {
                x -= geschwindigkeit;
            }
            if (steuerung.istRechtsGedrueckt() && x+BREITE < 1) {
                x += geschwindigkeit;
            }
        }

        private List<Runnable> upgradesFangen() {
            return welt.getEntites(FallendesUpgrade.class)
                    .filter(fallendesUpgrade -> fallendesUpgrade.getRechteck().kollidiertMit(getRechteck()))
                    .<Runnable>map(gefangenesUpgrade -> () -> {
                        gefangenesUpgrade.upgrade.enable(welt);
                        welt.entities.remove(gefangenesUpgrade);
                    })
                    .toList();
        }

        private List<Runnable> kanonenKugelnSpawnen() {
            if (applet.frameCount % 60 != 0) {
                return Collections.emptyList();
            }

            if (!welt.aktivierteUpgrades.containsKey(UpgradeType.KANONE)) {
                return Collections.emptyList();
            }

            return List.of(() -> welt.entities.add(new KanonenKugel(welt, x + BREITE/2)));
        }

        @Override
        List<Runnable> draw() {
            bewegen();

            applet.rectMode(PConstants.CORNER);
            applet.noStroke();
            applet.fill(applet.color(0, 0, 255));
            applet.rect(
                    (welt.getXStart() + x*welt.getBreite()) * applet.width,
                    Y * applet.height,
                    BREITE * welt.getBreite() * applet.width,
                    HOEHE * applet.height
            );

            return Stream.of(upgradesFangen(), kanonenKugelnSpawnen()).flatMap(List::stream).toList();
        }

        @Override
        void keyPressed() {
            steuerung.keyPressed();
        }

        @Override
        void keyReleased() {
            steuerung.keyReleased();
        }

        private Rechteck getRechteck() {
            return new Rechteck(x, Y, BREITE, HOEHE);
        }
    }

    private static final class Stein extends Entity {
        private static final float BREITE = 0.05f;
        private static final float HOEHE = 0.03f;
        private static final float ABSTAND_X = BREITE / 2;
        private static final float ABSTAND_Y = HOEHE / 2;
        private static final float UPGRADE_WAHRSCHEINLICHKEIT = 0.2f;

        private final float x;
        private final float y;
        private final Optional<UpgradeType> upgrade;

        private Stein(Welt welt, float x, float y) {
            super(welt);
            this.x = x;
            this.y = y;
            if (applet.random(1) <= UPGRADE_WAHRSCHEINLICHKEIT) {
                upgrade = Optional.of(UpgradeType.zufaellig(applet));
            } else {
                upgrade = Optional.empty();
            }
        }

        @Override
        List<Runnable> draw() {
            applet.rectMode(PConstants.CORNER);
            applet.noStroke();
            applet.fill(upgrade.map(upgrade -> upgrade.getColor(applet)).orElse(applet.color(79, 6, 71)));
            applet.rect(
                    (welt.getXStart() + x*welt.getBreite()) * applet.width,
                    y * applet.height,
                    BREITE * welt.getBreite() * applet.width,
                    HOEHE * applet.height
            );

            return Collections.emptyList();
        }

        private Rechteck getRechteck() {
            return new Rechteck(x, y, BREITE, HOEHE);
        }
    }

    private enum UpgradeType {
        SCHNELLER {
            @Override
            int getColor(PApplet applet) {
                return applet.color(255, 0, 0);
            }

            @Override
            void enable(Welt welt) {
                welt.aktivierteUpgrades.put(this, 6*60);
            }
        },
        KANONE {
            @Override
            int getColor(PApplet applet) {
                return applet.color(0);
            }

            @Override
            void enable(Welt welt) {
                welt.aktivierteUpgrades.put(this, 6*60);
            }
        };

        private static UpgradeType zufaellig(PApplet applet) {
            return values()[applet.choice(values().length)];
        }

        abstract int getColor(PApplet applet);

        abstract void enable(Welt welt);
    }

    private static final class FallendesUpgrade extends Entity {
        private static final float SIZE = 0.02f;
        private static final float Y_GESCHWINDIGKEIT = 0.002f;

        private final UpgradeType upgrade;
        private final float x;
        private float y;

        private FallendesUpgrade(Welt welt, UpgradeType upgrade, float x, float y) {
            super(welt);
            this.upgrade = upgrade;
            this.x = x;
            this.y = y;
        }

        private void bewegen() {
            y += Y_GESCHWINDIGKEIT;
        }

        @Override
        List<Runnable> draw() {
            bewegen();

            applet.ellipseMode(PConstants.CORNER);
            applet.noStroke();
            applet.fill(upgrade.getColor(applet));
            float size = Math.min(SIZE * welt.getBreite() * applet.width, SIZE * applet.height);
            applet.circle(
                    (welt.getXStart() + x*welt.getBreite()) * applet.width,
                    y * applet.height,
                    size
            );

            if (y >= 1) {
                return List.of(() -> welt.entities.remove(this));
            }
            return Collections.emptyList();
        }

        private Rechteck getRechteck() {
            return new Rechteck(x, y, SIZE, SIZE);
        }
    }

    private static final class KanonenKugel extends Entity {
        private static final float SIZE = 0.02f;
        private static final float Y_GESCHWINDIGKEIT = -0.015f;

        private final float x;
        private float y = Platform.Y;

        private KanonenKugel(Welt welt, float x) {
            super(welt);
            this.x = x;
        }

        @Override
        List<Runnable> draw() {
            y += Y_GESCHWINDIGKEIT;

            applet.ellipseMode(PConstants.CORNER);
            applet.noStroke();
            applet.fill(0);
            float size = Math.min(SIZE * welt.getBreite() * applet.width, SIZE * applet.height);
            applet.circle(
                    (welt.getXStart() + x*welt.getBreite()) * applet.width,
                    y * applet.height,
                    size
            );

            return welt.getEntites(Stein.class)
                    .filter(stein -> stein.getRechteck().kollidiertMit(getRechteck()))
                    .findAny()
                    .map(stein -> List.<Runnable>of(() -> {
                        welt.entities.remove(stein);
                        welt.entities.remove(this);
                    }))
                    .orElse(Collections.emptyList());
        }

        private Rechteck getRechteck() {
            return new Rechteck(x, y, SIZE, SIZE);
        }
    }
}
