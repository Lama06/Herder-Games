package herdergames.ampel;

import processing.core.PApplet;

sealed interface AmpelPhase {
    int getLaengeSekunden();

    default int getLaengeFrames() {
        return getLaengeSekunden() * 60;
    }

    int getFarbe(PApplet applet);

    AmpelPhase getNaechstePhase();

    record Gruen() implements AmpelPhase {
        static final Gruen INSTANCE = new Gruen();

        @Override
        public int getLaengeSekunden() {
            return 2;
        }

        @Override
        public int getFarbe(PApplet applet) {
            return applet.color(0, 255, 0);
        }

        @Override
        public AmpelPhase getNaechstePhase() {
            return new Gelb(this);
        }
    }

    record Gelb(AmpelPhase vorher) implements AmpelPhase {
        public Gelb {
            if (vorher instanceof Gelb) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public int getLaengeSekunden() {
            return vorher instanceof Rot ? 2 : 1;
        }

        @Override
        public int getFarbe(PApplet applet) {
            return applet.color(252, 186, 3);
        }

        @Override
        public AmpelPhase getNaechstePhase() {
            if (vorher instanceof Gruen) {
                return Rot.INSTANCE;
            }
            if (vorher instanceof Rot) {
                return Gruen.INSTANCE;
            }
            throw new IllegalStateException();
        }
    }

    record Rot() implements AmpelPhase {
        static final Rot INSTANCE = new Rot();

        @Override
        public int getLaengeSekunden() {
            return 2;
        }

        @Override
        public int getFarbe(PApplet applet) {
            return applet.color(255, 0, 0);
        }

        @Override
        public AmpelPhase getNaechstePhase() {
            return new Gelb(this);
        }
    }
}