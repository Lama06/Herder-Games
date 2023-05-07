package herdergames.break_out;

import processing.core.PApplet;

enum UpgradeType {
    SCHNELLER {
        @Override
        int getColor(PApplet applet) {
            return applet.color(255, 0, 0);
        }

        @Override
        void enable(Welt welt) {
            welt.aktivierteUpgrades.put(this, 10 * 60);
        }
    },
    KANONE {
        @Override
        int getColor(PApplet applet) {
            return applet.color(0);
        }

        @Override
        void enable(Welt welt) {
            welt.aktivierteUpgrades.put(this, 6 * 60);
        }
    };

    static UpgradeType zufaellig(PApplet applet) {
        return values()[applet.choice(values().length)];
    }

    abstract int getColor(PApplet applet);

    abstract void enable(Welt welt);
}
